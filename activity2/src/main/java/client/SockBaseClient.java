package client;

import java.net.*;
import java.io.*;

import org.json.*;

import buffers.RequestProtos.Request;
import buffers.ResponseProtos.Response;
//import jdk.jfr.internal.RequestEngine;
import buffers.ResponseProtos.Entry;

import java.util.*;
import java.util.stream.Collectors;

class SockBaseClient {

    public static void main (String args[]) throws Exception {
        Socket serverSock = null;
        OutputStream out = null;
        InputStream in = null;
        int i1=0, i2=0;
        int port = 9099; // default port

        // Make sure two arguments are given
        if (args.length != 2) {
            System.out.println("Expected arguments: <host(String)> <port(int)>");
            System.exit(1);
        }
        String host = args[0];
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port] must be integer");
            System.exit(2);
        }

        // Ask user for username
        System.out.println("Please provide your name for the server.");
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String strToSend = stdin.readLine();

        // Build the first request object just including the name
        Request op = Request.newBuilder()
                .setOperationType(Request.OperationType.NAME)
                .setName(strToSend).build();
        Response response;
        try {
            // connect to the server
            serverSock = new Socket(host, port);

            // write to the server
            out = serverSock.getOutputStream();
            in = serverSock.getInputStream();

            op.writeDelimitedTo(out);

            // read from the server
            response = Response.parseDelimitedFrom(in);

            // print the server response. 
            System.out.println(response.getGreeting()); 

            //greeting recieved, get user input
            boolean notQuit = true;

            //while loop to do operations
            while(notQuit) {
                String userStartInput = stdin.readLine();
                try {
                    int selectedUserNum = Integer.parseInt(userStartInput);
                    
                    //if 3 selected, quit
                    switch(selectedUserNum) {
                        case(1): //shows leaderboard
                            op = Request.newBuilder()
                                .setOperationType(Request.OperationType.LEADER).build();
                            break;
                        case(2): //starts game
                            op = Request.newBuilder()
                                .setOperationType(Request.OperationType.NEW).build();
                            break;
                        case(3): //quits
                            op = Request.newBuilder()
                                .setOperationType(Request.OperationType.QUIT).build();
                            notQuit = false;
                            break;
                        default:
                            System.out.println("Invalid integer, please enter 1, 2, or 3.");
                            continue;
                    }
                    //send the request
                    op.writeDelimitedTo(out);
                    
                    //read from the server
                    response = Response.parseDelimitedFrom(in);

                    //print server response
                    System.out.println(response.getGreeting());
                    
                        
                    

                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid Input, please enter 1, 2, or 3");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null)   in.close();
            if (out != null)  out.close();
            if (serverSock != null) serverSock.close();
        }
    }




}


