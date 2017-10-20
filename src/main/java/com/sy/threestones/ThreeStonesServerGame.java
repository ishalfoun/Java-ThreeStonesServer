package com.sy.threestones;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author KimHyonh
 */
public class ThreeStonesServerGame {
    
    ThreeStonesPacket packet;
    ThreeStonesBoard board;
    
    public ThreeStonesServerGame() {
        packet = new ThreeStonesPacket();
    }
    
    public void playGame() {        
        ThreeStonesBoard board = new ThreeStonesBoard(11);
        List<Stone> stones = new ArrayList<>();
        
        while(stones.size() < 15){
            Stone playerStone = packet.receivePacket();
            playerStone.setType(PlayerType.PLAYER);
            board.placeStone(playerStone);
            
            Stone stone = determineNextMove(board.getPlayableSlot(playerStone));
            stone.setType(PlayerType.COMPUTER);
            board.placeStone(stone);
//            slot.placeStone();
//            board.placeStone(computerStone);
            
//            stones.add(computerStone);
//            
//            packet.sendPacket(computerStone);
        }
    }
    
    public Stone determineNextMove(List<Tile> playableTiles) {
        Stone stone =  null;
        
        int point = 0;
        for(Tile tile : playableTiles) {
            int newPoint = countPointForAPosition(tile, PlayerType.COMPUTER);
            if(newPoint > point){                 
                point = newPoint;
                stone = (Stone) tile;
                stone.setType(PlayerType.COMPUTER);
            }            
        }
        
        if(point == 0) {
            int random = (int) (Math.random() * playableTiles.size());
            stone = (Stone) playableTiles.get(random);
            stone.setType(PlayerType.COMPUTER);
        }
        
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
