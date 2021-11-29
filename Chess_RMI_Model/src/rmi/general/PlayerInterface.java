package rmi.general;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import model.Player;

public interface PlayerInterface extends Remote{
    public Player checkLogin(Player player) throws RemoteException;
    
    public boolean addPlayer(Player player) throws RemoteException;
    
    public ArrayList<Player> searchPlayerByName(String key) throws RemoteException;
}
