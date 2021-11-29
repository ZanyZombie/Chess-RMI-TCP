package client.view;

import chess.Square;
import chess.pieces.Piece;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import chess.Board;
import chess.Move;
import chess.pieces.Queen;
import client.control.ClientCtr;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import model.ObjectWrapper;
import model.Player;

public class GameFrm extends JFrame implements ActionListener{
    private String imageFolder = "C:/Users/Administrator/Documents/NetBeansProjects/Chess_RMI_Model/images/";
    
    private JPanel centerPanel;
    private SquareView[][] squareViews = new SquareView[8][8];
    private JLabel[][] pieceViews = new JLabel[8][8];
    private JButton btnDraw, btnSurrender, btnPlayAgain, btnQuit;
    private JLabel lblPlayer, lblOpponent;
    private JTextField txtOpponentTimer, txtPlayerTimer;
    private JLabel lblNotification;

    private Board board = new Board();
    private Move newMove = new Move();
    private int currentTurn;
    private int color;
    private ClientCtr myControl;
    private Player opponent, first;
    private Timer timer;
    private int myRemainTime, opponentRemainTime;
    private boolean opponentOfferDraw, offerDraw;
    public GameFrm (ClientCtr clientCtr, Player opponent, Player first) {
        this.myControl = clientCtr;
        this.opponent = opponent;
        this.first = first;
        if (first.equals(myControl.getPlayer())){
            color = Piece.WHITE;
        }else{
            color = Piece.BLACK;
        }
        myControl.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.GAME_FRM, this));

        setTitle("Chess board");
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);

        this.setContentPane(mainPanel);
        this.pack();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  
        this.setSize(650, 570);
        this.setResizable(false);

        centerPanel = new JPanel();
        GridLayout gridLayout = new GridLayout(8, 8);
        centerPanel.setLayout(gridLayout);
        centerPanel.setBounds(10, 30, 480, 480);
        this.add(centerPanel);

        btnDraw = new JButton("Cầu hòa");
        btnDraw.setBounds(500, 30, 120, 35);
        btnDraw.addActionListener(this);
        this.add(btnDraw);
        
        btnSurrender = new JButton("Đầu hàng");
        btnSurrender.setBounds(500, 80, 120, 35);
        btnSurrender.addActionListener(this);
        this.add(btnSurrender);
        
        btnPlayAgain = new JButton("Chơi lại");
        btnPlayAgain.setBounds(500, 130, 120, 35);
        btnPlayAgain.addActionListener(this);
        btnPlayAgain.setEnabled(false);
        this.add(btnPlayAgain);
        
        btnQuit = new JButton("Thoát phòng");
        btnQuit.setBounds(500, 180, 120, 35);
        btnQuit.addActionListener(this);
        this.add(btnQuit);
        
        lblOpponent = new JLabel(opponent.getName());
        lblOpponent.setBounds(500, 230, 120, 50);
        lblOpponent.setFont(new Font("Serif", Font.PLAIN, 26));
        lblOpponent.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(lblOpponent);
        
        txtOpponentTimer = new JTextField();
        txtOpponentTimer.setBounds(500, 280, 120, 50);
        txtOpponentTimer.setFont(new Font("Serif", Font.PLAIN, 26));
        txtOpponentTimer.setHorizontalAlignment(SwingConstants.CENTER);
        txtOpponentTimer.setText("00:00");
        txtOpponentTimer.setEditable(false);
        this.add(txtOpponentTimer);
        
        txtPlayerTimer = new JTextField();
        txtPlayerTimer.setBounds(500, 350, 120, 50);
        txtPlayerTimer.setFont(new Font("Serif", Font.PLAIN, 26));
        txtPlayerTimer.setHorizontalAlignment(SwingConstants.CENTER);
        txtPlayerTimer.setText("00:00");
        txtPlayerTimer.setEditable(false);
        this.add(txtPlayerTimer);
        
        lblPlayer = new JLabel(myControl.getPlayer().getName());
        lblPlayer.setBounds(500, 400, 120, 50);
        lblPlayer.setFont(new Font("Serif", Font.PLAIN, 26));
        lblPlayer.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(lblPlayer);

        lblNotification = new JLabel();
        lblNotification.setBounds(500, 450, 120, 35);
        lblNotification.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(lblNotification);
        
        this.addWindowListener( new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                myControl.sendData(new ObjectWrapper(ObjectWrapper.QUIT, null));
                quit();
            }
        });
        
        addSquareViews();
        resetBoard();
//        testFindThreats();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() instanceof JButton){            
            if (((JButton)e.getSource()).equals(btnDraw)){
                offerDraw ^= true;
                if (offerDraw)
                    lblNotification.setText("Bạn đã cầu hòa");
                else
                    lblNotification.setText(null);
                myControl.sendData(new ObjectWrapper(ObjectWrapper.OFFER_DRAW, null));
            }else if (((JButton)e.getSource()).equals(btnSurrender)){
                myControl.sendData(new ObjectWrapper(ObjectWrapper.SURRENDER, null));
            }else if (((JButton)e.getSource()).equals(btnPlayAgain)){
                myControl.sendData(new ObjectWrapper(ObjectWrapper.PLAY_AGAIN, null));
                btnPlayAgain.setEnabled(false);
            }else if (((JButton)e.getSource()).equals(btnQuit)){
                myControl.sendData(new ObjectWrapper(ObjectWrapper.QUIT, null));
                quit();
            }
        }
    }
    
    public void quit(){
        myControl.getHomeView().setVisible(true);
        this.dispose();
    }
    
    public void gameOver(ObjectWrapper data){
        btnDraw.setEnabled(false);
        btnSurrender.setEnabled(false);
        btnPlayAgain.setEnabled(true);
        int matchResult = (int) data.getData();
        int status = (int) data.getExtraData();
        String result ="";
        if (matchResult == myControl.getPlayer().getId()){
            result = "<html>Bạn thắng do "; 
        }else if (matchResult == opponent.getId()){
            result = "<html>Bạn thua do ";
        }else {
            if (status == Board.STALEMATE){
                result = "Hòa do hết nước đi";
            }else{
                result = "Hòa";
            }
        }

        if (status == Board.CHECKMATE){
            result += "<br> chiếu hết </html>"; 
        }else if (status == Board.SURRENDER){
            result += "<br> đầu hàng </html>";
        }
        lblNotification.setText(result);
        timer.cancel();
        timer.purge();
    }
    
    public void showClock(){
        String min = (myRemainTime%60<10 ? "0":"") + myRemainTime/60;
        String sec = (myRemainTime%60<10 ? "0":"") + myRemainTime%60;
        txtPlayerTimer.setText(min + " : " + sec);
        
        min = (opponentRemainTime%60<10 ? "0":"") + opponentRemainTime/60;
        sec = (opponentRemainTime%60<10 ? "0":"") + opponentRemainTime%60;
        txtOpponentTimer.setText(min + " : " + sec);
    }
    
    public void receivedDataProcessing(ObjectWrapper data) {
        switch (data.getPerformative()) {
            case ObjectWrapper.REPLY_MAKE_MOVE:{
                if (((String) data.getData()).equalsIgnoreCase("ok")){
                    makeMove(newMove);
                    newMove = new Move();
                    myRemainTime = (int) data.getExtraData();
                    timer.cancel();
                    timer.purge();
                    timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask(){
                    @Override
                    public void run() {
                        opponentRemainTime--;
                        if (opponentRemainTime == 0){
                            timer.cancel();
                        }   
                        showClock();
                    }
                }, 1000, 1000);
                }
                break;

            }
            case ObjectWrapper.SERVER_INFORM_MAKE_MOVE:{
                Move move = (Move) data.getData();
                makeMove(move);
                
                this.opponentRemainTime = (int) data.getExtraData();
                timer.cancel();
                timer.purge();
                timer = new Timer();
                
                timer.scheduleAtFixedRate(new TimerTask(){
                        @Override
                        public void run() {
                            myRemainTime--;
                            if (myRemainTime == 0){
                                timer.cancel();
                            }   
                            showClock();
                        }
                    }, 1000, 1000);
                break;
            }
            case ObjectWrapper.OPPONENT_OFFER_DRAW:{
                opponentOfferDraw ^= true;
                if (opponentOfferDraw)
                    lblNotification.setText("Đối thủ đã cầu hòa");
                else
                    lblNotification.setText(null);
                break;
            }
            case ObjectWrapper.OPPONENT_QUIT:{
                JOptionPane.showMessageDialog(this, "Đối thủ đã thoát");
                quit();
                break;
            }
            case ObjectWrapper.SERVER_INFORM_GAME_OVER:{
                gameOver(data);
                break;
            }
            case ObjectWrapper.OPPONENT_PLAY_AGAIN:{
                lblNotification.setText("Đối thủ đã sẵn sàng");
                break;
            }
            case ObjectWrapper.REMATCH:{
                color *= -1;
                resetBoard();
                break;
            }
            default:
                break;
        }
    }
    
    public void addSquareViews(){       
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                SquareView sqView  = new SquareView(row, col);
                JLabel pieceView = new JLabel();
                sqView.add(pieceView);
                pieceViews[row][col] = pieceView;
                squareViews[row][col] = sqView;
                centerPanel.add(sqView);
            }
        }
    }
    
    public void resetBoard(){
        board = new Board();
        currentTurn = Piece.WHITE;
        opponentOfferDraw = false;
        offerDraw = false;
        lblNotification.setText(null);
        btnDraw.setEnabled(true);
        btnSurrender.setEnabled(true);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                pieceViews[row][col].setIcon(null);
            }
        }
        centerPanel.removeAll();
        if (color == Piece.WHITE){
            //white
//            System.out.println("white");
//            color = Piece.WHITE;
            for (int row=7; row>=0; row--){
                for (int col=7; col>=0; col--){
                    Square square = board.getSquare(row, col);
                    SquareView squareView = squareViews[row][col];
                    centerPanel.add(squareView);
                    if (square.isOccupied()){
                        squareView.paintPiece(square.getPiece().getImage());
                    }
                }
            }
        }else{
            //black
//            System.out.println("black");
//            color = Piece.BLACK;
            for (int row=0; row<8; row++){
                for (int col=0; col<8; col++){
                    Square square = board.getSquare(row, col);
                    SquareView squareView = squareViews[row][col];
                    centerPanel.add(squareView);
                    if (square.isOccupied()){
                        squareView.paintPiece(square.getPiece().getImage());
                    }
                }
            }
        }      
        centerPanel.repaint();

        this.myRemainTime = 300;
        this.opponentRemainTime = 300;
        timer = new Timer();
        showClock();
        if (color == Piece.WHITE){
            timer.scheduleAtFixedRate(new TimerTask(){
                    @Override
                    public void run() {
                        myRemainTime--;
                        if (myRemainTime == 0){
                            timer.cancel();
                        }   
                        showClock();
                    }
            }, 1000, 1000);
        }else{
            timer.scheduleAtFixedRate(new TimerTask(){
                    @Override
                    public void run() {
                        opponentRemainTime--;
                        if (opponentRemainTime == 0){
                            timer.cancel();
                        }   
                        showClock();
                    }
            }, 1000, 1000);
        }
        
    }
    
    public void makeMove(Move move){
        board.makeMove(move);
        currentTurn *= -1;

        squareViews[move.getxBefore()][move.getyBefore()].paintPiece(null);
        Piece piece = board.getSquare(move.getxAfter(), move.getyAfter()).getPiece();
        squareViews[move.getxAfter()][move.getyAfter()].paintPiece(piece.getImage());
                
        //CASTLING
        if (move.getType() == Move.CASTLE){
            if(move.getyAfter() == 1){//king side
                squareViews[move.getxAfter()][0].paintPiece(null);
                squareViews[move.getxAfter()][2].paintPiece(board.getSquare(move.getxAfter(), 2).getPiece().getImage());

            }else{//queen side
                squareViews[move.getxAfter()][7].paintPiece(null);
                squareViews[move.getxAfter()][4].paintPiece(board.getSquare(move.getxAfter(), 4).getPiece().getImage());

            }
        }
        
        //EN PASSANT
        if (move.getType() == Move.ENPASSANT){
            squareViews[move.getxBefore()][move.getyAfter()].paintPiece(null);
        }
        
        //PROMOTION
        if (move.getType() == Move.PROMOTE_TO_QUEEN){
            squareViews[move.getxAfter()][move.getyAfter()].paintPiece((new Queen(currentTurn*-1)).getImage());
        }
    }
    
    class SquareView extends JPanel {
        
        private int row, col;
        private String imageFile;
        public SquareView(int row, int col) {
            this.row = row;
            this.col = col;
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    System.out.println(row+ " " + col);
                    
                    for (int row = 0; row < 8; row++) {
                        for (int col = 0; col < 8; col++) {
                            squareViews[row][col].setImage(null);
                            squareViews[row][col].repaint();
                        }
                    }
                    
                    Square square =  board.getSquare(row, col);
                    ArrayList<Move> moves = board.getLegalMoves();
                    
                    if (square.isOccupied() && square.getPiece().getColor() == color){
                        for (Move move : moves){
                            if (move.getxBefore() == row && move.getyBefore() == col){
                                SquareView squareView = squareViews[move.getxAfter()][move.getyAfter()];
                                if (board.getSquare(move.getxAfter(), move.getyAfter()).isOccupied()){
                                    squareView.setImage("round.png");
                                    squareView.repaint();
                                }else{
                                    squareView.setImage("dot30.png");
                                    squareView.repaint();
                                }
                                newMove = new Move();
                                newMove.setxBefore(row);
                                newMove.setyBefore(col);
                            }
                        }                        
                    }else{
                        newMove.setxAfter(row);
                        newMove.setyAfter(col);
                        if (moves.contains(newMove)){
                            newMove = moves.get(moves.indexOf(newMove));
                            myControl.sendData(new ObjectWrapper(ObjectWrapper.MAKE_MOVE, newMove));
                        }else{
                            System.out.println("not a legal move");
                        }
                    }
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            if ((col + row) % 2 == 0) {
                g2d.setColor(Color.white);
            }else{
                g2d.setColor(Color.blue);
            }
            g2d.fillRect(0, 0, 60, 60);
            if(imageFile == "dot30.png"){
                try {
                    g2d.drawImage(ImageIO.read(new File(imageFolder + "dot30.png")), 15, 15, null);
                } catch (IOException ex) {
                    Logger.getLogger(GameFrm.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (imageFile == "round.png"){
                try {
                    g2d.drawImage(ImageIO.read(new File(imageFolder + "round.png")), 0, 0, null);
                } catch (IOException ex) {
                    Logger.getLogger(GameFrm.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        public void paintPiece(String str){
            pieceViews[row][col].setIcon(new ImageIcon(str));
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public int getCol() {
            return col;
        }

        public void setCol(int col) {
            this.col = col;
        }        
        
        public void setImage(String imageFile) {
            this.imageFile = imageFile;
        }
        
    }
    
}

