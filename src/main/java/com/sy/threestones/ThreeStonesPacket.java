package com.sy.threestones;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreeStonesPacket {
      
    private final Logger log = LoggerFactory.getLogger(
            this.getClass().getName());
        
    InputStream in;
    OutputStream out;
    byte[] receiveByte;
    
    //constructor
    public ThreeStonesPacket(Socket socket) {    
        
        receiveByte = new byte[4];
        try {
            in = socket.getInputStream();    
            out = socket.getOutputStream();
        } catch (IOException ex) {
            log.error("ThreeStonesPacket : " + ex.getMessage());
        }
    }
    
    public void sendPacket(Stone stone, Opcode opcode) throws IOException {
        byte[] byteBuffer;
        switch(opcode) {
            case ACK_GAME_START:
            {
                byteBuffer = new byte[]{(byte)Opcode.ACK_GAME_START.getValue(), 0b0, 0b0, 0b0};
                break;
            }
            case SERVER_PLACE:
            {
                if (stone==null)
                    throw new IllegalArgumentException();
                byteBuffer = new byte[]{(byte)Opcode.SERVER_PLACE.getValue(), (byte)stone.getX(), (byte)stone.getY(), 0b0};
                break;
            }
            case REQ_PLAY_AGAIN:
            {
                byteBuffer = new byte[]{(byte)Opcode.REQ_PLAY_AGAIN.getValue(), 0b0, 0b0, 0b0};
                break;   
            }
            default:
                throw new IllegalArgumentException();
                // TODO: add code here
        }
     
        out.write(byteBuffer);
    }
        
    public void receivePacket() throws IOException {
        
        int totalBytesRcvd = 0;      // Total bytes received so far
        int bytesRcvd;        // Bytes received in last read
        
        while (totalBytesRcvd < receiveByte.length) {
          if ( (bytesRcvd = in.read(receiveByte, totalBytesRcvd, receiveByte.length - totalBytesRcvd)) == -1)
            throw new SocketException("Connection closed prematurely");
          totalBytesRcvd += bytesRcvd;
        }   
    }
    
    public Stone getStone() {
        
        int x = (int) receiveByte[1];
        int y = (int) receiveByte[2];
        
        Stone stone = new Stone(x, y, PlayerType.PLAYER);
        
        log.info("Stone : " + stone.toString());
        
        return stone;
    }
    
    public Opcode getOpcode() {        
        Opcode opcode = Opcode.values()[ (int) receiveByte[0]];
        
        log.info("Opcode : " + opcode.name());
        return opcode;        
    }
    
    public boolean canGameStart() throws IOException {
        Opcode opcode = Opcode.values()[(int) receiveByte[0]];
        
        log.info("Opcode canGameStart : " + opcode.name());
        if(opcode == Opcode.REQ_GAME_START) {
            this.sendPacket(null, Opcode.ACK_GAME_START);
            return true;
        }
        
        return false;
    }
    
    public boolean playAgain() {
        Opcode opcode = Opcode.values()[ (int) receiveByte[0]];
        log.info("Opcode playAgain : " + opcode.name());
        return opcode == Opcode.ACK_PLAY_AGAIN;
    }
}
