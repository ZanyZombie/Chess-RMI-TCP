package rmi.general;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import model.Friendship;
import model.Invitation;
import model.Player;

public interface FriendInterface extends Remote{
    public ArrayList<Player> getFriendList(Player player) throws RemoteException;
    
    public ArrayList<Invitation> getFriendRequests(Player player) throws RemoteException;
    
    public Friendship getFriendship(Player user, Player friend) throws RemoteException;
    
    public boolean addFriend(Player user, Player friend) throws RemoteException;
    
    public boolean acceptFriendRequest(Player user, Player friend) throws RemoteException;
    
    public boolean declineFriendRequest(Player user, Player friend) throws RemoteException;
    
}