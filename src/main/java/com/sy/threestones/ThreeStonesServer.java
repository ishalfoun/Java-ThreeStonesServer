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
    
    private final int PORT = 50000;
//    private String serverAddress;
//    private String clientAddress;
//    private int port;
    
    /**
     * Run an infinite loop that keeps listening for client connection
     * @throws IOException 
     */
    public void runServer()throws IOException  {

        ServerSocket ss = new ServerSocket(PORT); 
        InetAddress address = InetAddress.getLocalHost();
//        serverAddress = ss.getInetAddress().getHostAddress();
        
        while(true) {
            log.info("runServer");
            System.out.println("Server is running...");
            System.out.println("Server addresss : " + address.getHostAddress());
            
            
            Socket cs = ss.accept();
            log.info("accept client connection");
            System.out.println("Established client connection!");

            ThreeStonesServerSession session = new ThreeStonesServerSession(cs);
            
//            System.out.println("Server address : " + cs.getLocalAddress().getHostAddress());
            System.out.print("Client address : " + session.getPacket().getIpAddress());
            System.out.println(" at port : " + session.getPacket().getPort());
            
            session.playSession();
            session.closeSession();
            System.out.println("Client close connection!");
        }
    }
//    
//    public String getServerAddress() {
//        return this.serverAddress;
//    }
//    
//    public String getClientAddress() {
//        return this.clientAddress;
//    }
//    
//    public int getPort() {
//        return this.port;
//    }
}
