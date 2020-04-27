package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread{
    private Socket socket = null ;

    /**
     * Asignam socketul primit
     * @param socket se va folosi pentru ca deschide canalele de citire/scriere
     */
    public ClientThread (Socket socket) {
        this.socket = socket ;
    }

    /**
     * Aici asteptam request-uri de la server si scriem in soket raspunsurile.
     */
    public void run () {
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            while(true) {
                String request = in.readLine();
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                String raspuns = "Server received the request " + request;
                out.println(raspuns);
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("Communication error... " + e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println (e);
            }
        }
    }
}
