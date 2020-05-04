package game;

import java.util.ArrayList;
import java.util.List;


public class Game {
    int id;
    Board gameBoard;
    volatile int players;
    volatile boolean win = false;

    /**
     * id - id-ul jocului
     * gameBoard - tabla de joc
     * players - nr. de jucatori activi in camera de joc
     * win - true = jocul a fost castigat, false = altfel
     * @param id
     * @param gameBoard
     */
    public Game(int id, Board gameBoard) {
        this.id = id;
        this.gameBoard = gameBoard;
        players = 1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Board getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(Board gameBoard) {
        this.gameBoard = gameBoard;
    }

    public boolean existsWinner() {
        return gameBoard.verifyWinner();
    }

    public int getPlayers() {
        return players;
    }

    public void setPlayers(int activePlayers) {
        this.players = activePlayers;
    }
    public void incrementPlayers() {
        if(players < 2)
            players++;
    }

    public boolean isWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    /**
     * Functie ce genereaza tabla de joc ca un String pe randuri.
     * Avem nevoie pentru generarea HTML-ului
     * @return lista reprezentand board-ul
     */
    public List<String> getBoardAsString() {
        List<String> myList = new ArrayList<>();
        for(int i = 0; i < 15; i++) {
            String myRow = "";
            for(int j = 0 ; j < 15; j++) {
                myRow += gameBoard.getBoard()[i][j] + " ";
            }
            myList.add(myRow);
        }
        return myList;
    }
}
