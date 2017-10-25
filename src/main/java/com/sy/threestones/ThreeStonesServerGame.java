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
    
//    private ThreeStonesPacket packet;
//    IsaakServerSession session;
    private ThreeStonesBoard board;
//    boolean gameRunning;
//    Stone playerStone;
//    Stone compStone;
    
    private List<Stone> stones;
    
    private ArrayList<Object> receivedPacket;
    
//    public ThreeStonesServerGame(IsaakServerSession i) {
////        packet = new ThreeStonesPacket();
//        board = new ThreeStonesBoard(11);
//        stones = new ArrayList<>();
//        session = i;
//        gameRunning=true;
//    }
    
    public ThreeStonesServerGame() {
        board = new ThreeStonesBoard(11);
        board.fillBoardFromCSV("src/main/resources/board.csv");
        stones = new ArrayList<>();
    }
    
//    public void playGame() throws IOException, Exception
//    {
//        while (stones.size() < 15)
//        {
//            receivedPacket = session.receivePacket(); //receive Handshake packet
//            if (receivedPacket.get(1) == Opcode.CLIENT_PLACE)
//            {
//                Stone playerStone = (Stone)receivedPacket.get(0);
//                playerStone.setType(PlayerType.PLAYER);
//                if(!board.placeStone(playerStone)){
//                    // stone not placed
//                }
//                Stone compStone = determineNextMove(board.getPlayableSlot(playerStone));
//                compStone.setType(PlayerType.COMPUTER);
//                board.placeStone(compStone);
//
//                stones.add(compStone);
//                session.sendPacket(compStone, Opcode.SERVER_PLACE);
//            }
////            else
//            //log: Did not receive CLIENT_PLACE");
//         }
//        //dispay: game finished!!
//    }
    
    public void playGame(ThreeStonesPacket packet) throws IOException {        

        log.info("Server Game - playGame");
        
//        Stone previousStone = null;
        while(stones.size() < 15){
            packet.receivePacket();
            Stone playerStone = packet.getStone();
            playerStone.setType(PlayerType.PLAYER);
            
            log.info("playerStone : " + playerStone.toString());
            
            List<Tile> playerPlayableSlots = new ArrayList<>();
            if(stones.size() > 0)
                playerPlayableSlots = board.getPlayableSlot(stones.get(stones.size() - 1));
            
            for(Tile t : playerPlayableSlots) {             
                log.debug("Player playable slots : " + t.toString());   
            }
            
            if(!board.placeStone(playerStone) || !isInPlayableSlots(playerPlayableSlots, playerStone)) {
                packet.sendPacket(null, Opcode.NOT_VALID_PLACE);
                continue;
            }
            
            List<Tile> computerPlayableSlots = board.getPlayableSlot(playerStone);
            Stone stone = determineNextMove(computerPlayableSlots);
            stone.setType(PlayerType.COMPUTER);
            if(board.placeStone(stone)) {
                packet.sendPacket(stone, Opcode.SERVER_PLACE);
                stones.add(stone);
            }
        }
        
        packet.sendPacket(null, Opcode.REQ_PLAY_AGAIN);
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
//                stone = (Stone) tile;
                stone = new Stone(slot.getX(), slot.getY(), PlayerType.COMPUTER);
            }            
        }
        
        if(point == 0) {
            int random = (int) (Math.random() * playableTiles.size());
            Slot slot = (Slot) playableTiles.get(random);
            stone = new Stone(slot.getX(), slot.getY(), PlayerType.COMPUTER);
//            stone.setType(PlayerType.COMPUTER);
        }
        
        log.info("nextMove : " + stone.toString());
        return stone;
    } 
    
    public int countPointForAPosition(Tile tile, PlayerType type) {
      
        int topPoint = countPoint(getTopTiles(tile), type);
        int bottomPoint = countPoint(getBottomTiles(tile), type);
        int leftPoint = countPoint(getLeftTiles(tile), type);
        int rightPoint = countPoint(getRightTiles(tile), type);
        int topLeftPoint = countPoint(getTopLeftTiles(tile), type);
        int topRightPoint = countPoint(getTopRightTiles(tile), type);
        int bottomLeftPoint = countPoint(getBottomLeftTiles(tile), type);
        int bottomRightPoint = countPoint(getBottomRightTiles(tile), type);
        
        return topPoint + bottomPoint + leftPoint + rightPoint
                + topLeftPoint + topRightPoint + bottomLeftPoint + bottomRightPoint;
    }

    public void incrementScore() {
        
    }
        
    private int countPoint(Tile[] tiles, PlayerType type) {
        int numStones = 1; // 1 because of the current stone
        for(int i=0; i<tiles.length; i++) {
            if(tiles[i].hasStone()) {
                Slot slot = (Slot) tiles[i];
                if(slot.getStone().getType() == type)
                    numStones++;
            }
        }
        
        if(numStones == 3) return 1;
        
        return 0;
    }
    private Tile[] getTopTiles(Tile tile) {
        Tile[] top = new Tile[2];
        
        for(int i=0; i<top.length; i++) {
            if(board.getBoard()[tile.getX()][tile.getY()].isPlayable()) {             
                top[i] = new Slot(tile.getX() - (i + 1), tile.getY());   
            } else {
                top[i] = new Flat(tile.getX() - (i + 1), tile.getY());   
            }
        }
        return top;
    }
        
    private Tile[] getBottomTiles(Tile tile) {
        Tile[] top = new Tile[2];
        
        for(int i=0; i<top.length; i++) {
            if(board.getBoard()[tile.getX()][tile.getY()].isPlayable()) {             
                top[i] = new Slot(tile.getX() + (i + 1), tile.getY());   
            } else {
                top[i] = new Flat(tile.getX() + (i + 1), tile.getY());   
            }
        }
        return top;
    }
    
    private Tile[] getLeftTiles(Tile tile) {
        Tile[] top = new Tile[2];
        
        for(int i=0; i<top.length; i++) {
            if(board.getBoard()[tile.getX()][tile.getY()].isPlayable()) {             
                top[i] = new Slot(tile.getX(), tile.getY() - (i + 1));   
            } else {
                top[i] = new Flat(tile.getX(), tile.getY() - (i + 1));   
            }
        }
        return top;
    }
    
    private Tile[] getRightTiles(Tile tile) {
        Tile[] top = new Tile[2];
        
        for(int i=0; i<top.length; i++) {
            if(board.getBoard()[tile.getX()][tile.getY()].isPlayable()) {             
                top[i] = new Slot(tile.getX(), tile.getY() + (i + 1));   
            } else {
                top[i] = new Flat(tile.getX(), tile.getY() + (i + 1));   
            }
        }
        return top;
    }
        
    private Tile[] getTopLeftTiles(Tile tile) {
        Tile[] top = new Tile[2];
        
        for(int i=0; i<top.length; i++) {
            if(board.getBoard()[tile.getX()][tile.getY()].isPlayable()) {             
                top[i] = new Slot(tile.getX() - (i + 1), tile.getY() - (i + 1));   
            } else {
                top[i] = new Flat(tile.getX() - (i + 1), tile.getY() - (i + 1));   
            }
        }
        return top;
    }
    
    private Tile[] getTopRightTiles(Tile tile) {
        Tile[] top = new Tile[2];

        for(int i=0; i<top.length; i++) {
            if(board.getBoard()[tile.getX()][tile.getY()].isPlayable()) {             
                top[i] = new Slot(tile.getX() - (i + 1), tile.getY() + (i + 1));   
            } else {
                top[i] = new Flat(tile.getX() - (i + 1), tile.getY() + (i + 1));   
            }
        }
        return top;
    }
        
    private Tile[] getBottomLeftTiles(Tile tile) {
        Tile[] top = new Tile[2];
        
        for(int i=0; i<top.length; i++) {
            if(board.getBoard()[tile.getX()][tile.getY()].isPlayable()) {             
                top[i] = new Slot(tile.getX() + (i + 1), tile.getY() - (i + 1));   
            } else {
                top[i] = new Flat(tile.getX() + (i + 1), tile.getY() - (i + 1));   
            }
        }
        return top;
    }
    
    private Tile[] getBottomRightTiles(Tile tile) {
        Tile[] top = new Tile[2];

        for(int i=0; i<top.length; i++) {
            if(board.getBoard()[tile.getX()][tile.getY()].isPlayable()) {             
                top[i] = new Slot(tile.getX() + (i + 1), tile.getY() + (i + 1));   
            } else {
                top[i] = new Flat(tile.getX() + (i + 1), tile.getY() + (i + 1));   
            }
        }
        return top;
    }
    
}
