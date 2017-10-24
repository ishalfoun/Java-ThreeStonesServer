package com.sy.threestones;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Roan
 */
public class ThreeStonesServerApp {

//    private final static Logger log = LoggerFactory.getLogger(
//            this.getClass().getName());
//        
    public static void main(String[] args) {   

        System.out.println("3 Stones game");
        
//        log.debug("adk");
        ThreeStonesServer server = new ThreeStonesServer();
        try {
            server.runServer();
        } catch (IOException ex) {
//            log.debug("asldk");
        }
    }
    
}
