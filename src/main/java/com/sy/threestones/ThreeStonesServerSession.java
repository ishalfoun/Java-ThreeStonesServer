package com.sy.threestones;

import java.io.IOException;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author KimHyonh
 */
public class ThreeStonesServerSession {
    
    private final Logger log = LoggerFactory.getLogger(
            this.getClass().getName());
    
    ThreeStonesPacket packet;
    
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
                game.playGame(packet);
                        
                packet.sendPacket(null, Opcode.REQ_PLAY_AGAIN, 0, 0);
                log.debug("send REQ_PLAY_AGAIN");
        
                packet.receivePacket();
                packet.receivePacket();
                playAgain = packet.playAgain();
            }   
        }
    }
    
//    public void closeSession() throws IOException {
//        socket.close();
//    }
}
