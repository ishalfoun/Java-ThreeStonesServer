package com.sy.threestones;

import java.io.IOException;
import java.net.SocketException;
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
    
    /**
     * When the game started, first it will draw the board, then
     * the game will be played. Game logic: first it wait for the 
     * player stone, place that stone on the board, if it can't place
     * on the board, it will send back an opcode INVALID_PLACE and wait 
     * the player stone again, else the computer will place a stone on the
     * board, and send that stone back with the player score and computer
     * score.
     * 
     * @param packet
     * @throws IOException 
     */
    public void playGame(ThreeStonesPacket packet) throws IOException {        

        log.info("Server Game - playGame");
                    
        board = new ThreeStonesBoard(11);
        board.fillBoardFromCSV("src/main/resources/board.csv");
        
        List<Stone> computerStones = new ArrayList<>();
        final int TOTAL_STONE = 15;
        
        int playerScore = 0;
        int computerScore = 0;
        while(computerStones.size() < TOTAL_STONE){
           
            packet.receivePacket();   
            
            Stone playerStone = packet.getStone();
            playerStone.setType(PlayerType.PLAYER);
            
            log.info("playerStone : " + playerStone.toString());
            
            List<Tile> playerPlayableSlots = new ArrayList<>();
            if(computerStones.size() > 0)
                playerPlayableSlots = board.getPlayableSlot(computerStones.get(computerStones.size() - 1));
            
            // debug player playableSlots
//            for(Tile t : playerPlayableSlots) {             
//                log.debug("Player playable slots : " + t.toString());   
//            }            
            
            if(!isInPlayableSlots(playerPlayableSlots, playerStone) || !board.placeStone(playerStone) ) {
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
            
            // Debug computer playable slots
             for(Tile tile : computerPlayableSlots) {
                log.debug("Computer playable slot : " + tile.toString());
            }
            
            Stone stone = determineNextMove(computerPlayableSlots);
            stone.setType(PlayerType.COMPUTER);
            if(board.placeStone(stone)) {
                computerScore += countPointForAPosition(stone, stone.getType());
                if(computerStones.size() < TOTAL_STONE - 1) {                    
                    packet.sendPacket(stone, Opcode.SERVER_PLACE, playerScore, computerScore);                    
                } else {
                    packet.sendPacket(stone, Opcode.REQ_PLAY_AGAIN, playerScore, computerScore);
                }
                computerStones.add(stone);   
            }
            
            log.debug("computer slot : " + board.getBoard()[stone.getX()][stone.getY()]);
            log.debug("#stones : " + computerStones.size());
            
            log.info("playerScore : " + playerScore);
            log.info("computerScore : " + computerScore);
        }
    }
    
    /**
     * Check if the stone can be placed in the playableSlots
     * @param playableSlots
     * @param stone
     * @return 
     */
    private boolean isInPlayableSlots(List<Tile> playableSlots, Stone stone) {
        if(playableSlots.size() == 0) 
            return true;
        
        for(Tile tile : playableSlots) {
            if(stone.getX() == tile.getX() && stone.getY() == tile.getY())
                return true;
        }
        
        return false;
    }
    
    /**
     * Determine the best possible move for the computer. This will
     * receive a list of playable tiles, and return the move that get 
     * the best score. If there is no score, it return random stone that
     * in the list playable tiles.
     * 
     * @param playableTiles - a list of tiles that must be a slot that has no stone
     * @return 
     */
    public Stone determineNextMove(List<Tile> playableTiles) {
        log.info("Server Game - determineNextMove");
        
        Stone stone =  null;
        
        int point = 0;
        int computerStones = 0;
        int playerPoint = 0;
        for(Tile tile : playableTiles) {
            int newPoint = countPointForAPosition(tile, PlayerType.COMPUTER);
            int newComStone = countStonesAroundATile(tile, PlayerType.COMPUTER);
            int newPlayerPoint = countPointForAPosition(tile, PlayerType.PLAYER);
//            log.debug("newPoint : " + newPoint);
            if(newPoint > point || newPlayerPoint > playerPoint || newComStone > computerStones){                 
                log.debug("inside newPoint > point");
                point = newPoint;
                playerPoint = newPlayerPoint;
                computerStones = newComStone;
                Slot slot = (Slot) tile;
                stone = new Stone(slot.getX(), slot.getY(), PlayerType.COMPUTER);
                
                log.debug("player stone : " + playerPoint);
                log.debug("computer stone : " + computerStones);
            }            
        }
        
        if(point == 0 && playerPoint == 0 && computerStones == 0) {
            log.debug("inside point == 0");
            int random = (int) (Math.random() * playableTiles.size());
            Slot slot = (Slot) playableTiles.get(random);
            stone = new Stone(slot.getX(), slot.getY(), PlayerType.COMPUTER);
        }
        
        log.debug("point = " + point);
        log.info("nextMove : " + stone.toString());
        return stone;
    } 
    
    /**
     * Count all the possible point of the tile
     * @param tile
     * @param type
     * @return 
     */
    public int countPointForAPosition(Tile tile, PlayerType type) {
        log.info("input Tile " + tile.toString());
      
        int topStones = countStone(getTopTiles(tile), type);
        int bottomStones = countStone(getBottomTiles(tile), type);
        
        int verticalPoint = calculatePoint(topStones + bottomStones + 1);
        
        int leftStones = countStone(getLeftTiles(tile), type);
        int rightStones = countStone(getRightTiles(tile), type);
        
        int herizontalPoint = calculatePoint(leftStones + rightStones + 1);     
        
        int topLeftStones = countStone(getTopLeftTiles(tile), type);
        int bottomRightStones = countStone(getBottomRightTiles(tile), type);
        
        int forwardDiagonalPoint = calculatePoint(topLeftStones + bottomRightStones + 1);
        
        int topRightStones = countStone(getTopRightTiles(tile), type);
        int bottomLeftStones = countStone(getBottomLeftTiles(tile), type);
        
        int backwardDiagonalPoint = calculatePoint(topRightStones + bottomLeftStones + 1);
        
        return verticalPoint + herizontalPoint + forwardDiagonalPoint + backwardDiagonalPoint;
    }
    
    /**
     * Count the stones of the same type around the tile. This will count
     * 2 tiles horizontal, vertical and diagonal around the tile.
     * @param tile
     * @param type
     * @return 
     */
    private int countStonesAroundATile(Tile tile, PlayerType type) {
              
        int topStones = countStone(getTopTiles(tile), type);
        int bottomStones = countStone(getBottomTiles(tile), type);
   
        int leftStones = countStone(getLeftTiles(tile), type);
        int rightStones = countStone(getRightTiles(tile), type);
  
        int topLeftStones = countStone(getTopLeftTiles(tile), type);
        int bottomRightStones = countStone(getBottomRightTiles(tile), type);
  
        int topRightStones = countStone(getTopRightTiles(tile), type);
        int bottomLeftStones = countStone(getBottomLeftTiles(tile), type);

        return topStones + bottomStones + leftStones + rightStones + topLeftStones
                + bottomRightStones + topRightStones + bottomLeftStones;
    }
        
    /**
     * 1 point for 3 stones, 2 point for 4 stones and 3 point for 5 stones
     * @param numStones
     * @return 
     */
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
    
    /**
     * Count stones of the same type that are inside the tiles[]
     * @param tiles[]
     * @param type 
     * @return 
     */
    private int countStone(Tile[] tiles, PlayerType type) {
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
    
    /**
     * Get 2 tiles above this
     * @param tile
     * @return 
     */
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
       
    /**
     * Get 2 tiles below this
     * @param tile
     * @return 
     */
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
    
    /**
     * Get 2 left tiles from this
     * @param tile
     * @return 
     */
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
    
    /**
     * Get 2 right tiles from this
     * @param tile
     * @return 
     */
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
        
    /**
     * Get 2 top left tiles from this
     * @param tile
     * @return 
     */
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
    
    /**
     * Get 2 top right tiles from this
     * @param tile
     * @return 
     */
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
     
    /**
     * Get 2 bottom left tiles from this
     * @param tile
     * @return 
     */
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
    
    /**
     * Get 2 bottom right tiles from this
     * @param tile
     * @return 
     */
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
