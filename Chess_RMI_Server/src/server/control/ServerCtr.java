package server.control;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import jdbc.dao.FriendDAO;
import jdbc.dao.GroupDAO;
import jdbc.dao.MatchDAO;

import jdbc.dao.PlayerDAO;
import jdbc.dao.TournamentDAO;
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
import server.view.ServerMainFrm;
 
 
public class ServerCtr extends UnicastRemoteObject 
        implements PlayerInterface,FriendInterface,
                    TournamentInterface,GroupInterface,
                    StandingsInterface, MatchInterface{
    private IPAddress myAddress = new IPAddress("localhost", 9999);     // default server host/port
    private Registry registry;
    private ServerMainFrm view;
    private String rmiService = "rmiServer";    // default rmi service key
     
    public ServerCtr(ServerMainFrm view) throws RemoteException{
        this.view = view;   
    }
     
    public ServerCtr(ServerMainFrm view, int port, String service) throws RemoteException{
        this.view = view;   
        myAddress.setPort(port);
        this.rmiService = service;
    }
     
    public void start() throws RemoteException{
        // registry this to the localhost
        try{
            try {
                //create new one
                registry = LocateRegistry.createRegistry(myAddress.getPort());
            }catch(ExportException e) {//the Registry exists, get it
                registry = LocateRegistry.getRegistry(myAddress.getPort());
            }
            registry.rebind(rmiService, this);
            myAddress.setHost(InetAddress.getLocalHost().getHostAddress());
            view.showServerInfo(myAddress, rmiService);
            view.showMessage("The RIM has registered the service key: " + rmiService + ", at the port: " + myAddress.getPort());
        }catch(RemoteException e){
            throw e;
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
     
    public void stop() throws RemoteException{
        // unbind the service
        try{
            if(registry != null) {
                registry.unbind(rmiService);
                UnicastRemoteObject.unexportObject(this,true);
            }
            view.showMessage("The RIM has unbinded the service key: " + rmiService + ", at the port: " + myAddress.getPort());
        }catch(RemoteException e){
            throw e;
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
     
//    PlayerDAO
    @Override
    public Player checkLogin(Player player) throws RemoteException{
        return new PlayerDAO().checkLogin(player);
    }
    
    @Override
    public boolean addPlayer(Player player) throws RemoteException{
        return new PlayerDAO().addPlayer(player);
    }

    @Override
    public ArrayList<Player> searchPlayerByName(String key) throws RemoteException{
        return new PlayerDAO().searchPlayer(key);
    }
    
//    FriendDAO
    @Override
    public ArrayList<Player> getFriendList(Player player) throws RemoteException {
        return new FriendDAO().getFriendList(player);
    }

    @Override
    public ArrayList<Invitation> getFriendRequests(Player player) throws RemoteException {
        return new FriendDAO().getFriendRequests(player);
    }

    @Override
    public Friendship getFriendship(Player user, Player friend) throws RemoteException {
        return new FriendDAO().getFriendship(user, friend);
    }

    @Override
    public boolean addFriend(Player user, Player friend) throws RemoteException {
        return new FriendDAO().addFriend(user, friend);
    }

    @Override
    public boolean acceptFriendRequest(Player user, Player friend) throws RemoteException {
        return new FriendDAO().updateAcceptance(user, friend);
    }

    @Override
    public boolean declineFriendRequest(Player user, Player friend) throws RemoteException {
        return new FriendDAO().updateDeclination(user, friend);
    }
    
//    TournamentDAO
    @Override
    public ArrayList<Tournament> getTournaments(Player player) throws RemoteException {
        return new TournamentDAO().getTournaments(player);
    }
//    GroupDAO
    @Override
    public ArrayList<Group> getGroupList(Player player) throws RemoteException {
        return new GroupDAO().getGroupList(player);
    }

    @Override
    public ArrayList<Invitation> getGroupInvitations(Player player) throws RemoteException {
        return new GroupDAO().getGroupInvitations(player);
    }

    @Override
    public ArrayList<Player> getGroupMembers(Group group) throws RemoteException {
        return new GroupDAO().getGroupMembers(group);
    }

    @Override
    public Group createGroup(Group group, Player player) throws RemoteException {
        return new GroupDAO().createGroup(group, player);
    }

    @Override
    public boolean invitePlayerToGroup(Player inviter, GroupMember groupMember) throws RemoteException {
        return new GroupDAO().invitePlayerToGroup(inviter, groupMember);
    }

    @Override
    public boolean acceptGroupInvitation(Group group, Player player) throws RemoteException {
        return new GroupDAO().acceptGroupInvitation(group, player);
    }

    @Override
    public boolean declineGroupInvitation(Group group, Player player) throws RemoteException {
        return new GroupDAO().declineGroupInvitation(group, player);
    }

    @Override
    public ArrayList<StandingsDetail> getStandings() throws RemoteException {
        return new MatchDAO().getStandings();
    }
    
    @Override
    public boolean saveMatch(Match match, Player p1, Player p2) throws RemoteException {
        return new MatchDAO().saveMatch(match, p1, p2);
    }

}