package chess.pieces;

import java.util.ArrayList;

import chess.Board;
import chess.Move;

public class King extends Piece {

    public King(int color) {
        super(color);
    }

    @Override
    public String getImage() {
        if(color == Piece.WHITE)
            return imageFolder + "White_King.png";
        else
            return imageFolder + "Black_King.png";
    }

    @Override
    public ArrayList<Move> getMoves(Board board, int x, int y) {
        ArrayList<Move> moves = new ArrayList<>();
        /*
            [-1,-1] [-1, 0] [-1,1]
            [ 0,-1] [ 0, 0] [ 0,1]
            [ 1,-1] [ 1, 0] [ 1,1]
        */
        int[] dxValues = {-1, -1, -1, 0,  0,  1, 1, 1};
        int[] dyValues = {-1,  0,  1, -1, 1, -1, 0, 1};
        for (int index =0; index< dxValues.length; index++){
            int newX = x+dxValues[index];
            int newY = y+dyValues[index];
            if(valid(newX, newY))
                if (!board.getSquare(newX, newY).isOccupied()){
                    moves.add(new Move(x, y, newX,newY));
                }else if (board.getSquare(newX, newY).getPiece().getColor() != this.color){
                    moves.add(new Move(x, y, newX,newY));
                }
        }
        //Castling
        if(!hasMoved){
            //Queen side
            if (!board.getSquare(x, y+1).isOccupied() //queen
                && !board.getSquare(x, y+2).isOccupied() //bishop
                && !board.getSquare(x, y+3).isOccupied() //knight
                && board.getSquare(x, y+4).isOccupied()){ //rook
                    if (board.getSquare(x, y+4).getPiece() instanceof Rook 
                        && !((Rook)board.getSquare(x, y+4).getPiece()).isHasMoved())  
                            moves.add(new Move(x, y, x, y+2, Move.CASTLE));
            }
            
            //King side
            if (!board.getSquare(x, y-1).isOccupied() //bishop
                && !board.getSquare(x, y-2).isOccupied() //knight
                && board.getSquare(x, y-3).isOccupied()){ //rook
                    if (board.getSquare(x, y-3).getPiece() instanceof Rook 
                        && !((Rook)board.getSquare(x, y-3).getPiece()).isHasMoved())  
                            moves.add(new Move(x, y, x, y-2, Move.CASTLE));
            }
        }
        return moves;
    }
}