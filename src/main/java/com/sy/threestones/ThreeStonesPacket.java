package com.sy.threestones;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
   REQ_GAME_START,
   ACK_GAME_START, =server
   CLIENT_PLACE,
   SERVER_PLACE, = server
   REQ_PLAY_AGAIN,  = server
   ACK_PLAY_AGAIN    
 * @author Isaack
 */
public class ThreeStonesPacket {
    
    
    InputStream in;
    OutputStream out;
    
    //constructor
    public ThreeStonesPacket(Socket socket) throws IOException
    {
        
        in = socket.getInputStream();
        out = socket.getOutputStream();
    
    }
    public void sendPacket(Stone stone, Opcode opcode) {
        // TODO : turn the Stone object into byte[], then send it
        
    }
    
    
    public void receivePacket() {
        
    }
    
    public void sendScore(int score) {
        
    }
}
