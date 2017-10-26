package com.sy.threestones;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Roan
 */
public class ThreeStonesServerApp {

    public static void main(String[] args) throws IOException {   

        System.out.println("Welcome to 3 Stones game");

        ThreeStonesServer server = new ThreeStonesServer();
        server.runServer();

//        System.out.println("Client address : " + server.getClientAddress());
//        System.out.println("Connected port : " + server.getPort());
    }
    
}
