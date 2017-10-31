package com.sy.threestones;

/**
 * the flat class defines a "wall" on the board where stones can not be places.
 * @author Pengkim Sy
 */
public class Flat extends Tile{

    public Flat(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean isPlayable() {
        return false;
    }

    @Override
    public Tile getTile() {
        return this;
    }

    @Override
    public boolean hasStone() {
        return false;
    }

    @Override
    public String toString() {
        return "Flat{" + "x=" + x + ", y=" + y +'}';
    }
    
    
}
