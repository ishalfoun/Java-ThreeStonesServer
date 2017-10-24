package com.sy.threestones;

import java.io.IOException;
import java.net.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Roan
 */
public class ThreeStonesServer {
    
    private final Logger log = LoggerFactory.getLogger(
            this.getClass().getName());
    
    private final int PORTNUM = 9999;
    
    public void runServer()throws IOException  {
        log.info("runServer");
        ServerSocket ss = new ServerSocket(PORTNUM); 
        while(true) {
            Socket cs = ss.accept();
            log.info("accept client connection");
            ThreeStonesServerSession session = new ThreeStonesServerSession(cs);
//            IsaakServerSession threeStonesServerSession = new IsaakServerSession(ss);
            session.playSession();
        }
    }
}
