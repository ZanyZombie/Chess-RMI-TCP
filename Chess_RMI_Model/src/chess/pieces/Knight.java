package chess.pieces;

import java.util.ArrayList;

import chess.*;
import chess.pieces.Piece;
import static chess.pieces.Piece.valid;

public class Knight extends Piece {

    public Knight(int color) {
        super(color);
    }

    @Override
    public String getImage() {
        if(color == Piece.WHITE)
            return imageFolder + "White_Knight.png";
        else
            return imageFolder + "Black_Knight.png";
    }

    @Override
    public ArrayList<Move> getMoves(Board board, int x, int y) {
        ArrayList<Move> moves = new ArrayList<>();
        /*
            [  ][01][  ][03][  ]
            [10][  ][  ][  ][14]
            [  ][  ][22][  ][  ]
            [30][  ][  ][  ][34]
            [  ][41][  ][43][  ]

        */
        int[] dxValues = {-2, -2, -1,-1, 1, 1, 2, 2};
        int[] dyValues = {-1,  1, -2, 2,-2, 2, -1, 1};
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

        return moves;
    }
}