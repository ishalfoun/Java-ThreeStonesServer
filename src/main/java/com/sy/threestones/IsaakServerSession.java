/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sy.threestones;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author i
 */
public class IsaakServerSession {

/**
 *
   REQ_GAME_START,
   ACK_GAME_START, =server
   CLIENT_PLACE,
   SERVER_PLACE, = server
   REQ_PLAY_AGAIN,  = server
   ACK_PLAY_AGAIN    
 * @author Isaack
 */
    
    private InputStream in;
    private OutputStream out;
    private ServerSocket servSock;
    private Socket socket;
    private int recvMsgSize;
    private byte[] byteBuffer;
    
    //constructor
    public IsaakServerSession(ServerSocket servSock) throws IOException
    {
        this.servSock = servSock;
        this.socket= servSock.accept();
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
        this.recvMsgSize = 4;
        byteBuffer = new byte[4];
    }
    
    public void sendPacket(Stone stone, Opcode opcode) throws IOException {
        // TODO : turn the Stone object into byte[], then send it
        
        byte[] byteBuffer;
        switch(opcode)
        {
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
    
    /**
     * Receives 1 packet, exception if connection closed
     * @throws IOException 
     */
    public void receivePacket() throws IOException 
    {
        // Receive until client closes connection, indicated by -1 return
        while ((recvMsgSize = in.read(byteBuffer)) != -1)
        {
            System.out.println("received: (bytes)"+ byteBuffer[0] + " "
                    + byteBuffer[1] + " " + byteBuffer[2] + " " 
                    + byteBuffer[3] + " " );
            //sending back:
            out.write(byteBuffer, 0, recvMsgSize);
            if (recvMsgSize==4)
            {
                Opcode opcode= Opcode.values()[(int)byteBuffer[0]];
                int x = (int)byteBuffer[1];
                int y = (int)byteBuffer[2];

            }
        }
    }
    
    public static void main (String args[]) throws IOException
    {
        System.out.println("starting server");
        IsaakServerSession server = new IsaakServerSession(new ServerSocket(7)); 
            // create a server to accept client.
        System.out.println("listening until connection closed");
        server.receivePacket(); //wait for receiving, then display & resend
        
        //this.socket.close(); // might be needed!
    }
}


