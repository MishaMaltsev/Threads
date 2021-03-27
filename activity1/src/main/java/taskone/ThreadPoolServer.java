package taskone;

import java.io.IOError;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Makes server multithreaded with limited pool of connections
 */
class ThreadPoolServer extends Thread {

    private Socket sock;
    private int id, runningThreads;

    StringList strings = new StringList();


    public ThreadPoolServer(Socket sock, int id, int runningThreads) {
        this.sock = sock;
        this.id = id;
        this.runningThreads = runningThreads;
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
        //set defaults
        Socket sock = null;
        int id = 0;
        int inputPortNum = 0;
        int poolCount = 2;

        try {
            if(args.length != 2) {
                System.out.println("Usage: gradle runTask3 Pport=<port num>");
                System.exit(0);
            }

            //try  catch for input from command line
            try {
                inputPortNum = Integer.parseInt(args[0]);
                poolCount = Integer.parseInt(args[1]);
            } catch (NumberFormatException nfe) {
                System.out.println("[Port] and [Pool] must be integers");
                System.exit(1);
            }
            //create new server instance for the input port num
            ServerSocket server = new ServerSocket(inputPortNum);
            int runningThreads = 0;
            while(true) {
                if(runningThreads < poolCount) {
                    System.out.println("Threaded server waiting for connects on port " +inputPortNum);
                    sock = server.accept();
                    System.out.println("Threaded server connected to client id: " + id);
                    ThreadPoolServer servThread = new ThreadPoolServer(sock, id++, runningThreads++);
                    servThread.start();
                } else  if (runningThreads >= poolCount) {
                    //System.out.println("Too many instances running currently, try again later.");
                }
                //need to reduce runningthreads by 1 when one disconnects
            } //end while
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(sock != null) {
                sock.close();
            }
        }

    }


}