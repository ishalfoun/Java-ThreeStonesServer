package com.sy.threestones;

import java.io.IOException;
import java.net.*;

/**
 *
 * @author Roan
 */
public class ThreeStonesServer {
    
    public void runServer()throws IOException  {
        int portNum = 7;
        ServerSocket ss = new ServerSocket(portNum); 
        System.out.println("the server port number is" + portNum);
        while(true){
            Socket cs = ss.accept();
            ThreeStonesServerSession threeStonesServerSession = new ThreeStonesServerSession(cs);
            threeStonesServerSession.playSession();
        }
        // TODO : infinite loop until the socket accept
        // then create ThreeStonesSession(Socket)
        // then call the playSession() on the ThreeStonesSession object
    }
}
