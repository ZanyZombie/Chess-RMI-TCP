package rmi.general;

import java.rmi.Remote;
import java.rmi.RemoteException;
import model.Match;
import model.Player;

public interface MatchInterface extends Remote{
    public boolean saveMatch(Match match, Player p1, Player p2) throws RemoteException;
}