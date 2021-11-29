package server.control;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import model.Friendship;
import model.Group;
import model.GroupMember;
import model.IPAddress;
import model.Invitation;
import model.Match;
import model.Player;
import model.StandingsDetail;
import model.Tournament;
import rmi.general.FriendInterface;
import rmi.general.GroupInterface;
import rmi.general.MatchInterface;
import rmi.general.PlayerInterface;
import rmi.general.StandingsInterface;
import rmi.general.TournamentInterface;


public class RMIClientCtr {
    private PlayerInterface playerStub;
    private FriendInterface friendStub;
    private TournamentInterface tournamentStub;
    private GroupInterface groupStub;
    private StandingsInterface standingsStub;
    private MatchInterface matchStub;
    
    private final IPAddress serverAddress = new IPAddress("localhost", 9999); //default server address
    private final String rmiService = "rmiServer";                            //default server service key
    
    public RMIClientCtr(){
        init();
    }
     
    public boolean init(){
        try{
            // get the registry
            Registry registry = LocateRegistry.getRegistry(serverAddress.getHost(), serverAddress.getPort());
            // lookup the remote objects
            playerStub = (PlayerInterface)(registry.lookup(rmiService));
            friendStub = (FriendInterface)(registry.lookup(rmiService));
            tournamentStub = (TournamentInterface)(registry.lookup(rmiService));
            groupStub = (GroupInterface)(registry.lookup(rmiService));
            standingsStub = (StandingsInterface) (registry.lookup(rmiService));
            matchStub = (MatchInterface)(registry.lookup(rmiService));

        }catch(NotBoundException | RemoteException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public Player remoteCheckLogin(Player user){
        try {
            return playerStub.checkLogin(user);
        }catch(RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean remoteAddPlayer(Player player){
        try {
            return playerStub.addPlayer(player);
        }catch(RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public ArrayList<Player> remoteSearchPlayerByName(String key){
        try {
            return playerStub.searchPlayerByName(key);
        }catch(RemoteException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
//    FriendDAO
    public ArrayList<Player> remoteGetFriendList(Player player){
        try {
            return friendStub.getFriendList(player);
        }catch(RemoteException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public ArrayList<Invitation> remoteGetFriendRequests(Player player){
        try {
            return friendStub.getFriendRequests(player);
        }catch(RemoteException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public Friendship remoteGetFriendship(Player user, Player friend){
        try {
            return friendStub.getFriendship(user, friend);
        }catch(RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean remoteAddFriend(Player user, Player friend){
        try {
            return friendStub.addFriend(user, friend);
        }catch(RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean remoteAcceptFriendRequest(Player user, Player friend) {
        try {
            return friendStub.acceptFriendRequest(user, friend);
        }catch(RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean remoteDeclineFriendRequest(Player user, Player friend) {
        try {
            return friendStub.declineFriendRequest(user, friend);
        }catch(RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }
    
//    TournamentDAO
    public ArrayList<Tournament> remoteGetTournaments(Player player){
        try {
            return tournamentStub.getTournaments(player);
        }catch(RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }
    
//    GroupDAO
    public ArrayList<Group> remoteGetGroupList(Player player){
        try {
            return groupStub.getGroupList(player);
        }catch(RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public ArrayList<Invitation> remoteGetGroupInvitations(Player player){
        try {
            return groupStub.getGroupInvitations(player);
        }catch(RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public ArrayList<Player> remoteGetGroupMembers(Group group){
        try {
            return groupStub.getGroupMembers(group);
        }catch(RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Group remoteCreateGroup(Group group, Player player){
        try {
            return groupStub.createGroup(group, player);
        }catch(RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean remoteInvitePlayerToGroup(Player inviter, GroupMember groupMember){
        try {
            return groupStub.invitePlayerToGroup(inviter, groupMember);
        }catch(RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean remoteAcceptGroupInvitation(Group group, Player player){
        try {
            return groupStub.acceptGroupInvitation(group, player);
        }catch(RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean remoteDeclineGroupInvitation(Group group, Player player){
        try {
            return groupStub.declineGroupInvitation(group, player);
        }catch(RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }
    
//    Standings
    public ArrayList<StandingsDetail> remoteGetStandings(){
        try {
            return standingsStub.getStandings();
        }catch(RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean remoteSaveMatch(Match match, Player player1, Player player2){
        try {
            return matchStub.saveMatch(match, player1, player2);
        }catch(RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }
}
