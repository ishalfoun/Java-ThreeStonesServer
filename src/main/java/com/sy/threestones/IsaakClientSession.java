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
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author i
 */
public class IsaakClientSession {
    
    
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private byte[] byteBuffer;
    private int packetSize;
    
    //constructor
    public IsaakClientSession (Socket socket) throws IOException
    {
        this.socket=socket;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
        this.packetSize=4;
    }
    
    /**
     * Waits until it receives one Packet.
     * then returns the stone and opcode as a list
     * 
     * @return List of [Stone, opcode]
     * @throws IOException 
     */
    public ArrayList<Object> receivePacket() throws IOException
    {
        int totalBytesRcvd = 0;      // Total bytes received so far
        int bytesRcvd;        // Bytes received in last read
        while (totalBytesRcvd < packetSize)
        {
          if ( (bytesRcvd = in.read(byteBuffer, totalBytesRcvd,   packetSize - totalBytesRcvd)) == -1)
            throw new SocketException("Connection closed prematurely");
          totalBytesRcvd += bytesRcvd;
        }
        Opcode opcode= Opcode.values()[(int)byteBuffer[0]];
        int x = (int)byteBuffer[1];
        int y = (int)byteBuffer[2];
        //int score = (int)byteBuffer[3];
        
        System.out.println("Received: opcode:"+opcode.getValue()+" x="+x+" y="+y);
        return new ArrayList<>(Arrays.asList(new Stone(x,y), opcode));
    }
     
    
    
    /**
     * Sends one packet containing a stone and opcode
     * @param stone
     * @param opcode
     * @throws IOException 
     */
    public void sendPacket(Stone stone, Opcode opcode) throws IOException 
    {    
        switch(opcode)
        {
            case REQ_GAME_START:
            {
                byteBuffer = new byte[]{(byte)Opcode.REQ_GAME_START.getValue(), 0b0, 0b0, 0b0};
                break;
            }
            case CLIENT_PLACE:
            {
                if (stone==null)
                    throw new IllegalArgumentException();
                byteBuffer = new byte[]{(byte)Opcode.CLIENT_PLACE.getValue(), (byte)stone.getX(), (byte)stone.getY(), 0b0};
                break;
            }
            case ACK_PLAY_AGAIN:
            {
                byteBuffer = new byte[]{(byte)Opcode.ACK_PLAY_AGAIN.getValue(), 0b0, 0b0, 0b0};
                break;   
            }
            default:
                throw new IllegalArgumentException();
                // TODO: add code here
        }   
        
        out.write(byteBuffer);
    }
    
    
    public static void main (String args[]) throws IOException
    {
        
        Scanner sc=new Scanner(System.in);  
        System.out.println("Welcome! Please enter <Server> to start game");  
        sc.next();
        String server = "192.168.12.104";
        int servPort = 7;

        IsaakClientSession isi = new IsaakClientSession( new Socket(server, servPort));

        System.out.println("sending a packet");
        isi.sendPacket(new Stone(3,4), Opcode.CLIENT_PLACE);
    
        System.out.println("waiting to receive packet");
        ArrayList<Object> recvd = isi.receivePacket();
        
        
    }
    
}
