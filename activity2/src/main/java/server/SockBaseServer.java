package server;

import java.net.*;
import java.io.*;
import java.util.*;

import javax.swing.SingleSelectionModel;

import org.json.*;
import java.lang.*;

import buffers.RequestProtos.Request;
import buffers.RequestProtos.Logs;
import buffers.RequestProtos.Message;
import buffers.ResponseProtos.Response;
import buffers.ResponseProtos.Entry;

class SockBaseServer {
    static String logFilename = "logs.txt";

    ServerSocket serv = null;
    InputStream in = null;
    OutputStream out = null;
    Socket clientSocket = null;
    int port = 9099; // default port
    Game game;

    //move leaderboard to global
    // Creating Entry and Leader response
    Response.Builder res = Response.newBuilder()
        .setResponseType(Response.ResponseType.LEADER);


    public SockBaseServer(Socket sock, Game game){
        this.clientSocket = sock;
        this.game = game;
        try {
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();
        } catch (Exception e){
            System.out.println("Error in constructor: " + e);
        }
    }

    // Handles the communication right now it just accepts one input and then is done you should make sure the server stays open
    // can handle multiple requests and does not crash when the server crashes
    // you can use this server as based or start a new one if you prefer. 
    public void start() throws IOException {
        String name = "";


        System.out.println("Ready...");
        
        try {
            // read the proto object and put into new objct
            Request op = Request.parseDelimitedFrom(in);
            String result = null;

            boolean repeatedProcess = false, serverRunning = true;

            while(serverRunning) {
                // if the operation is NAME (so the beginning then say there is a commention and greet the client)
                if (op.getOperationType() == Request.OperationType.NAME && !repeatedProcess) {
                    // get name from proto object
                    name = op.getName();

                // writing a connect message to the log with name and CONNENCT
                writeToLog(name, Message.CONNECT);
                    System.out.println("Got a connection and a name: " + name);
                    Response response = Response.newBuilder()
                            .setResponseType(Response.ResponseType.GREETING)
                            .setGreeting("Hello " + name + " and welcome. \nWhat would you like to do? \n 1 - Display the Leaderboard \n 2 - Enter a Game \n 3 - quit")
                            .build();
                    response.writeDelimitedTo(out);
                    repeatedProcess = true;
                } else {
                    System.out.println(name + " completed a round, repeating...");
                    Response response = Response.newBuilder()
                        .setResponseType(Response.ResponseType.GREETING)
                        .setGreeting("Completed! What would you like to do now?\n \n 1 - Display the LeaderBoard \n 2 - Enter a Game \n 3 - quit")
                        .build();
                    response.writeDelimitedTo(out);
                }

                //now must read in what the client sends
                op = Request.parseDelimitedFrom(in);

                //if leaderboards
                if (op.getOperationType() == Request.OperationType.LEADER) {
                    String leaderBoardString = getLeaderBoard();
                    Response response = Response.newBuilder()
                        .setResponseType(Response.ResponseType.LEADER)
                        .setGreeting("Displaying Leaderboard...\n" + leaderBoardString) //TODO: display leaderboard
                        .build();
                    response.writeDelimitedTo(out);
                    Response leaderBoard = res.build();
                    leaderBoard.writeDelimitedTo(out);

                    for (Entry lead: leaderBoard.getLeaderList()){
                        System.out.println(lead.getName() + ": " + lead.getWins());
                    }

                    System.out.println("Task: Displayed Leaderboard");

                    //else if play game
                } else if (op.getOperationType() == Request.OperationType.NEW) {
                    System.out.println("Task: Started a Game for " +name);
                    game.newGame();
                    //TODO: implement playing game
                    //record the game into the leaderboards
                    Entry leader = Entry.newBuilder()
                        .setName(name)
                        .setWins(0)
                        //.setLogins(0)
                        .build();
                    res.addLeader(leader);
                    //else if quit
                } else if (op.getOperationType() == Request.OperationType.QUIT) {
                    //serverRunning = false; for testing purposes
                    System.out.println("The user " +name +"has quit.");

                }
            }


        //switch statement led to errors
            // switch (op.getOperationType()) {
            //     case(LEADER): //show leaderboards
            //         Response response = Response.newBuilder()
            //             .setResponseType(Response.ResponseType.LEADER)
            //             .setGreeting("Displaying Leaderboard...\n")
            //             .build();
            //         response.writeDelimitedTo(out);

            //         break;
            //     case(NEW): //start a new game
            //         game.newGame();
            //         break;
            //     case(QUIT): //quit the server
            //         break;
            // }

            // Example how to start a new game and how to build a response with the image which you could then send to the server
            // LINE 67-108 are just an example for Protobuf and how to work with the differnt types. They DO NOT
            // belong into this code. 
            // game.newGame(); // starting a new game

            // adding the String of the game to 
            // Response response2 = Response.newBuilder()
            //     .setResponseType(Response.ResponseType.TASK)
            //     .setImage(game.getImage())
            //     .setTask("Great task goes here")
            //     .build();

            // // On the client side you would receive a Response object which is the same as the one in line 70, so now you could read the fields
            // System.out.println("Task: " + response2.getResponseType());
            // System.out.println("Image: \n" + response2.getImage());
            // System.out.println("Task: \n" + response2.getTask());

            

            // building and Entry
            // Entry leader = Entry.newBuilder()
            //     .setName("name")
            //     .setWins(0)
            //     .setLogins(0)
            //     .build();

            // // building and Entry
            // Entry leader2 = Entry.newBuilder()
            //     .setName("name2")
            //     .setWins(1)
            //     .setLogins(1)
            //     .build();

            // res.addLeader(leader);
            // res.addLeader(leader2);

            // Response response3 = res.build();

            // for (Entry lead: response3.getLeaderList()){
            //     System.out.println(lead.getName() + ": " + lead.getWins());
            // }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (out != null)  out.close();
            if (in != null)   in.close();
            if (clientSocket != null) clientSocket.close();
        }
    }


    /**
     * returns a string of the leaderBoard formatted for user
     * 
     * @return
     */
    public String getLeaderBoard() {
        
        
        return "";
    }

    /**
     * Replaces num characters in the image. I used it to turn more than one x when the task is fulfilled
     * @param num -- number of x to be turned
     * @return String of the new hidden image
     */
    public String replace(int num){
        for (int i = 0; i < num; i++){
            if (game.getIdx()< game.getIdxMax())
                game.replaceOneCharacter();
        }
        return game.getImage();
    }


    /**
     * Writing a new entry to our log
     * @param name - Name of the person logging in
     * @param message - type Message from Protobuf which is the message to be written in the log (e.g. Connect) 
     * @return String of the new hidden image
     */
    public static void writeToLog(String name, Message message){
        try {
            // read old log file 
            Logs.Builder logs = readLogFile();

            // get current time and data
            Date date = java.util.Calendar.getInstance().getTime();

            // we are writing a new log entry to our log
            // add a new log entry to the log list of the Protobuf object
            logs.addLog(date.toString() + ": " +  name + " - " + message);

            // open log file
            FileOutputStream output = new FileOutputStream(logFilename);
            Logs logsObj = logs.build();

            // This is only to show how you can iterate through a Logs object which is a protobuf object
            // which has a repeated field "log"

            for (String log: logsObj.getLogList()){

                System.out.println(log);
            }

            // write to log file
            logsObj.writeTo(output);
        }catch(Exception e){
            System.out.println("Issue while trying to save");
        }
    }

    /**
     * Reading the current log file
     * @return Logs.Builder a builder of a logs entry from protobuf
     */
    public static Logs.Builder readLogFile() throws Exception{
        Logs.Builder logs = Logs.newBuilder();

        try {
            // just read the file and put what is in it into the logs object
            return logs.mergeFrom(new FileInputStream(logFilename));
        } catch (FileNotFoundException e) {
            System.out.println(logFilename + ": File not found.  Creating a new file.");
            return logs;
        }
    }


    public static void main (String args[]) throws Exception {
        Game game = new Game();

        if (args.length != 2) {
            System.out.println("Expected arguments: <port(int)> <delay(int)>");
            System.exit(1);
        }
        int port = 9099; // default port
        int sleepDelay = 10000; // default delay
        Socket clientSocket = null;
        ServerSocket serv = null;

        try {
            port = Integer.parseInt(args[0]);
            sleepDelay = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port|sleepDelay] must be an integer");
            System.exit(2);
        }
        try {
            serv = new ServerSocket(port);
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(2);
        }

        //change so that abrupt disconnect does not stop server
        // clientSocket = serv.accept();
        // SockBaseServer server = new SockBaseServer(clientSocket, game);
        // server.start();
        
        while(true) {
            Socket socket = serv.accept();
            SockBaseServer server = new SockBaseServer(socket, game);
            server.start();
        }

    }

    private static Response buildRepsonse(String result) {
        return null;
    }


}

