package chess.pieces;

import chess.Board;
import chess.Move;
import java.util.ArrayList;

public abstract class Piece {
    public static final int WHITE = 1;
    public static final int BLACK = -1;
    protected String imageFolder = "C:/Users/Administrator/Documents/NetBeansProjects/Chess_RMI_Model/images/";
    
    int color;

    boolean hasMoved;

    public Piece(int color) {
        this.color = color;
        this.hasMoved = false;
    }
    
    public int getColor() {
        return color;
    }    

    public abstract ArrayList<Move> getMoves(Board board, int x, int y);
    
    public abstract String getImage();

    static public boolean valid(int x, int y) {
        return !(x < 0 || x > 7 || y < 0 || y > 7);
    }
    
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public boolean isHasMoved() {
        return hasMoved;
    }
}