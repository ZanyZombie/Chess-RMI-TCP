package chess;

import chess.pieces.*;
import static chess.pieces.Piece.valid;
import java.util.ArrayList;

public class Board {
    public static final int PLAYING = 0;
    public static final int STALEMATE = 1;
    public static final int CHECKMATE = 2;
    public static final int SURRENDER = 3;
    public static final int DRAW = 4;
    public static final int TIMEOUT = 5;

    private Square[][] squares;
    
    private Move lastMove;
    
    private int currentTurn;
    
    private ArrayList<Move> legalMoves;
    
    private int status;
    
    public Board() {
        this.squares = new Square[8][8];
        this.lastMove = new Move();
        this.currentTurn = Piece.WHITE;
        this.status = PLAYING;
        for(int row = 0; row<8; row++){
            for (int col = 0; col< 8; col++){
                squares[row][col] = new Square();
            }
        }
        
        squares[0][0] = new Square(new Rook(Piece.WHITE));
        squares[0][1] = new Square(new Knight(Piece.WHITE));
        squares[0][2] = new Square(new Bishop(Piece.WHITE));
        squares[0][3] = new Square(new King(Piece.WHITE));
        squares[0][4] = new Square(new Queen(Piece.WHITE));
        squares[0][5] = new Square(new Bishop(Piece.WHITE));
        squares[0][6] = new Square(new Knight(Piece.WHITE));
        squares[0][7] = new Square(new Rook(Piece.WHITE));

        squares[7][0] = new Square(new Rook(Piece.BLACK));
        squares[7][1] = new Square(new Knight(Piece.BLACK));
        squares[7][2] = new Square(new Bishop(Piece.BLACK));
        squares[7][3] = new Square(new King(Piece.BLACK));
        squares[7][4] = new Square(new Queen(Piece.BLACK));
        squares[7][5] = new Square(new Bishop(Piece.BLACK));
        squares[7][6] = new Square(new Knight(Piece.BLACK));
        squares[7][7] = new Square(new Rook(Piece.BLACK));
        
        for (int col = 0; col<8; col++){
            squares[1][col] = new Square(new Pawn(Piece.WHITE));
            squares[6][col] = new Square(new Pawn(Piece.BLACK));
        }
        
        findLegalMoves(currentTurn);
    }


    public ArrayList<Move> findThreats(int xRoot, int yRoot ) {
        Piece root = squares[xRoot][yRoot].getPiece();
        ArrayList<Move> threats = new ArrayList<>();

        //find knight threats
        int[] xKnight = {-2, -2, -1,-1, 1, 1, 2, 2};
        int[] yKnight = {-1,  1, -2, 2,-2, 2, -1, 1};
        for (int index =0; index< xKnight.length; index++){
            int newX = xRoot+xKnight[index];
            int newY = yRoot+yKnight[index];
            if(valid(newX, newY)){
                if (squares[newX][newY].isOccupied()){
                    Piece piece = squares[newX][newY].getPiece();
                    if (piece.getColor() != root.getColor() && piece instanceof Knight){
                            threats.add(new Move(newX, newY, xRoot,yRoot));
                    }
                }
            }
        }//end for index
        
        //find straight line threats
        int[] xRook = {-1,  0, 0, 1};
        int[] yRook = { 0, -1, 1, 0};
        for (int index=0; index<xRook.length; index++){
            int dx = xRook[index], dy = yRook[index];
            for (int k = 1; k<8; k++){
                int newX = xRoot+dx*k, newY = yRoot+dy*k;
                if(valid(newX, newY)) {
                    if(squares[newX][newY].isOccupied()) {
                        Piece piece = squares[newX][newY].getPiece();
                        if (piece.getColor() != root.getColor() 
                            && (piece instanceof Rook || piece instanceof Queen)){
                                threats.add(new Move(newX, newY, xRoot,yRoot));
                        }
                        break;
                    }
                }
            }//end for k
        }//end for index
        
        //find diagonal line threats
        int[] xBishop = {-1, -1, 1, 1};
        int[] yBishop = {-1, 1, -1, 1};
        for (int index=0; index<xRook.length; index++){
            int dx = xBishop[index], dy = yBishop[index];
            for (int k = 1; k<8; k++){
                int newX = xRoot+dx*k, newY = yRoot+dy*k;
                if(valid(newX, newY)) {
                    if(squares[newX][newY].isOccupied()) {
                        Piece piece = squares[newX][newY].getPiece();
                        if (piece.getColor() != root.getColor() 
                            && (piece instanceof Bishop || piece instanceof Queen)){
                                threats.add(new Move(newX, newY, xRoot,yRoot));
                        }
                        break;
                    }
                }
            }//end for k
        }//end for index
        // find pawn threats
        for (int dy = -1; dy<2; dy+=2){
            if (valid(xRoot+root.getColor(), yRoot+dy))
            if(squares[xRoot+root.getColor()][yRoot+dy].isOccupied()){
                if (squares[xRoot+root.getColor()][yRoot+dy].getPiece().getColor() != root.getColor()
                        && squares[xRoot+root.getColor()][yRoot+dy].getPiece() instanceof Pawn)
                    threats.add(new Move(xRoot+root.getColor(), yRoot+dy, xRoot,yRoot));
            }
        }
        
        return threats;
    }
    
    private void findLegalMoves(int color) {   
        this.legalMoves = new ArrayList<>();

        //find king
        int xKing = -1, yKing = -1;
        for(int row = 0; row < 8; row++){
            for(int col = 0; col < 8; col++) {
                Piece piece = squares[row][col].getPiece();
                if(piece != null && 
                    piece.getColor() == color &&
                    piece instanceof King) {
                        xKing = row; yKing = col;
                }
            }
        }
        
        ArrayList<Move> threats = findThreats(xKing, yKing);        
        
        for (int row=0; row<8; row++){
            for(int col=0; col<8; col++){
                Piece piece = squares[row][col].getPiece();
                if (piece!=null && piece.getColor() == color){
                    ArrayList<Move> moves = piece.getMoves(this, row, col);
                        for (Move move: moves){
                            if (move.getType() == Move.CASTLE){
                                if(move.getyAfter() == 1){//king side
                                    if (threats.size() == 0 
                                            && isKingSafeAfterTheMove(new Move(xKing, yKing, xKing, yKing-1), xKing, yKing)
                                            && isKingSafeAfterTheMove(new Move(xKing, yKing, xKing, yKing-2), xKing, yKing))
                                                legalMoves.add(move);
                                }else{//queen side
                                    if (threats.size() == 0 
                                            && isKingSafeAfterTheMove(new Move(xKing, yKing, xKing, yKing+1), xKing, yKing)
                                            && isKingSafeAfterTheMove(new Move(xKing, yKing, xKing, yKing+2), xKing, yKing))
                                                legalMoves.add(move);
                                }
                            }else{
                                if (isKingSafeAfterTheMove(move, xKing, yKing)){
                                    legalMoves.add(move);
                                } 
                            }
                        }
                }
            }//end for col
        }//end for row
        
        if (legalMoves.size() == 0){
            if (threats.size() == 0){
                status = Board.STALEMATE;
            }else{
                status = Board.CHECKMATE;
            }
        } 
    }
    
    public boolean isKingSafeAfterTheMove(Move move, int xKing, int yKing){
        boolean result; 
        //remember the board before move
        Square root = squares[move.getxBefore()][move.getyBefore()];
        Square goal = squares[move.getxAfter()][move.getyAfter()];
        
        //make the move and check
        squares[move.getxAfter()][move.getyAfter()] = root;
        squares[move.getxBefore()][move.getyBefore()] = new Square();
        
        if (move.getxBefore() == xKing && move.getyBefore() == yKing){//king move
            xKing = move.getxAfter();
            yKing = move.getyAfter();
        }
        
        if (findThreats(xKing, yKing).isEmpty()){
            result = true;
        }else{
            result = false;
        }
        //reverse the move
        squares[move.getxBefore()][move.getyBefore()] = root;
        squares[move.getxAfter()][move.getyAfter()] = goal;
        
        return result;
    }

    public void makeMove(Move move) {
        lastMove = move;
        Square squareBefore = squares[move.getxBefore()][move.getyBefore()];

        squares[move.getxAfter()][move.getyAfter()] = squareBefore;
        squares[move.getxBefore()][move.getyBefore()] = new Square();
        squareBefore.getPiece().setHasMoved(true);

        if(move.getType() == Move.CASTLE) {
            if(move.getyAfter() == 1){//king side
                squares[move.getxBefore()][2] = squares[move.getxBefore()][0];
                squares[move.getxBefore()][2].getPiece().setHasMoved(true);
                squares[move.getxBefore()][0] = new Square();
            }else{//queen side
                squares[move.getxBefore()][4] = squares[move.getxBefore()][7];
                squares[move.getxBefore()][4].getPiece().setHasMoved(true);
                squares[move.getxBefore()][7] = new Square();
            }
        }
        
        if(move.getType() == Move.ENPASSANT) {
            System.out.println("ENPASSANT");
            squares[move.getxBefore()][move.getyAfter()] = new Square();
        }

        if (move.getType() == Move.PROMOTE_TO_QUEEN){
            squares[move.getxAfter()][move.getyAfter()] = new Square(new Queen(currentTurn));
        }
        
        currentTurn *= -1;
        findLegalMoves(currentTurn);

    }
	
    public Square getSquare(int x, int y) {
        return squares[x][y];
    }

    public Move getLastMove() {
        return lastMove;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public ArrayList<Move> getLegalMoves() {
        return legalMoves;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
        
}