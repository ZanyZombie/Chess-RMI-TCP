package chess;

import chess.pieces.Piece;

public class Square {
    private boolean occupied;
    private Piece piece;

    public Square() {
        occupied = false;
    }
    
    public Square(Piece piece) {
        occupied = true;
        this.piece = piece;
    }
    
    public boolean isOccupied() {
        return occupied;
    }
    
    public Piece getPiece() {
        return piece;
    }
    
    public void setPiece(Piece piece) {
        this.piece = piece;
    }
    
}