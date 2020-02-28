package ChatRoom;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * A multithreaded chat room server. When a client connects the server requests a screen
 * name by sending the client the text "SUBMITNAME", and keeps requesting a name until
 * a unique one is received. After a client submits a unique name, the server acknowledges
 * with "NAMEACCEPTED". Then all messages from that client will be broadcast to all other
 * clients that have submitted a unique screen name. The broadcast messages are prefixed
 * with "MESSAGE".
 * <p>
 * This is just a teaching example so it can be enhanced in many ways, e.g., better
 * logging. Another is to accept a lot of fun commands, like Slack.
 */
public class ChatServer {

    // All client names, so we can check for duplicates upon registration.
    public static ArrayList<String> names = new ArrayList<>();
    //IDs of all the group memebers are being stored here
    public static ArrayList<String> IDs = new ArrayList<>();


    public static ArrayList<String> IPs = new ArrayList<>();
    public static ArrayList<String> Ports = new ArrayList<>();
    // The set of all the print writers for all the clients, used for broadcast.
    private static Set<PrintWriter> writers = new HashSet<>();


    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running...");
        // System.out.println("IP Address:- " + inetAddress.getHostAddress());

        ExecutorService pool = Executors.newFixedThreadPool(500);
        //server listening on this socket
        try (ServerSocket listener = new ServerSocket(59001)) {
            // System.out.println(listener);
            while (true) {
                pool.execute(new Handler(listener.accept()));


            }

        }


    }


    /**
     * The client handler task.
     */
    public static class Handler implements Runnable {
        public String name, ID, getport;
        //clients socket
        private Socket socket;
        //taking user inputs
        private Scanner in;
        //sending it to sockets
        private PrintWriter out;
        private String IP;
        private String port;

        /**
         * Constructs a handler thread, squirreling away the socket. All the interesting
         * work is done in the run method. Remember the constructor is called from the
         * server's main method, so this has to be as short as possible.
         */
        public Handler(Socket socket) {
            this.socket = socket;
        }

        /**
         * Services this thread's client by repeatedly requesting a screen name until a
         * unique one has been submitted, then acknowledges the name and registers the
         * output stream for the client in a global set, then repeatedly gets inputs and
         * broadcasts them.
         */
        public void run() {
            try {

                //user input being taken from above specified socket
                in = new Scanner(socket.getInputStream());
                //sending output to socket
                out = new PrintWriter(socket.getOutputStream(), true);

                // Keep requesting an ID until we get a unique one.
                while (true) {
                    out.println("SUBMITID");
                    ID = in.nextLine();
                    if (ID == null) {
                        return;
                    }
                    synchronized (IDs) {
                        if (!ID.isEmpty() && !IDs.contains(ID)) {
                            IDs.add(ID);

                            break;
                        }
                    }
                }
                out.println("IDACCEPTED " + ID);
                //keep requesting name until we get a unique one

                while (true) {
                    out.println("SUBMITNAME");
                    name = in.nextLine();
                    if (name == null) {
                        return;
                    }
                    synchronized (names) {
                        if (!name.isEmpty() && !names.contains(name)) {
                            names.add(name);
                            break;
                        }
                    }
                }


                out.println("NAMEACCEPTED " + name);


                for (PrintWriter writer : writers) {
                    writer.println("MESSAGE " + name+ " has joined");

                }
                writers.add(out);

                File member1 = new File(names.get(0));
                if (member1.createNewFile()) {
                    System.out.println("File is created!");
                } else {
                    System.out.println("File already exists.");
                }

                Coordinator contactlist = new Coordinator();
                File cl = Coordinator.contactlist();

                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("E yyyy/MM/dd HH-mm-ss");

                Credentials IP = new Credentials();
                IPs.add(IP.IP());
                // Accept messages from this client and broadcast them.
                while (true) {
                    String input = in.nextLine();
                    if (input.toLowerCase().startsWith("/quit")) {
                        return;
                    }


                    for (PrintWriter writer : writers) {
                        //broadcasting messages to group
                        if(name==names.get(0)){
                            writer.println("MESSAGE " + name + "(coordinator)");
                        }
                        if (input.toLowerCase().startsWith("list")) {
                            writer.println("list " +"ID: " + IDs + "Name: " +  names +"IP: " + IPs + "\n");
                        }

                    }

                    for (PrintWriter writer : writers) {
                        //broadcasting messages to group

                        writer.println("MESSAGE " + name + ": " + input);
                    }


                    FileWriter write = new FileWriter(member1, true);
                    write.write(name + " : " + input + "  " + sdf.format(date) + "\n");
                    write.close();

                    FileWriter write1 = new FileWriter(cl, true);
                    write1.write(name + " : " + ID + "  " + "\n");
                    write1.close();

//                    if (name != null && names.remove(name) && IDs.remove(ID)) {
//
//                    }

                }


            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if (out != null) {
                    writers.remove(out);
                }
                if (name != null) {
                    System.out.println(name + " is leaving");
                    names.remove(name);
                    for (PrintWriter writer : writers) {
                        writer.println("leaving");
                        writer.println("MESSAGE " + name + " has left");
                    }

                }
                try {
                    socket.close();
                } catch (IOException e) {
                }

            }
        }
    }
}
