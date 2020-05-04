package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Player implements Runnable {
    String name;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Functie ce va trimite request-uri la server. Acestea pot fi de mai multe feluri:
     *  - initial putem crea un joc, sa ne alaturam intr-un joc sau sa aflam ce jocuri disponibile sunt in acel moment
     *  - apoi avem doua optiuni: putem vedea cum arata tabla jocului sau putem face o miscare (prin submit move # #)
     * While(flag) este o bucla care se executa atata timp cat nu se da o comanda valida.
     * In urmatoarele structuri de decizie if, decidem cum sa actionam in functie de ce comanda primim
     * @param outSocket canalul de scriere in socket
     * @param in canalul de citire din socket
     * @param socket socket-ul. Avem nveoie de el deoarece la un moment dat se termina jocul si trebuie sa apelam: socket.close()
     * @throws IOException
     */
    public static void sendRequest(PrintWriter outSocket, BufferedReader in, Socket socket) throws IOException {
        Scanner scanner = new Scanner(System.in);
        boolean flag = false;
        String request = "";
        while(!flag) {
            request = scanner.nextLine();
            flag = true;
            if(!request.toLowerCase().startsWith("create") && !request.toLowerCase().startsWith("join") && !request.toLowerCase().startsWith("submit") && !request.toLowerCase().startsWith("show")) {
                System.out.println("Coamnda invalida");
                flag = false;
            }
        }
        outSocket.println(request);
        if(request.toLowerCase().startsWith("show table")) {
            String response = in.readLine();
            int i = 0, j = 29;
            for(int k = 0; k < 15; k++) {
                System.out.println(response.substring(i, j));
                i += 30;
                j += 30;
            }
            sendRequest(outSocket, in, socket);
        }
        else if (request.toLowerCase().startsWith("submit")) {
            String response = in.readLine();
            if(response.startsWith("Game over")) {
                System.out.println(response);
                socket.close();
                System.exit(0);
            }
            System.out.println(response);
            if(response.startsWith("Casuta ocupata sau invalida")) {
                sendRequest(outSocket, in, socket);
            }
        }
        else if(request.toLowerCase().startsWith("show games")) {
            String response = in.readLine();
            System.out.println(response);
            sendRequest(outSocket, in, socket);
        }
    }

    /**
     * Functie override a interfetei Runnable care defineste comportamentul unui client. Acesta trimite requesturi, citeste
     *  raspunsuri de la server si le afiseaza
     */
    @Override
    public void run() {
        String serverAddress = "127.0.0.1";
        int PORT = 8100;

        try (Socket socket = new Socket(serverAddress, PORT);
             PrintWriter outSocket =  new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader (
                     new InputStreamReader(socket.getInputStream()))
        )
        {
            sendRequest(outSocket, in, socket);
            String response = in.readLine();
            System.out.println(response);
            response = in.readLine();
            System.out.println(response);
            while (true) {
                response = in.readLine();
                if(response.startsWith("Game over")) {
                    System.out.println(response);
                    socket.close();
                    System.exit(0);
                }
                System.out.println(response);
                sendRequest(outSocket, in, socket);
            }
        } catch (IOException e) {
            System.err.println("No server listening... " + e);
        }
    }

}
