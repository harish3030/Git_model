import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;
public class server1 {

    private static ServerSocket serverSocket;
    private static Socket clientSocket = null;

    public static void main(String[] args) throws IOException {

        try {
            serverSocket = new ServerSocket(2000);
            System.out.println("Heyy!! Server started.");
        } catch (Exception e) {
            System.err.println("Port already in use.");
            System.exit(1);
        }
   
  
        while (true) {
            try {
                clientSocket = serverSocket.accept();
               System.out.println("Accepted connection : " + clientSocket);

                Thread t = new Thread(new ServiceClient1(clientSocket));

                t.start();

            } catch (Exception e) {
                System.err.println("Error in connection attempt.");
            }
        }
    }
}
