package client.control;

//import DTO.Room;
//import client.view.CreateGroupFrm;
//import client.view.GroupDetailFrm;
import client.view.Friend_DetailFrm;
import client.view.Friend_SearchFrm;
import client.view.GameFrm;
import client.view.Group_CreateFrm;
import client.view.Group_DetailFrm;
import client.view.HomeFrm;
//import client.view.InviteGroupFrm;
import client.view.LoginFrm;
import client.view.StandingsFrm;
//import client.view.PlayerDetailFrm;
//import client.view.RegisterFrm;
//import client.view.SearchPlayerFrm;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import model.Group;
import model.IPAddress;
import model.Invitation;
import model.ObjectWrapper;
import model.Player;

public class ClientCtr {
    private LoginFrm loginView;
    private HomeFrm homeView;
    private ArrayList<ObjectWrapper> myFunction;

    private Socket mySocket;
    private ClientListening myListening;                            
    
    private Player player;
    private ArrayList<Player> onlinePlayers;
    private ArrayList<Player> ingamePlayers;
    private ArrayList<Player> myFriends;
    private ArrayList<Group> myGroups;
    private ArrayList<Invitation> myInvitations;
    private IPAddress serverAddress = new IPAddress("localhost",8888);  
    
    public ClientCtr(LoginFrm view){
        super();
        this.loginView = view;
        myFunction = new ArrayList<ObjectWrapper>(); 
        myFriends = new ArrayList<>();
        onlinePlayers = new ArrayList<>();
        openConnection();
    }
    public void openConnection(){        
        try {
            mySocket = new Socket(serverAddress.getHost(), serverAddress.getPort());  
            myListening = new ClientListening();
            myListening.start();
//            view.showMessage("Connected to the server at host: " + serverAddress.getHost() + ", port: " + serverAddress.getPort());
        } catch (Exception e) {
            e.printStackTrace();
//            view.showMessage("Error when connecting to the server!");
//            return false;
        }
//        return true;
    }
    public boolean closeConnection(){
        try {
            if(myListening != null){
                myListening.stop();
            }
            if(mySocket !=null) {
                mySocket.close();
            }
            myFunction.clear();             
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean sendData(Object obj){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(mySocket.getOutputStream());
            oos.writeObject(obj);           
             
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    public ArrayList<ObjectWrapper> getActiveFunction() {
        return myFunction;
    }

    public ArrayList<Player> getMyFriends() {
        return myFriends;
    }

    public ArrayList<Group> getMyGroups() {
        return myGroups;
    }

    public ArrayList<Player> getOnlinePlayers() {
        return onlinePlayers;
    }

    public ArrayList<Player> getIngamePlayers() {
        return ingamePlayers;
    }

    public ArrayList<Invitation> getMyInvitations() {
        return myInvitations;
    }
    
    public void setHomeView(HomeFrm view) {
        this.homeView = view;
    }

    public HomeFrm getHomeView() {
        return homeView;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
    
    class ClientListening extends Thread{
         
        public ClientListening() {
            super();
        }
         
        public void run() {
            try {
                while(true) {
                ObjectInputStream ois = new ObjectInputStream(mySocket.getInputStream());
                Object obj = ois.readObject();
                if(obj instanceof ObjectWrapper) {
                    ObjectWrapper data = (ObjectWrapper)obj;
                    System.out.println("server reply: " +data.getPerformative());
                    if(data.getPerformative() == ObjectWrapper.REPLY_LOGIN_USER){
                        loginView.receivedDataProcessing(data);
                    }else 
                    //init home view
                    if (data.getPerformative() == ObjectWrapper.REPLY_GET_LIST_FRIEND){
                        myFriends = (ArrayList<Player>) data.getData();
                        homeView.updateListFriends();
                    }else if(data.getPerformative() == ObjectWrapper.REPLY_GET_LIST_GROUP){
                        myGroups = (ArrayList<Group>) data.getData();
                        homeView.updateListGroups();
                    }else if(data.getPerformative() == ObjectWrapper.REPLY_GET_ALL_INVITATION){
                        myInvitations = (ArrayList<Invitation>) data.getData();
                        homeView.updateListInvitations();
                    }else if(data.getPerformative() == ObjectWrapper.SERVER_INFORM_ONLINE_PLAYERS){
                        onlinePlayers = (ArrayList<Player>) data.getData();
                    }else if(data.getPerformative() == ObjectWrapper.SERVER_INFORM_INGAME_PLAYERS){
                        ingamePlayers = (ArrayList<Player>) data.getData();
                        homeView.updateListFriends();
                        homeView.updateListChallenges();
                    }else if(data.getPerformative() == ObjectWrapper.SERVER_INFORM_PLAYER_CONNECTED){
                        onlinePlayers.add((Player) data.getData());
                        homeView.updateListFriends();
                    }else if(data.getPerformative() == ObjectWrapper.SERVER_INFORM_PLAYER_DISCONNECTED){
                        onlinePlayers.remove((Player) data.getData());
                        ingamePlayers.remove((Player) data.getData());
                        homeView.updateListFriends();
                        homeView.updateListChallenges();

//                        if(homeView.getListChallenges().contains(player)){
//                            homeView.getListChallenges().remove(player);
//                            homeView.updateListChallenges();
//                        }
                    //friend 
                    }else if (data.getPerformative() == ObjectWrapper.SERVER_INFORM_NEW_FRIEND){
                        myFriends.add((Player) data.getData());
                        homeView.updateListFriends();
                        for(ObjectWrapper fto: myFunction){
                            if (fto.getData() instanceof Friend_DetailFrm){
                                ((Friend_DetailFrm) fto.getData()).receivedDataProcessing(data);
                            }
                        }
                    }else if (data.getPerformative() == ObjectWrapper.SERVER_INFORM_NEW_FRIEND_REQUEST){
                        myInvitations.add((Invitation) data.getData());
                        homeView.updateListInvitations();
                                        
                    //friend detail form
                    }else if(data.getPerformative() == ObjectWrapper.REPLY_GET_LIST_TOUR
                            || data.getPerformative() == ObjectWrapper.REPLY_GET_FRIEND_RELATIONSHIP
                            || data.getPerformative() == ObjectWrapper.REPLY_ADD_FRIEND
                            || data.getPerformative() == ObjectWrapper.REPLY_ACCEPT_FRIEND_REQUEST 
                            || data.getPerformative() == ObjectWrapper.REPLY_DECLINE_FRIEND_REQUEST
                            || data.getPerformative() == ObjectWrapper.REPLY_CHALLENGE){
                            
                            for(ObjectWrapper fto: myFunction){
                                if (fto.getData() instanceof Friend_DetailFrm){
                                    ((Friend_DetailFrm) fto.getData()).receivedDataProcessing(data);
                                }
                            }
                    //group 
                    }else if (data.getPerformative() == ObjectWrapper.SERVER_INFORM_NEW_GROUP_INVITATION){
                        sendData(new ObjectWrapper(ObjectWrapper.GET_ALL_INVITATION, getPlayer()));
                        homeView.updateListInvitations();
                    }else if (data.getPerformative() == ObjectWrapper.REPLY_GET_LIST_GROUP_MEMBER
                            ||data.getPerformative() == ObjectWrapper.REPLY_ACCEPT_GROUP_REQUEST
                            ||data.getPerformative() == ObjectWrapper.REPLY_DECLINE_GROUP_REQUEST){
                            for(ObjectWrapper fto: myFunction){
                                if (fto.getData() instanceof Group_DetailFrm){
                                    ((Group_DetailFrm) fto.getData()).receivedDataProcessing(data);
                                }
                            }
                    //game
                    }else if (data.getPerformative() == ObjectWrapper.RECEIVE_CHALLENGE){
                        Player player = (Player) data.getData();
                        if(!homeView.getListChallenges().contains(player)){
                            homeView.getListChallenges().add(player);
                            homeView.updateListChallenges();
                        }
                        
                    }else if (data.getPerformative() == ObjectWrapper.SERVER_INFORM_MATCH_FOUND){
                        Player player = (Player) data.getData();
                        Player first = (Player) data.getExtraData();
                        homeView.openGameFrm(player, first);
                        
                    }
                    else if (data.getPerformative() == ObjectWrapper.OPPONENT_QUIT
                            ||data.getPerformative() == ObjectWrapper.REPLY_MAKE_MOVE
                            || data.getPerformative() == ObjectWrapper.SERVER_INFORM_MAKE_MOVE
                            ||data.getPerformative() == ObjectWrapper.SERVER_INFORM_GAME_OVER
                            ||data.getPerformative() == ObjectWrapper.OPPONENT_OFFER_DRAW
                            ||data.getPerformative() == ObjectWrapper.OPPONENT_PLAY_AGAIN
                            ||data.getPerformative() == ObjectWrapper.REMATCH){
                            for(ObjectWrapper fto: myFunction){
                                if (fto.getData() instanceof GameFrm){
                                    ((GameFrm) fto.getData()).receivedDataProcessing(data);
                                }
                            }
                    }
//                   
//                    }else if (data.getPerformative() == ObjectWrapper.SERVER_INFORM_NEW_GROUP_MEMBER){
//                        for(ObjectWrapper fto: myFunction){
//                                if (fto.getData() instanceof GroupDetailFrm){
//                                    ((GroupDetailFrm) fto.getData()).receivedDataProcessing(data);
//                                }
//                            }
//                    }
//                    }else if (data.getPerformative() == ObjectWrapper.SERVER_INFORM_NEW_FRIEND_REQUEST){
//                        myInvitations.add((Invitation) data.getData());
//                        homeView.updateListInvitations();
//                    }else if (data.getPerformative() == ObjectWrapper.SERVER_INFORM_NEW_GROUP_INVITATION){
//                        myInvitations.add((Invitation) data.getData());
//                        homeView.updateListInvitations();
//                    }else if(data.getPerformative() == ObjectWrapper.REPLY_LOGIN_USER){
//                        loginView.receivedDataProcessing(data);
//                    }else if (data.getPerformative() == ObjectWrapper.REPLY_GET_LIST_FRIEND){
//                        myFriends = (ArrayList<Player>) data.getData();
//                        homeView.updateListFriends();
//                    }else if(data.getPerformative() == ObjectWrapper.REPLY_GET_LIST_GROUP){
//                        myGroups = (ArrayList<Group>) data.getData();
//                        homeView.updateListGroups();
//                    }else if(data.getPerformative() == ObjectWrapper.REPLY_GET_ALL_INVITATION){
//                        myInvitations = (ArrayList<Invitation>) data.getData();
//                        homeView.updateListInvitations();
//                    }else if(data.getPerformative() == ObjectWrapper.REPLY_ACCEPT_FRIEND_REQUEST 
//                            || data.getPerformative() == ObjectWrapper.REPLY_DECLINE_FRIEND_REQUEST ){
//                            
//                            for(ObjectWrapper fto: myFunction){
//                                if (fto.getData() instanceof PlayerDetailFrm){
//                                    ((PlayerDetailFrm) fto.getData()).receivedDataProcessing(data);
//                                }
//                            }
//                    }else if(data.getPerformative() == ObjectWrapper.REPLY_ACCEPT_GROUP_REQUEST 
//                            || data.getPerformative() == ObjectWrapper.REPLY_DECLINE_GROUP_REQUEST ){
//                            
//                            for(ObjectWrapper fto: myFunction){
//                                if (fto.getData() instanceof GroupDetailFrm){
//                                    ((GroupDetailFrm) fto.getData()).receivedDataProcessing(data);
//                                }
//                            }
                    else{
                        for(ObjectWrapper fto: myFunction){
                            
                            if(fto.getPerformative() == data.getPerformative()) {
                                switch(data.getPerformative()) {
                                
//                                case ObjectWrapper.REPLY_ADD_USER:
//                                    RegisterFrm registerFrm = (RegisterFrm) fto.getData();
//                                    registerFrm.receivedDataProcessing(data); 
//                                    break;
                                    
                                case ObjectWrapper.REPLY_SEARCH_PLAYER:
                                    Friend_SearchFrm searchPlayerFrm = (Friend_SearchFrm) fto.getData();
                                    searchPlayerFrm.receivedDataProcessing(data); 
                                    break;
                                    
                                case ObjectWrapper.REPLY_CREATE_GROUP:
                                    Group_CreateFrm createGroupFrm = (Group_CreateFrm) fto.getData();
                                    createGroupFrm.receivedDataProcessing(data); 
                                    break;
                                case ObjectWrapper.REPLY_GET_STANDINGS:
                                    StandingsFrm standingsFrm = (StandingsFrm) fto.getData();
                                    standingsFrm.receivedDataProcessing(data); 
                                    break;
//                                case ObjectWrapper.REPLY_ADD_FRIEND:
//                                    PlayerDetailFrm playerDetailFrm = (PlayerDetailFrm) fto.getData();
//                                    playerDetailFrm.receivedDataProcessing(data); 
//                                    break;
//                                case ObjectWrapper.REPLY_GET_LIST_GROUP_MEMBER:
//                                    GroupDetailFrm groupDetailFrm = (GroupDetailFrm) fto.getData();
//                                    groupDetailFrm.receivedDataProcessing(data); 
//                                    break;
//                                case ObjectWrapper.REPLY_SENT_GROUP_INVITATION:
//                                    InviteGroupFrm inviteGroupFrm = (InviteGroupFrm) fto.getData();
//                                    inviteGroupFrm.receivedDataProcessing(data); 
//                                    break;
//                                    
//                                case ObjectWrapper.REPLY_FIND_GAME:
//                                    Room room = (Room) fto.getData();
////                                    inviteGroupFrm.receivedDataProcessing(data);
//                                    System.out.println(room.getPlayer1().getId());
//                                    System.out.println(room.getPlayer2().getId());
//                                    System.out.println(room.getWhite());
//                                    break;
                                }//end switch
                                
                                    
                            }
                        }//end for
                            
                    }
                }
                }//end while
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
}
