package com.sy.threestones;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pengkim Sy
 */
public class ThreeStonesBoard {
    
    private Tile[][] board;
    private int size;
    
    public ThreeStonesBoard(int size) {
        this.size = size;
        this.board = new Tile[size][size];
    }
    
    public ThreeStonesBoard(Tile[][] board) {
        this.board = board;
    }

    public Tile[][] getBoard() {
        return board;
    }

    public int getSize() {
        return this.size;
    }
    
    
    public List<Tile> getPlayableSlot(Stone lastStonePlaced) {
        List<Tile> playableSlot = new ArrayList<>();
        
        // Find the playableSlot that are in the same row or column (x, y)
        for(int i=0; i<this.size; i++) {
            for(int j=0; j<this.size; j++) {
                if(i == lastStonePlaced.getY() || j == lastStonePlaced.getX()) {
                    Tile tile = board[i][j];
                    if(!tile.hasStone() && tile.isPlayable()){                     
                        playableSlot.add(tile);   
                    }
                }
            }
        }

        // If there's no slot in the same row and column get all playableSlot
        if(playableSlot.size() == 0) {
            for(int i=0; i<this.size; i++) {
                for(int j=0; j<this.size; j++) {
                    Tile tile = board[i][j];
                    if(!tile.hasStone() && tile.isPlayable()){                     
                        playableSlot.add(tile);   
                    }
                }
            }
        }
        return playableSlot;
    }
    
    public boolean placeStone(Stone stone) {
        
        if(board[stone.getX()][stone.getY()].isPlayable()){
            Slot slot = (Slot) board[stone.getX()][stone.getY()];
            if(!slot.hasStone()){
                slot.placeStone(stone);
                return true;
            }
        }
        
        return false;
    }
}
