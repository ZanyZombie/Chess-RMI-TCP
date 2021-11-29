package chess.pieces;

import chess.Board;
import chess.Move;
import java.util.ArrayList;


public class Pawn extends Piece{
    public Pawn(int color) {
        super(color);
    }

    @Override
    public String getImage() {
        if(color == Piece.WHITE)
            return imageFolder + "White_Pawn.png";
        else
            return imageFolder + "Black_Pawn.png";
    }

    @Override
    public ArrayList<Move> getMoves(Board board, int x, int y) {
        ArrayList<Move> moves = new ArrayList<>();
        //1st move
        if (!hasMoved){
            if(!board.getSquare(x + 2*color, y).isOccupied() 
                && !board.getSquare(x + 1*color, y).isOccupied()){
                moves.add(new Move(x, y, x + 2*color,y, Move.PAWN_FIRST_MOVE));
            }
        }
        //move forward
        if(!board.getSquare(x + color, y).isOccupied()){
            moves.add(new Move(x, y, x + color,y));
        }
        //take 
        for (int dy = -1; dy<2; dy+=2){
            if (valid(x+color, y+dy))
            if(board.getSquare(x+color, y+dy).isOccupied()){
                if (board.getSquare(x+color, y+dy).getPiece().getColor() != color)
                    moves.add(new Move(x, y, x+color,y+dy));
            }
        }
        //en passant
        if (board.getLastMove().getType() == Move.PAWN_FIRST_MOVE){
            if (board.getLastMove().getxAfter() == x){
                moves.add(new Move(x, y, x + color, board.getLastMove().getyAfter(), Move.ENPASSANT));
            }
        }
        //promote
        for  (Move move: moves){
            if (move.getxAfter() == 0 || move.getxAfter() == 7){
                move.setType(Move.PROMOTE_TO_QUEEN);
            }
        }
        return moves;
    }
    
}
