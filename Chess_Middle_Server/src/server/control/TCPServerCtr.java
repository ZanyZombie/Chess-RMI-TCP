package server.control;

import chess.Board;
import chess.Move;
import chess.pieces.Piece;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import model.Group;
import model.GroupMember;
import model.IPAddress;
import model.Invitation;
import model.Match;
import model.ObjectWrapper;
import model.Player;
import model.StandingsDetail;
import rmi.general.FriendInterface;
import rmi.general.GroupInterface;
import rmi.general.PlayerInterface;
import rmi.general.StandingsInterface;
import rmi.general.TournamentInterface;
import server.view.TCPServerMainFrm;


public class TCPServerCtr {
    private TCPServerMainFrm view;
    private ServerSocket myServer;
    private ServerListening myListening;
    private ArrayList<ServerProcessing> myProcess;
    private IPAddress myAddress = new IPAddress("localhost",8888);  //default server host and port
    
    private RMIClientCtr rmiServer;
            
    private ArrayList<Player> onlinePlayers;
    private ArrayList<Player> ingamePlayers;
    private ArrayList<Match> matchs;

    public TCPServerCtr(TCPServerMainFrm view){
        myProcess = new ArrayList<ServerProcessing>();
        onlinePlayers = new ArrayList<Player>();
        ingamePlayers = new ArrayList<Player>();
        this.view = view;
        openTCPServer();  
        rmiServer = new RMIClientCtr();
    }
    
    private void openTCPServer(){
        try {
            myServer = new ServerSocket(myAddress.getPort()); //tcp
            myListening = new ServerListening(); //tcp
            myListening.start();//tcp
            myAddress.setHost(InetAddress.getLocalHost().getHostAddress()); //udp +tcp
            view.showServerInfor(myAddress);
            view.showMessage("TCP server is running at the port " + myAddress.getPort() +"...");
        }catch(Exception e) {
            e.printStackTrace();
            view.showMessage("Error to open the datagram socket!");
        }
    }
    
    public void stopServer() {
        try {
            for(ServerProcessing sp:myProcess)
                sp.stop();
            myListening.stop();
            myServer.close();
            view.showMessage("TCP server is stopped!");
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    public void sendPublicData(ObjectWrapper data, Player exception){
        for (ServerProcessing sp : myProcess) {
            if (sp.threadOwner != null)
            if (!sp.threadOwner.equals(exception)){
                sp.sendDataToClient(data);
            }
        }
    }
    
    public void sendPrivateData(Player player, ObjectWrapper data){
        for (ServerProcessing sp : myProcess) {
            if (sp.threadOwner.equals(player)){
                sp.sendDataToClient(data);
            }
        }
    }
    
    class ServerListening extends Thread{
         
        public ServerListening() {
            super();
        }
         
        public void run() {
            view.showMessage("server is listening... ");
            try {
                while(true) {
                    Socket clientSocket = myServer.accept();
                    ServerProcessing sp = new ServerProcessing(clientSocket);
                    sp.start();
                    myProcess.add(sp);
                    view.showMessage("Number of client connecting to the server: " + myProcess.size());
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
     
    /**
     * The class to treat the requirement from client
     *
     */
    class ServerProcessing extends Thread{
        private Socket mySocket;
        private Player threadOwner;
        private ServerProcessing opponent;
        private Match match;
        private Board board;
        private int color = 0;
        private boolean offerDraw, playAgain;
        private int remainTime;
        private Timer timer;
        
        public ServerProcessing(Socket s) {
            super();
            mySocket = s;
        }
         
        public void sendDataToClient(Object obj) {
            try {
                ObjectOutputStream oos= new ObjectOutputStream(mySocket.getOutputStream());
                oos.writeObject(obj);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        
        private TimerTask timerTask(){
            return new TimerTask(){
                @Override
                public void run() {
                    remainTime--;
                    if (remainTime == 0){
                        timer.cancel();
                        System.out.println("het gio");
                        board.setStatus(Board.TIMEOUT);
                        if (board.getCurrentTurn() == color){
                            gameOver(opponent.threadOwner.getId(), Board.TIMEOUT);
                        }
                    }   
                }
            };
        }
        
        public void setupGame(){
            Match m = new Match();
            m.setStarttime(new Timestamp(new Date().getTime()));
            this.match = m;
            opponent.match = m;
            
            if (color == 0){
                this.color = Piece.WHITE;
                opponent.color = Piece.BLACK;
            }else{
                this.color *= -1;
                opponent.color *= -1;
            }
            

            Board board = new Board();
            this.board = board;
            opponent.board = board;
            
            this.offerDraw = false;
            opponent.offerDraw = false;
            this.playAgain = false;
            opponent.playAgain = false;
            
            
            this.remainTime = 300;
            opponent.remainTime = 300;
            timer = new Timer();
            if(color == Piece.WHITE)
                timer.scheduleAtFixedRate(timerTask(), 1000, 1000);
            else 
                opponent.timer.scheduleAtFixedRate(timerTask(), 1000, 1000);
        }
        
        public void handleMove(Move move){
            if (this.board.getStatus() == Board.PLAYING){
                if (this.color == board.getCurrentTurn()){
                    if(board.getLegalMoves().contains(move)){
                        board.makeMove(move);
                        timer.cancel();
                        timer.purge(); 
                        opponent.timer = new Timer();
                        opponent.timer.scheduleAtFixedRate(opponent.timerTask(), 1000, 1000);
                        sendDataToClient(new ObjectWrapper(ObjectWrapper.REPLY_MAKE_MOVE, "ok", remainTime));
                        opponent.sendDataToClient(new ObjectWrapper(ObjectWrapper.SERVER_INFORM_MAKE_MOVE, move, remainTime));

                        if (board.getStatus() == Board.CHECKMATE){
                            gameOver(threadOwner.getId(), Board.CHECKMATE);
                        }else if (board.getStatus() == Board.STALEMATE){
                            gameOver(0, Board.STALEMATE);
                        }
                    }else{
                        System.out.println("not a legal move");
                    }
                }
            }
        }
        
        public void opponentOut(){
            if(board.getStatus() == Board.PLAYING){
                gameOver(threadOwner.getId(), Board.SURRENDER);
            }
            sendDataToClient(new ObjectWrapper(ObjectWrapper.OPPONENT_QUIT, null));
            ingamePlayers.remove(opponent.threadOwner);
            ingamePlayers.remove(this.threadOwner);
            sendPublicData(new ObjectWrapper(ObjectWrapper.SERVER_INFORM_INGAME_PLAYERS, ingamePlayers), null);

            this.opponent = null;
            this.match = null;
            this.color = 0;
        }
        
        public void gameOver(int matchResult, int status){
            match.setEndtime(new Timestamp(new Date().getTime()));
            match.setResult(matchResult);
            boolean save = rmiServer.remoteSaveMatch(match, threadOwner, opponent.threadOwner);
            
            board.setStatus(status);
            sendDataToClient(new ObjectWrapper(ObjectWrapper.SERVER_INFORM_GAME_OVER, matchResult, status));
            if (this.timer != null){
                this.timer.cancel();
                this.timer.purge();
            }
            if (opponent != null){
                opponent.sendDataToClient(new ObjectWrapper(ObjectWrapper.SERVER_INFORM_GAME_OVER, matchResult, status));
                if (opponent.timer != null){
                    opponent.timer.cancel();
                    opponent.timer.purge();
                }
            }
        }
        
        public void run() { 
            try {
                while(true) {
                    ObjectInputStream ois = new ObjectInputStream(mySocket.getInputStream());
                    Object objectWrapper = ois.readObject();
                    if(objectWrapper instanceof ObjectWrapper){
                        ObjectWrapper data = (ObjectWrapper)objectWrapper;
 
                        switch(data.getPerformative()) {
                        //Sign up & login frm
                        case ObjectWrapper.ADD_USER:{
                            Player player = (Player) data.getData();
                            if (rmiServer.remoteAddPlayer(player)) {
                                    sendDataToClient(new ObjectWrapper(ObjectWrapper.REPLY_ADD_USER, "ok"));
                            } else {
                                    sendDataToClient(new ObjectWrapper(ObjectWrapper.REPLY_ADD_USER, "false"));
                            }
                            break;
                        }
                        case ObjectWrapper.LOGIN_USER:{
                            view.showMessage("Login");
                            Player player = rmiServer.remoteCheckLogin((Player) data.getData());
                            if (player instanceof Player){
                                if (onlinePlayers.contains(player)){
                                    sendDataToClient(new ObjectWrapper(ObjectWrapper.REPLY_LOGIN_USER, "already online"));
                                }else{
                                    threadOwner = player;
                                    sendDataToClient(new ObjectWrapper(ObjectWrapper.REPLY_LOGIN_USER, player));
                                    sendDataToClient(new ObjectWrapper(ObjectWrapper.SERVER_INFORM_ONLINE_PLAYERS, onlinePlayers));
                                    sendDataToClient(new ObjectWrapper(ObjectWrapper.SERVER_INFORM_INGAME_PLAYERS, ingamePlayers));
                                    sendPublicData(new ObjectWrapper(ObjectWrapper.SERVER_INFORM_PLAYER_CONNECTED, player), player);
                                    onlinePlayers.add(player);
                                }
                            }else{
                                sendDataToClient(new ObjectWrapper(ObjectWrapper.REPLY_LOGIN_USER, "false"));
                            }
                            break;
                        }
                        //Home Frm
                        case ObjectWrapper.GET_LIST_FRIEND :{
                            ArrayList<Player> friends = rmiServer.remoteGetFriendList((Player) data.getData());
                            sendDataToClient(new ObjectWrapper(ObjectWrapper.REPLY_GET_LIST_FRIEND, friends));
                            break;
                        }
                        case ObjectWrapper.GET_LIST_GROUP:{
                            ArrayList<Group> groups = rmiServer.remoteGetGroupList((Player) data.getData());
                            sendDataToClient(new ObjectWrapper(ObjectWrapper.REPLY_GET_LIST_GROUP, groups));
                            break;
                        }
                        case ObjectWrapper.GET_ALL_INVITATION:{
                            Player player = (Player) data.getData();
                            ArrayList<Invitation> invitations = rmiServer.remoteGetFriendRequests(player);
                            invitations.addAll(rmiServer.remoteGetGroupInvitations(player));
                            sendDataToClient(new ObjectWrapper(ObjectWrapper.REPLY_GET_ALL_INVITATION, invitations));
                            break;
                        }
                        //Friend
                        case ObjectWrapper.SEARCH_PLAYER:{
                            ArrayList<Player> players = rmiServer.remoteSearchPlayerByName((String) data.getData());
                            sendDataToClient(new ObjectWrapper(ObjectWrapper.REPLY_SEARCH_PLAYER, players));
                            break;
                        }
                        case ObjectWrapper.GET_FRIEND_RELATIONSHIP:{

                            Player friend = (Player) data.getData();
                            Player user = (Player) data.getExtraData();
                            sendDataToClient(new ObjectWrapper(ObjectWrapper.REPLY_GET_FRIEND_RELATIONSHIP,
                            rmiServer.remoteGetFriendship(user, friend)));
                            break;
                        }
                        case ObjectWrapper.GET_LIST_TOUR:{
                            Player player = (Player) data.getData();
                            sendDataToClient(new ObjectWrapper(ObjectWrapper.REPLY_GET_LIST_TOUR,
                                rmiServer.remoteGetTournaments(player)));
                            break;
                        }
                        case ObjectWrapper.ADD_FRIEND:{
                            Player friend = (Player) data.getExtraData();
                            boolean rs = rmiServer.remoteAddFriend(threadOwner, friend);
                            sendDataToClient(new ObjectWrapper(ObjectWrapper.REPLY_ADD_FRIEND, rs));

                            if (rs){
                                Invitation invitation = new Invitation(Invitation.FRIEND_REQUEST, threadOwner, null);
                                sendPrivateData(friend, new ObjectWrapper(ObjectWrapper.SERVER_INFORM_NEW_FRIEND_REQUEST, invitation));
                            }
                            break;

                        }
                        case ObjectWrapper.ACCEPT_FRIEND_REQUEST: {
                            Player friend = (Player)data.getExtraData();
                            boolean rs = rmiServer.remoteAcceptFriendRequest(threadOwner, friend);
                            sendDataToClient(new ObjectWrapper(ObjectWrapper.REPLY_ACCEPT_FRIEND_REQUEST, rs));
                            sendPrivateData(friend, new ObjectWrapper(ObjectWrapper.SERVER_INFORM_NEW_FRIEND, threadOwner));
                            break;
                        }
                        case ObjectWrapper.DECLINE_FRIEND_REQUEST: {
                            Player friend = (Player)data.getExtraData();
                            boolean rs = rmiServer.remoteDeclineFriendRequest(threadOwner, friend);
                            sendDataToClient(new ObjectWrapper(ObjectWrapper.REPLY_DECLINE_FRIEND_REQUEST, rs));
                            break;
                        }
                        
                        //Group
                        case ObjectWrapper.CREATE_GROUP:{
                            Group group = (Group)data.getData();
                            Player player = (Player)data.getExtraData();
//                            group = groupDAO.addGroup(group, player);
                            group = rmiServer.remoteCreateGroup(group, player);
                            sendDataToClient(new ObjectWrapper(ObjectWrapper.REPLY_CREATE_GROUP, group));
                            break;
                        }
                        case ObjectWrapper.GET_LIST_GROUP_MEMBER:{
                            Group group = (Group)data.getData();

                            ArrayList<Player> result = rmiServer.remoteGetGroupMembers(group);
                            sendDataToClient(new ObjectWrapper(ObjectWrapper.REPLY_GET_LIST_GROUP_MEMBER,result));
                            break;
                        }
                        case ObjectWrapper.SENT_GROUP_INVITATION:{
                            Group group = (Group)data.getData();
                            Player friend = (Player) data.getExtraData();
                            GroupMember groupMember = new GroupMember();
                            groupMember.setGroupid(group.getId());
                            groupMember.setPlayerid(friend.getId());

                            boolean rs = rmiServer.remoteInvitePlayerToGroup(threadOwner, groupMember);
                            sendDataToClient(new ObjectWrapper(ObjectWrapper.REPLY_SENT_GROUP_INVITATION, rs));
                            if(rs){
                                Invitation inv = new Invitation(); 
                                inv.setType(Invitation.GROUP_INVITATION);
                                inv.setInviter(threadOwner);
                                inv.setTarget(group);
                                sendPrivateData(friend, new ObjectWrapper(ObjectWrapper.SERVER_INFORM_NEW_GROUP_INVITATION, inv));
                            }
                            break;
                        }

                        case ObjectWrapper.ACCEPT_GROUP_REQUEST:{
                            Group group = (Group)data.getData();
                            boolean rs = rmiServer.remoteAcceptGroupInvitation(group, threadOwner);
                            sendDataToClient(new ObjectWrapper(ObjectWrapper.REPLY_ACCEPT_GROUP_REQUEST, rs));
                            break;
                        }
//
                        case ObjectWrapper.DECLINE_GROUP_REQUEST:{
                            Group group = (Group)data.getData();
                            boolean rs = rmiServer.remoteDeclineGroupInvitation(group, threadOwner);
                            sendDataToClient(new ObjectWrapper(ObjectWrapper.REPLY_DECLINE_GROUP_REQUEST, rs));
                            break;
                        }
                        //Standings
                        case ObjectWrapper.GET_STANDINGS:{
                            ArrayList<StandingsDetail> standings = rmiServer.remoteGetStandings();
                            sendDataToClient(new ObjectWrapper(ObjectWrapper.REPLY_GET_STANDINGS, standings));
                            break;
                        }
                        //Challenge
                        case ObjectWrapper.CHALLENGE:{
                            Player player = (Player) data.getData();
                            sendPrivateData(player, new ObjectWrapper(ObjectWrapper.RECEIVE_CHALLENGE, threadOwner));                        
                            sendDataToClient(new ObjectWrapper(ObjectWrapper.REPLY_CHALLENGE, null));
                            break;
                        }
                        case ObjectWrapper.DECLINE_CHALLENGE:{
                            Player player = (Player) data.getData();
                            sendPrivateData(player, new ObjectWrapper(ObjectWrapper.SERVER_INFORM_DECLINE_CHALLENGE, threadOwner));                        
                            break;
                        }
                        case ObjectWrapper.ACCEPT_CHALLENGE:{
                            Player player = (Player) data.getData();
                            if (!ingamePlayers.contains(player))
                            {
                                for (ServerProcessing sp : myProcess) {
                                    if (sp.threadOwner.equals(player)){
                                        this.opponent = sp;
                                        sp.opponent = this;
                                    }
                                }
                                
                                setupGame();
                                
                                ingamePlayers.add(threadOwner);
                                ingamePlayers.add(player);
                                sendPublicData(new ObjectWrapper(ObjectWrapper.SERVER_INFORM_INGAME_PLAYERS, ingamePlayers), null);
                                sendDataToClient(new ObjectWrapper(ObjectWrapper.SERVER_INFORM_MATCH_FOUND, player, threadOwner));
                                sendPrivateData(player, new ObjectWrapper(ObjectWrapper.SERVER_INFORM_MATCH_FOUND, threadOwner, threadOwner));                        
                            }else{
                                sendDataToClient(new ObjectWrapper(ObjectWrapper.REPLY_ACCEPT_CHALLENGE, player));
                            }
                            break;
                        }
                        case ObjectWrapper.SURRENDER:{
                            gameOver(opponent.threadOwner.getId(), Board.SURRENDER);
                            break;
                        }
                        case ObjectWrapper.OFFER_DRAW:{
                            if (!opponent.offerDraw){
                                opponent.sendDataToClient(new ObjectWrapper(ObjectWrapper.OPPONENT_OFFER_DRAW, null));
                                this.offerDraw ^= true;
                            }else{
                                board.setStatus(Board.DRAW);
                                gameOver(0, Board.DRAW);
                                break;
                            }

                            break;
                        }
                        case ObjectWrapper.PLAY_AGAIN:{
                            if (!opponent.playAgain){
                                opponent.sendDataToClient(new ObjectWrapper(ObjectWrapper.OPPONENT_PLAY_AGAIN, null));
                                playAgain = true;
                            }else{
                                setupGame();
                                sendDataToClient(new ObjectWrapper(ObjectWrapper.REMATCH, null));
                                opponent.sendDataToClient(new ObjectWrapper(ObjectWrapper.REMATCH, null));
                                break;
                            }

                            break;
                        }
                        case ObjectWrapper.QUIT:{
                            opponent.opponentOut();
                            this.opponent = null;
                            this.match = null;
                            this.color = 0;
                            break;
                        }
                        case ObjectWrapper.MAKE_MOVE:{
                            Move move = (Move) data.getData();
                            handleMove(move);
                            break;
                        }
                        
                        default:
                            break;
                        }
 
                    }
                }
            }catch (EOFException | SocketException e) {             
                //e.printStackTrace();
                myProcess.remove(this);
                onlinePlayers.remove(threadOwner);
                ingamePlayers.remove(threadOwner);
                sendPublicData(new ObjectWrapper(ObjectWrapper.SERVER_INFORM_PLAYER_DISCONNECTED, threadOwner), null);
                if (opponent!= null){
                    opponent.opponentOut();
                }
                view.showMessage("Number of client connecting to the server: " + myProcess.size());
                try {
                    mySocket.close();
                }catch(Exception ex) {
                    ex.printStackTrace();
                }
                this.stop();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }
}
