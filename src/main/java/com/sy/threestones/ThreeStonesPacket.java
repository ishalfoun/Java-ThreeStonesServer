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
    
    Socket socket;
    //constructor
    public ThreeStonesPacket(Socket socket) {   
        
        this.socket = socket;
        receiveByte = new byte[5];
        try {
            in = socket.getInputStream();    
            out = socket.getOutputStream();
        } catch (IOException ex) {
            log.error("ThreeStonesPacket : " + ex.getMessage());
        }
    }
    
    public void sendPacket(Stone stone, Opcode opcode, int playerScore, int computerScore) throws IOException {
        byte[] byteBuffer;
        switch(opcode) {
            case ACK_GAME_START:
            {
                byteBuffer = new byte[]{(byte)Opcode.ACK_GAME_START.getValue(), 0b0, 0b0, 0b0, 0b0};
                break;
            }
            case SERVER_PLACE:
            {
                if (stone==null)
                    throw new IllegalArgumentException();
                byteBuffer = new byte[]{(byte)Opcode.SERVER_PLACE.getValue(), (byte)stone.getX()
                        , (byte)stone.getY(), (byte) playerScore, (byte) computerScore};
                break;
            }
            case REQ_PLAY_AGAIN:
            {
                byteBuffer = new byte[]{(byte)Opcode.REQ_PLAY_AGAIN.getValue(), 0b0, 0b0, 0b0, 0b0};
                break;   
            }
            case NOT_VALID_PLACE:
            {
                byteBuffer = new byte[]{(byte)Opcode.NOT_VALID_PLACE.getValue(), 0b0, 0b0, 0b0, 0b0};
                break;
            }
            default:
                throw new IllegalArgumentException();
        }
     
        out.write(byteBuffer);
        log.info("send Packet");
    }
        
    public void receivePacket() throws IOException, SocketException {
        log.info("receive packet");

        int totalBytesRcvd = 0;      // Total bytes received so far
        int bytesRcvd;        // Bytes received in last read
        
        while (totalBytesRcvd < receiveByte.length) {
          if ((bytesRcvd = in.read(receiveByte, totalBytesRcvd, receiveByte.length - totalBytesRcvd)) == -1)
            throw new SocketException("Connection closed prematurely");
            totalBytesRcvd += bytesRcvd;
            log.debug("totalByteRcvd : " + totalBytesRcvd);
        }       
    }
    
    public String getIpAddress() {
        return this.socket.getInetAddress().getHostAddress();
    }
    
    public int getPort() {
        return this.socket.getPort();
    }
    
    public int getLocalPort() {
        return this.socket.getLocalPort();
    }
    
    public void closeConnection() throws IOException {
        this.socket.close();
    }
    
    public Stone getStone() throws IOException {
        int x = (int) receiveByte[1];
        int y = (int) receiveByte[2];
        
        Stone stone = new Stone(x, y, PlayerType.PLAYER);
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
            this.sendPacket(null, Opcode.ACK_GAME_START, 0, 0);
            return true;
        }
        
        return false;
    }
    
    public boolean playAgain() throws IOException {
        Opcode opcode = Opcode.values()[ (int) receiveByte[0]];
        
        log.info("Opcode playAgain : " + opcode.name());
        
        if(opcode == Opcode.ACK_PLAY_AGAIN) {
            this.sendPacket(null, Opcode.ACK_GAME_START, 0, 0);            
            return true;
        }
        return false;
    }
}
