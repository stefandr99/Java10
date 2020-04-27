package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
    /**
     * Portul la care ascultam
     */
    public static final int PORT = 8100;

    /**
     * Constructorul in care creem un ServerScoket. Ii asignam apoi portul ales.
     * Creem un socket de comunicare cand s-a conectat un client si creem un nou thread.
     * @throws IOException
     */
    public GameServer() throws IOException {
        ServerSocket serverSocket = null ;
        try {
            serverSocket = new ServerSocket(PORT);
            while (true) {
                System.out.println ("Waiting for a client ...");
                Socket socket = serverSocket.accept();
                new ClientThread(socket).start();
            }
        } catch (IOException e) {
            System.err. println ("Ooops... " + e);
        } finally {
            serverSocket.close();
        }
    }
    public static void main ( String [] args ) throws IOException {
        GameServer server = new GameServer();
    }
}
