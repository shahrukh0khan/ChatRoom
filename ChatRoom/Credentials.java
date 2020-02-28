package ChatRoom;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Credentials {
    JFrame frame = new JFrame("Chatter");

    public static InetAddress inetAddress;


//    static {
//        try {
//
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//    }

    public String IP() throws UnknownHostException {

        inetAddress = InetAddress.getLocalHost();
        return inetAddress.toString();
    }






    public String getPort() throws IOException {

        return JOptionPane.showInputDialog(
                frame,
                "Port No:",
                "Port",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    public String getIP() throws IOException {

        return JOptionPane.showInputDialog(
                frame,
                "IP :",
                "IP",
                JOptionPane.PLAIN_MESSAGE
        );


    }



}

