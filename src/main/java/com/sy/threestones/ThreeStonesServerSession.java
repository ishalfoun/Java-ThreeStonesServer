package com.sy.threestones;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author KimHyonh
 */
public class ThreeStonesServerSession {
    
    private final Logger log = LoggerFactory.getLogger(
            this.getClass().getName());
    
    private ThreeStonesPacket packet;
        
    public ThreeStonesServerSession(Socket socket) {
//        this.socket = socket;
        packet = new ThreeStonesPacket(socket);
    }
    
    public void playSession() throws IOException {
        log.info("playSession");
        
        if(packet.canGameStart()) {
            log.info("packet : " + packet.getOpcode());
            
            ThreeStonesServerGame game = new ThreeStonesServerGame();
            boolean playAgain = true;
            while(playAgain) {                
                try {
                    game.playGame(packet);

                    packet.sendPacket(null, Opcode.REQ_PLAY_AGAIN, 0, 0);
                    log.debug("send REQ_PLAY_AGAIN");
        
                    packet.receivePacket();                
                    packet.receivePacket(); 
                    
                    playAgain = packet.playAgain();
                } catch(SocketException e) {
                    playAgain = false;
                    log.error(e.getMessage());                    
                }
            }   
        }
    }
    
    public ThreeStonesPacket getPacket() {
        return this.packet;
    }
    public void closeSession() throws IOException {
        packet.closeConnection();
    }
}
