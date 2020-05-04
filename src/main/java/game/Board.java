package game;

import java.util.Arrays;

/**
 * White = 1
 * Black = 2
 */
public class Board {
    int[][] board;
    int turn;

    public Board() {
        this.board = new int[15][15];
        for(int[] row : this.board)
            Arrays.fill(row, 0);
        turn = 1;
    }

    /**
     * Culoarea alba va avea valoarea 1 in matricea de joc
     * @param i linia
     * @param j coloana
     */
    public synchronized void placeWhite(int i, int j) {
        this.board[i][j] = 1;
    }

    /**
     * Culoarea neagra va avea valoarea 2 in matricea de joc
     * @param i linia
     * @param j coloana
     */
    public synchronized void placeBlack(int i, int j) {
        this.board[i][j] = 2;
    }

    /**
     * Verificam daca este pe tabla pozitia [i][j] si daca e libera
     * @param i linia
     * @param j coloana
     * @return true - daca e libera si pe tabla, false - altfel
     */
    public boolean isFree(int i, int j) {
        if(i < 0 || i > 14 || j < 0 || j > 14) return false;
        else return this.board[i][j] == 0;
    }

    /**
     * Algoritm de genul fill, putin modificat.
     * Am facut 2 vectori de deplasare care realizeaza deplasarea in toate cele 8 directii posibile.
     * @param i linia actuala
     * @param j coloana actuala
     * @param value valoarea de pe [i][j]
     * @return true - daca exista castigator, false altfel
     */
    private boolean verify(int i, int j, int value) {
        int[] di = {-1, -1, 0, 1, 1, 1, 0, -1};
        int[] dj = {0, 1, 1, 1, 0, -1, -1, -1};
        int l;
        for(int k = 0; k < 8; k++) {
            int iAux = i, jAux = j;
            for(l = 0; l < 5; l++) {
                if(this.board[iAux][jAux] != value)
                    break;
                iAux += di[k];
                jAux += dj[k];
                if(iAux < 0 || iAux > 14 || jAux < 0 || jAux > 14)
                    break;
            }
            if(l == 5) return true;
        }
        return false;
    }

    /**
     * Functie ca va apela verificarea conditiei de castigare pentru toate cele 225 pozitii de pe tabla
     * @return true daca exista castigator, false altfel
     */
    public boolean verifyWinner() {
        for(int i = 0; i < 15; i++)
            for(int j = 0; j < 15; j++) {
                if(this.board[i][j] != 0)
                    if(verify(i, j, this.board[i][j]))
                        return true;
            }
        return false;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }
}
