package chess.pieces;

import java.util.ArrayList;

import chess.*;
import chess.pieces.Piece;
import static chess.pieces.Piece.valid;

public class Queen extends Piece {

    public Queen(int color) {
            super(color);
    }

    @Override
    public String getImage() {
        if(color == Piece.WHITE)
            return imageFolder + "White_Queen.png";
        else
            return imageFolder + "Black_Queen.png";
    }

    @Override
    public ArrayList<Move> getMoves(Board board, int x, int y) {
        ArrayList<Move> moves = new ArrayList<>();
        /*
            [-1,-1] [-1, 0] [-1,1]
            [ 0, -1] [ 0, 0] [ 0,1]
            [ 1,-1] [ 1, 0] [ 1,1]
        */
        int[] dxValues = {-1, -1, -1, 0,  0,  1, 1, 1};
        int[] dyValues = {-1,  0,  1, -1, 1, -1, 0, 1};
        for (int index=0; index<dxValues.length; index++){
            int dx = dxValues[index];
            int dy = dyValues[index];
            for (int k = 1; k<8; k++){
                int newX = x+dx*k;
                int newY = y+dy*k;
                if(valid(newX, newY)) {
                if(board.getSquare(newX, newY).isOccupied()) {
                    if(board.getSquare(newX, newY).getPiece().color != this.color)
                        moves.add(new Move(x,y,newX,newY));	
                        break;
                    }
                    else{
                        moves.add(new Move(x,y,newX,newY));	
                    }
                }
            }
        }
        return moves;
    }
}