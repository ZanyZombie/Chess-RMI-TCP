package rmi.general;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import model.Group;
import model.GroupMember;
import model.Invitation;
import model.Player;

public interface GroupInterface extends Remote{
    public ArrayList<Group> getGroupList(Player player) throws RemoteException;
    
    public ArrayList<Invitation> getGroupInvitations(Player player) throws RemoteException;
            
    public ArrayList<Player> getGroupMembers(Group group) throws RemoteException;
            
    public Group createGroup(Group group, Player player) throws RemoteException;
            
    public boolean invitePlayerToGroup(Player inviter, GroupMember groupMember) throws RemoteException;
    
    public boolean acceptGroupInvitation(Group group, Player player) throws RemoteException;
    
    public boolean declineGroupInvitation(Group group, Player player) throws RemoteException;

}