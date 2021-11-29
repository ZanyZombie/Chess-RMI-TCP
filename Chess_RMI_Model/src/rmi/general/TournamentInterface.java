package rmi.general;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import model.Player;
import model.Tournament;

public interface TournamentInterface extends Remote{
    public ArrayList<Tournament> getTournaments(Player player) throws RemoteException;
}