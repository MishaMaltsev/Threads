package taskone;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Makes the server multithreaded
 * 
 * allow unbounded incoming connections to the server
 * no client should block
 * shared state of the string list should be properly managed
 */
class ThreadedServer extends Thread {
    private Socket sock;
    private int id;
    StringList strings = new StringList();

    public ThreadedServer(Socket sock, int id) {
        this.sock = sock;
        this.id = id;
    }

    //run method
    public void run() {
        try {
            Performer performer = new Performer(sock, strings);
            performer.doPerform();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        //set default values
        Socket sock = null;
        int id = 0;
        int inputPortNum = 0;
        //try catch for reading in
        try {

            if(args.length != 1) {
                System.out.println("Usage: gradle runTask2 --args=<port num>");
                System.exit(0);
            }
    
            //try catch for getting input from command line
            try {
                inputPortNum = Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                System.out.println("[Port] must be an integer");
                System.exit(1);
            }
            
            //create new server instance for the input port num
            ServerSocket server = new ServerSocket(inputPortNum);
            System.out.println("Server Started...");
            while(true) {
                System.out.println("Threaded server waiting for connects on port " +inputPortNum);
                sock = server.accept();
                System.out.println("Threaded server connected to client id: " + id);
    
                //create the thread
                ThreadedServer servThread = new ThreadedServer(sock, id++);
                //run thread
                servThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(sock != null) {
                sock.close();
            }    
        } //end finally
        
        
    }

}