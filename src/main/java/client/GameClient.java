package client;

import game.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class GameClient {

    /**
     * Se lanseaza un fir de execute reprezentand un client cu un nume.
     * @param args
     * @throws IOException
     */
    public static void main (String[] args) throws IOException {
        Runnable player = new Player("Stefan");
        new Thread(player).start();
    }
}
