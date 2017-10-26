package com.sy.threestones;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author KimHyonh
 */
public class ThreeStonesServerGame {
    
    private final Logger log = LoggerFactory.getLogger(
            this.getClass().getName());
    
    private ThreeStonesBoard board;

    public ThreeStonesServerGame() {
    }
    
    public void playGame(ThreeStonesPacket packet) throws IOException {        

        log.info("Server Game - playGame");
                    
        board = new ThreeStonesBoard(11);
        board.fillBoardFromCSV("src/main/resources/board.csv");
        
        List<Stone> stones = new ArrayList<>();
        final int TOTAL_STONE = 15;
        
        int playerScore = 0;
        int computerScore = 0;
        while(stones.size() < TOTAL_STONE){
            packet.receivePacket();
            Stone playerStone = packet.getStone();
            playerStone.setType(PlayerType.PLAYER);
            
            log.info("playerStone : " + playerStone.toString());
            
            List<Tile> playerPlayableSlots = new ArrayList<>();
            if(stones.size() > 0)
                playerPlayableSlots = board.getPlayableSlot(stones.get(stones.size() - 1));
            
                        
            if(!board.placeStone(playerStone) || !isInPlayableSlots(playerPlayableSlots, playerStone)) {
                packet.sendPacket(null, Opcode.NOT_VALID_PLACE, playerScore, computerScore);
                
                // debug player playableSlots
//                for(Tile t : playerPlayableSlots) {             
//                    log.debug("Player playable slots : " + t.toString());   
//                }
                continue;
            }
            
            playerScore += countPointForAPosition(playerStone, playerStone.getType());
            log.debug("player slot : " + board.getBoard()[playerStone.getX()][playerStone.getY()]);
            
            List<Tile> computerPlayableSlots = board.getPlayableSlot(playerStone);
            Stone stone = determineNextMove(computerPlayableSlots);
            stone.setType(PlayerType.COMPUTER);
            if(board.placeStone(stone)) {
                computerScore += countPointForAPosition(stone, stone.getType());
                packet.sendPacket(stone, Opcode.SERVER_PLACE, playerScore, computerScore);
                stones.add(stone);    
            }
            
            log.debug("computer slot : " + board.getBoard()[stone.getX()][stone.getY()]);
            log.debug("#stones : " + stones.size());
            
            log.info("playerScore : " + playerScore);
            log.info("computerScore : " + computerScore);
        }
    }
    
    private boolean isInPlayableSlots(List<Tile> playableSlots, Stone stone) {
        if(playableSlots.size() == 0) 
            return true;
        
        for(Tile tile : playableSlots) {
            if(stone.getX() == tile.getX() && stone.getY() == tile.getY())
                return true;
        }
        
        return false;
    }
    
    public Stone determineNextMove(List<Tile> playableTiles) {
        log.info("Server Game - determineNextMove");
        
        Stone stone =  null;
        
        int point = 0;
        for(Tile tile : playableTiles) {
            int newPoint = countPointForAPosition(tile, PlayerType.COMPUTER);
            if(newPoint > point){                 
                point = newPoint;
                Slot slot = (Slot) tile;
                stone = new Stone(slot.getX(), slot.getY(), PlayerType.COMPUTER);
            }            
        }
        
        if(point == 0) {
            int random = (int) (Math.random() * playableTiles.size());
            Slot slot = (Slot) playableTiles.get(random);
            stone = new Stone(slot.getX(), slot.getY(), PlayerType.COMPUTER);
        }
        
        log.info("nextMove : " + stone.toString());
        return stone;
    } 
    
    public int countPointForAPosition(Tile tile, PlayerType type) {
        log.info("input Tile " + tile.toString());
      
        int topStones = numStones(getTopTiles(tile), type);
        int bottomStones = numStones(getBottomTiles(tile), type);
        
        int verticalPoint = calculatePoint(topStones + bottomStones + 1);
        
        int leftStones = numStones(getLeftTiles(tile), type);
        int rightStones = numStones(getRightTiles(tile), type);
        
        int herizontalPoint = calculatePoint(leftStones + rightStones + 1);     
        
        int topLeftStones = numStones(getTopLeftTiles(tile), type);
        int bottomRightStones = numStones(getBottomRightTiles(tile), type);
        
        int forwardDiagonalPoint = calculatePoint(topLeftStones + bottomRightStones + 1);
        
        int topRightStones = numStones(getTopRightTiles(tile), type);
        int bottomLeftStones = numStones(getBottomLeftTiles(tile), type);
        
        int backwardDiagonalPoint = calculatePoint(topRightStones + bottomLeftStones + 1);
        
        return verticalPoint + herizontalPoint + forwardDiagonalPoint + backwardDiagonalPoint;
    }
        
    private int calculatePoint(int numStones) {
        int point = 0;
        switch(numStones) {
            case 3:
                point = 1;
                break;
            case 4:
                point = 2;
                break;
            case 5:
                point = 3;
                break;
        }
        return point;
    }
    
    private int numStones(Tile[] tiles, PlayerType type) {
        int numStones = 0; // 1 because of the current stone
        for(int i=0; i<tiles.length; i++) {
//            log.debug("tiles[" + i + ']' + tiles[i]);
            if(tiles[i].hasStone()) {
//                log.debug("tiles[" + i + ']' + tiles[i] + "has Stone");
                Slot slot = (Slot) tiles[i];
                if(slot.getStone().getType() == type)
                    numStones++;
            }
        }
        
        return numStones;
    }
    private Tile[] getTopTiles(Tile tile) {
        Tile[] tiles = new Tile[2];
        
        for(int i=0; i<tiles.length; i++) {
            Tile nextTile = board.getBoard()[tile.getX()][tile.getY() - (i + 1)];
            if(nextTile.isPlayable()) {             
                tiles[i] = (Slot) nextTile;   
            } else {
                tiles[i] = (Flat) nextTile;   
            }
            
            log.debug("Top Tile : " + tiles[i]);
        }
        return tiles;
    }
        
    private Tile[] getBottomTiles(Tile tile) {
        Tile[] tiles = new Tile[2];
        
        for(int i=0; i<tiles.length; i++) {
            Tile nextTile = board.getBoard()[tile.getX()][tile.getY() + (i + 1)];
            if(nextTile.isPlayable()) {             
                tiles[i] = (Slot) nextTile;   
            } else {
                tiles[i] = (Flat) nextTile;   
            }
            log.debug("Bottom Tile : " + tiles[i]);
        }
        return tiles;
    }
    
    private Tile[] getLeftTiles(Tile tile) {
        Tile[] tiles = new Tile[2];
        
        for(int i=0; i<tiles.length; i++) {
            Tile nextTile = board.getBoard()[tile.getX() - (i + 1)][tile.getY()];
            if(nextTile.isPlayable()) {             
                tiles[i] = (Slot) nextTile;   
            } else {
                tiles[i] = (Flat) nextTile;   
            }
            log.debug("Left Tile : " + tiles[i]);
        }
        return tiles;
    }
    
    private Tile[] getRightTiles(Tile tile) {
        Tile[] tiles = new Tile[2];
        
        for(int i=0; i<tiles.length; i++) {
            Tile nextTile = board.getBoard()[tile.getX() + (i + 1)][tile.getY()];
            if(nextTile.isPlayable()) {             
                tiles[i] = (Slot) nextTile;   
            } else {
                tiles[i] = (Flat) nextTile;   
            }
            log.debug("Right Tile : " + tiles[i]);
        }
        return tiles;
    }
        
    private Tile[] getTopLeftTiles(Tile tile) {
        Tile[] tiles = new Tile[2];
        
        for(int i=0; i<tiles.length; i++) {
            Tile nextTile = board.getBoard()[tile.getX() - (i + 1)][tile.getY() - (i + 1)];
            if(nextTile.isPlayable()) {             
                tiles[i] = (Slot) nextTile;  
            } else {
                tiles[i] = (Flat) nextTile;   
            }
            log.debug("TopLeft Tile : " + tiles[i]);
        }
        return tiles;
    }
    
    private Tile[] getTopRightTiles(Tile tile) {
        Tile[] tiles = new Tile[2];

        for(int i=0; i<tiles.length; i++) {
            Tile nextTile = board.getBoard()[tile.getX() + (i + 1)][tile.getY() - (i + 1)];
            if(nextTile.isPlayable()) {             
                tiles[i] = (Slot) nextTile;  
            } else {
                tiles[i] = (Flat) nextTile;   
            }
            log.debug("TopRight Tile : " + tiles[i]);
        }
        return tiles;
    }
        
    private Tile[] getBottomLeftTiles(Tile tile) {
        Tile[] tiles = new Tile[2];
        
        for(int i=0; i<tiles.length; i++) {
            Tile nextTile = board.getBoard()[tile.getX() - (i + 1)][tile.getY() + (i + 1)];
            if(nextTile.isPlayable()) {             
                tiles[i] = (Slot) nextTile;   
            } else {
                tiles[i] = (Flat) nextTile;   
            }
            log.debug("BottomLeft Tile : " + tiles[i]);
        }
        return tiles;
    }
    
    private Tile[] getBottomRightTiles(Tile tile) {
        Tile[] tiles = new Tile[2];

        for(int i=0; i<tiles.length; i++) {
            Tile nextTile = board.getBoard()[tile.getX() + (i + 1)][tile.getY() + (i + 1)];
            if(nextTile.isPlayable()) {             
                tiles[i] = (Slot) nextTile;   
            } else {
                tiles[i] = (Flat) nextTile;   
            }
            log.debug("BottomRight Tile : " + tiles[i]);
        }
        return tiles;
    }
    
}
