package com.sy.threestones;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author KimHyonh
 */
public class ThreeStonesBoardTest {
    
    ThreeStonesBoard board;
    
    public ThreeStonesBoardTest() {
    }
    
    @Before
    public void setUp() {
        board = new ThreeStonesBoard(5);
    }
    
//    @Test
//    public void testPlaceStone() {
//        Stone stone = new Stone(0, 0, PlayerType.COMPUTER);
//        board.placeStone(stone);
//        
//        Stone stone = (Stone) board.getBoard()[stone.getX()][stone.getY()];
//    }
}
