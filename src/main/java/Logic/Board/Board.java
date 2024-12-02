package Logic.Board;

import Logic.Piece.ChessPiece;

import java.util.*;

public class Board {
    private int size;
    private int[][] board; // contains ID of pieces
    private HashMap<String,List<ChessPiece>> chessPieces;
    private boolean whiteTurn=true;//true first color
    // list list chesspiece


    public Board(int size) {
        this.size = size;
        board = new int[size][size];
    }

    public int[][] getBoard() {
        return board;
    }

    public HashMap<String, List<ChessPiece>> getChessPieces() {
        return chessPieces;
    }

    public void setChessPieces(HashMap<String, List<ChessPiece>> chessPieces) {
        this.chessPieces = chessPieces;
    }

    public boolean getFirstColorTurn() {
        return whiteTurn;
    }

    public void setWhiteTurn(boolean whiteTurn) {
        this.whiteTurn = whiteTurn;
    }

    public Board clone(){
        Board clone = new Board(size);
        clone.setWhiteTurn(whiteTurn);
//        clone.setChessPieces();
        return clone;
    }

    public int getSize() {
        return size;
    }

    public boolean isWhiteTurn() {
        return whiteTurn;
    }


}
