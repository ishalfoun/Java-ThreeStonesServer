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
    
    /**
     * Sent the packet
     * 
     * @param stone
     * @param opcode
     * @param playerScore
     * @param computerScore
     * @throws IOException 
     */
    public void sendPacket(Stone stone, Opcode opcode, int playerScore
            , int computerScore) throws IOException {
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
        
    /**
     * Receive the incoming packet and put it in the receiveByte
     * 
     * @throws IOException
     * @throws SocketException 
     */
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
    
    /**
     * Get client IP address
     * @return 
     */
    public String getIpAddress() {
        return this.socket.getInetAddress().getHostAddress();
    }
    
    /**
     * Get client port
     * @return 
     */
    public int getPort() {
        return this.socket.getPort();
    }
    
    /**
     * Get local Port
     * @return 
     */
    public int getLocalPort() {
        return this.socket.getLocalPort();
    }
    
    /**
     * Close the socket
     * 
     * @throws IOException 
     */
    public void closeConnection() throws IOException {
        this.socket.close();
    }
    
    /**
     * Get the Stone from the packet that the server received
     * @return
     * @throws IOException 
     */
    public Stone getStone() throws IOException {
        int x = (int) receiveByte[1];
        int y = (int) receiveByte[2];
        
        Stone stone = new Stone(x, y, PlayerType.PLAYER);
        return stone;
    }
    
    /**
     * Get the Opcode from the packet that server received
     * @return 
     */
    public Opcode getOpcode() {        
        Opcode opcode = Opcode.values()[ (int) receiveByte[0]];
        
        log.info("Opcode : " + opcode.name());
        return opcode;        
    }
    
    /**
     * Check if the server receive REQ_GAME_START, if yes, send back
     * ACK_GAME_START and return true, otherwise return false
     * 
     * @return
     * @throws IOException 
     */
    public boolean canGameStart() throws IOException {
        Opcode opcode = Opcode.values()[(int) receiveByte[0]];
        
        log.info("Opcode canGameStart : " + opcode.name());

        if(opcode == Opcode.REQ_GAME_START) {
            this.sendPacket(null, Opcode.ACK_GAME_START, 0, 0);
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if the client send back ACK_PLAY_AGAIN, if yes, send back 
     * ACK_GAME_START and return true, otherwise return false
     * 
     * @return
     * @throws IOException 
     */
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
