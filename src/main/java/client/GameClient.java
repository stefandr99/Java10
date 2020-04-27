package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class GameClient {
    /**
     * Creem un socket si ii asignam portul si adresa locala
     * Creem toate canalele prin care vom scrie requesturi si vom primi raspunsuri
     * Daca e exit, inchidem clientul
     * @param args
     * @throws IOException
     */
    public static void main (String[] args) throws IOException {
        String serverAddress = "127.0.0.1";
        int PORT = 8100;
        try (Socket socket = new Socket(serverAddress, PORT);
             PrintWriter out =  new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader (
                     new InputStreamReader(socket.getInputStream()))
        )
        {
            while (true) {
                Scanner scanner = new Scanner(System.in);
                String request = scanner.nextLine();
                if (request.equals("exit")) {
                    socket.close();
                    System.exit(0);
                }
                out.println(request);
                String response = in.readLine();
                System.out.println(response);
            }
        } catch (UnknownHostException e) {
            System.err.println("No server listening... " + e);
        }
    }
}
