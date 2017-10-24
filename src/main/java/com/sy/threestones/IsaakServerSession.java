///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.sy.threestones;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.net.SocketException;
//import java.util.ArrayList;
//import java.util.Arrays;
//
///**
// *
// * @author i
// */
//public class IsaakServerSession {
//
//    private InputStream in;
//    private OutputStream out;
//    private ServerSocket servSock;
//    private Socket socket;
//    private int packetSize;
//    private byte[] byteBuffer;
//    private boolean play;
//    ThreeStonesServerGame game;
//    
//    //constructor
//    public IsaakServerSession(ServerSocket servSock) throws IOException
//    {
//        this.servSock = servSock;
//        this.socket= servSock.accept();
//        this.in = socket.getInputStream();
//        this.out = socket.getOutputStream();
//        this.packetSize = 4;
//        byteBuffer = new byte[4];
//        play=true;
//    }
//    
//    public void playSession() throws IOException
//    {
//        ArrayList<Object> receivedPacket = receivePacket();
//        
//        if (receivedPacket.get(1) == Opcode.REQ_GAME_START)
//        {
//            play=true;
//            game = new ThreeStonesServerGame(this);
//            //send back ACKnoledgement
//        }
//        while (play)
//        {
//            game.playGame();   
//            //ant to play agani?
//        }
//        
//    }
//    public void sendPacket(Stone stone, Opcode opcode) throws IOException {
//        // TODO : turn the Stone object into byte[], then send it
//        
//        byte[] byteBuffer;
//        switch(opcode)
//        {
//            case ACK_GAME_START:
//            {
//                byteBuffer = new byte[]{(byte)Opcode.ACK_GAME_START.getValue(), 0b0, 0b0, 0b0};
//                break;
//            }
//            case SERVER_PLACE:
//            {
//                if (stone==null)
//                    throw new IllegalArgumentException();
//                byteBuffer = new byte[]{(byte)Opcode.SERVER_PLACE.getValue(), (byte)stone.getX(), (byte)stone.getY(), 0b0};
//                break;
//            }
//            case REQ_PLAY_AGAIN:
//            {
//                byteBuffer = new byte[]{(byte)Opcode.REQ_PLAY_AGAIN.getValue(), 0b0, 0b0, 0b0};
//                break;   
//            }
//            default:
//                throw new IllegalArgumentException();
//                // TODO: add code here
//        }
//     
//        out.write(byteBuffer);
//    }
//    
//    /**
//     * Receives 1 packet, exception if connection closed
//     * @throws IOException 
//     */
//    public ArrayList<Object> receivePacket() throws IOException 
//    {
//       int totalBytesRcvd = 0;      // Total bytes received so far
//        int bytesRcvd;        // Bytes received in last read
//        while (totalBytesRcvd < packetSize)
//        {
//          if ( (bytesRcvd = in.read(byteBuffer, totalBytesRcvd,   packetSize - totalBytesRcvd)) == -1)
//            throw new SocketException("Connection closed prematurely");
//          totalBytesRcvd += bytesRcvd;
//        }
//        Opcode opcode= Opcode.values()[(int)byteBuffer[0]];
//        int x = (int)byteBuffer[1];
//        int y = (int)byteBuffer[2];
//        //int score = (int)byteBuffer[3];
//        
//        System.out.println("Received: opcode:"+opcode.getValue()+" x="+x+" y="+y);
//        return new ArrayList<>(Arrays.asList(new Stone(x, y, PlayerType.PLAYER), opcode));
//    }
//    
//    public static void main (String args[]) throws IOException
//    {
//        System.out.println("starting server");
//        IsaakServerSession server = new IsaakServerSession(new ServerSocket(7)); 
//            // create a server to accept client.
//        System.out.println("listening until connection closed");
//        server.receivePacket(); //wait for receiving, then display & resend
//        
//        //this.socket.close(); // might be needed!
//    }
//}
//
//
