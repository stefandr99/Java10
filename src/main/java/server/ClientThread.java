package server;

import freemarker.template.*;
import game.Board;
import game.Game;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ClientThread extends Thread{
    private Socket socket = null;
    private Game game;
    int culoare = 1;
    private boolean inGame = false;
    boolean correctMove = false;

    /**
     * Asignam socketul primit
     * @param socket se va folosi pentru ca deschide canalele de citire/scriere
     */
    public ClientThread (Socket socket) {
        this.socket = socket ;
    }

    /**
     * Jocul se va desfasura cu ajutorul sincronizarii si punerii de lacate pe tabla de joc.
     * Fiecare jucator isi asteapta randul in prima bucla while
     * Daca nu exista un castigator, este anuntat ca e randul lui si acesta poate face o miscare
     * Pana nu se face o miscare corecta (i.e. se da o comanda care plaseaza o piesa pe tabla intr-un loc liber), se tot cere un request.
     * De asemenea in acest timp el poate cere si configuratia tablei care, evident, reprezinta o miscare gresita si nu iese din while
     *
     * @param in
     * @param out
     * @throws IOException
     * @throws TemplateException
     */
    public void playGame(BufferedReader in, PrintWriter out) throws IOException, TemplateException {

        synchronized (game.getGameBoard()) {
            while (game.getGameBoard().getTurn() != culoare) {
                try {
                    game.getGameBoard().wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(!game.isWin()) {
                out.println("Este randul tau!");
                out.flush();
            }
            else {
                out.println("Game over! Ai pierdut!");
                out.flush();
            }
            correctMove = false;
            while(!correctMove) {
                String request = in.readLine();
                if (request.toLowerCase().startsWith("submit")) {
                    Scanner sc = new Scanner(request).useDelimiter("\\D+");
                    int i = sc.nextInt();
                    int j = sc.nextInt();

                    if (game.getGameBoard().isFree(i, j)) {
                        if (culoare == 1) game.getGameBoard().placeWhite(i, j);
                        else game.getGameBoard().placeBlack(i, j);
                        correctMove = true;

                        if(game.existsWinner()) {
                            out.println("Game over! Ai castigat!");
                            out.flush();
                            game.setWin(true);

                            freemarker.template.Configuration cfg = new Configuration();

                            cfg.setClassForTemplateLoading(ClientThread.class, "/");
                            cfg.setIncompatibleImprovements(new Version(2, 3, 20));
                            cfg.setDefaultEncoding("UTF-8");
                            cfg.setLocale(Locale.US);
                            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

                            Map<String, Object> input = new HashMap<String, Object>();
                            input.put("title", "The result");
                            input.put("linie", game.getBoardAsString());

                            Template template = cfg.getTemplate("gomoku.ftl");
                            Writer fileWriter = new FileWriter(new File("gomoku.html"));
                            try {
                                template.process(input, fileWriter);
                            } finally {
                                fileWriter.close();
                            }
                        }
                        else {
                            out.println("Ai mutat.");
                            out.flush();
                        }
                        if(culoare == 1)
                            game.getGameBoard().setTurn(2);
                        else game.getGameBoard().setTurn(1);
                        game.getGameBoard().notify();
                    } else {
                        out.println("Casuta ocupata sau invalida. Te rugam alege alta!");
                        out.flush();
                    }
                }
                else if(request.toLowerCase().startsWith("show table")) {
                    String response = "";
                    for(int i = 0; i < 15; i++) {
                        for(int j = 0; j < 15; j++) {
                            response += game.getGameBoard().getBoard()[i][j] + " ";
                        }
                    }
                    out.println(response);
                    out.flush();
                }
            }
        }
    }

    /**
     * Functie ce va raspunde tuturor cererilor clientului.
     * In functie daca este in joc sau nu, se trateaza diferit comenzile. Se poate observa in if(!inGame)...
     *
     */
    public void run () {
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            while(true) {
                if(!this.inGame) {
                    String request = in.readLine();
                    if (request.toLowerCase().startsWith("create")) {
                        int gameId = GameServer.games.size() + 1;
                        Board board = new Board();
                        this.game = new Game(gameId, board);
                        GameServer.games.add(game);
                        out.println("Ai creat un joc! Vei fi culoarea alb. Asteapta un oponent...");
                        out.flush();
                        while (game.getPlayers() == 1) ;
                        out.println("Start!");
                        out.flush();
                        this.inGame = true;
                    } else if (request.toLowerCase().startsWith("join")) {
                        int myId = new Scanner(request).useDelimiter("\\D+").nextInt();
                        this.game = GameServer.findById(myId);
                        out.println("Ai intrat in jocul cu id-ul " + myId + ". Vei fi culoarea negru.");
                        out.flush();
                        this.culoare = 2;
                        Thread.sleep(3000);
                        game.incrementPlayers();
                        out.println("Start!");
                        out.flush();
                        this.inGame = true;
                    } else if (request.toLowerCase().startsWith("show")) {
                        if (!(GameServer.games.isEmpty())) {
                            String showResponse = "Camerele de joc disponibile sunt: ";
                            String disponibil = "";
                            for (Game g : GameServer.games) {
                                if (g.getPlayers() == 1) {
                                    disponibil += g.getId() + " ";
                                }
                            }
                            if(!disponibil.isEmpty()) {
                                out.println(showResponse + disponibil);
                            }
                            else {
                                out.println("Niciun joc existent. Creeaza tu unul!");
                            }
                            out.flush();
                        } else {
                            out.println("Niciun joc existent. Creeaza tu unul!");
                            out.flush();
                        }
                    }
                }
                else {
                    playGame(in, out);
                }
            }
        } catch (IOException | InterruptedException | TemplateException e) {
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
