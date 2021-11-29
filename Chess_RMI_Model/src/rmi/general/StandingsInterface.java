package rmi.general;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import model.StandingsDetail;

public interface StandingsInterface extends Remote{
    public ArrayList<StandingsDetail> getStandings() throws RemoteException;
}