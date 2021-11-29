package jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import model.Friendship;
import model.Invitation;
import model.Player;

public class FriendDAO extends DAO {
    public FriendDAO() {
            super();
    }
    
    public ArrayList<Player> getFriendList(Player player){
        ArrayList<Player> friendList = new ArrayList<>();

        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append("select p.id as id, p.name as name from tblplayer p where p.id in "
                +"(select friendID from tblfriends where userID = ? and status = 1);");

        try {
                PreparedStatement ps = con.prepareStatement(sqlQuery.toString());
                ps.setInt(1, player.getId());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Player friend = new Player();
                    friend.setId(rs.getInt("id"));
                    friend.setName(rs.getString("name"));
                    friendList.add(friend);
                }
                return friendList;
        } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
        }
    }
    
    public ArrayList<Invitation> getFriendRequests(Player player){
        ArrayList<Invitation> invitations = new ArrayList<>();
        String sql = "select p.id, p.name from chess.tblplayer p "
                + "join chess.tblfriends f on p.id = f.userID "
                + "where f.friendID = ? and f.status = 0";
                
        try {
                PreparedStatement ps = con.prepareStatement(sql.toString());
                ps.setInt(1, player.getId());
                
                ResultSet rs = ps.executeQuery();
                while (rs.next()) { 
                    Player inviter = new Player();
                    inviter.setId(rs.getInt("id"));
                    inviter.setName(rs.getString("name"));

                    Invitation invitation = new Invitation();
                    invitation.setType(Invitation.FRIEND_REQUEST);
                    invitation.setInviter(inviter);
                    invitations.add(invitation);
                }
                return invitations;
        } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
        }    
    }
    
    public Friendship getFriendship(Player user, Player friend){
        String sql = "SELECT * FROM tblfriends WHERE userID = ? AND friendID = ?";
        String sql2 = "SELECT * FROM tblfriends WHERE userID = ? AND friendID = ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, user.getId());
            ps.setInt(2, friend.getId());
            Friendship friendship = new Friendship();
            ResultSet rs = ps.executeQuery();
            
            if (!rs.isBeforeFirst() ){
                ps = con.prepareStatement(sql2);
                ps.setInt(1, friend.getId());
                ps.setInt(2, user.getId());
                rs = ps.executeQuery();
            }
            
            if(rs.next()) {
                friendship.setId(rs.getInt("id"));
                friendship.setUserID(rs.getInt("userID"));
                friendship.setFriendID(rs.getInt("friendID"));
                friendship.setStatus(rs.getInt("status"));
                return friendship;
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addFriend(Player user, Player friend) {
        Friendship friendship = getFriendship(user, friend);
        String sql;
        if(friendship instanceof Friendship) {
            if(friendship.getUserID()== user.getId()){
                sql = "UPDATE tblfriends set status=0 WHERE userID = ? AND friendID = ?";
            }else {
                sql = "INSERT INTO tblfriends (userID, friendID, status) VALUES(?, ?, 0)";
            }
        } else {
            sql = "INSERT INTO tblfriends (userID, friendID, status) VALUES(?, ?, 0)";
        }
        try {
                    PreparedStatement ps = con.prepareStatement(sql.toString());
                    ps.setInt(1, user.getId());
                    ps.setInt(2, friend.getId());
                    ps.executeUpdate();
                    return true;
            } catch (Exception e) {
                    e.printStackTrace();
                    return false;
            }
    }
    
    public boolean updateAcceptance(Player user, Player friend) {
        String sqlUpdate = "UPDATE tblfriends set status=1 WHERE userID = ? AND friendID = ?";
        try {
                PreparedStatement ps = con.prepareStatement(sqlUpdate.toString());
                ps.setInt(1, friend.getId());
                ps.setInt(2, user.getId());
                ps.executeUpdate();
                
                Friendship friendship = getFriendship(user, friend);
                String sql = "INSERT INTO tblfriends (userID, friendID, status) VALUES(?, ?, 1)";
                if(friendship instanceof Friendship) {
                   if(friendship.getUserID()== user.getId()){
                        sql = "UPDATE tblfriends set status=1 WHERE userID = ? AND friendID = ?";
                    }
                }
                
                ps = con.prepareStatement(sql.toString());
                ps.setInt(1, user.getId());
                ps.setInt(2, friend.getId());
                ps.executeUpdate();
                return true;
        } catch (Exception e) {
                e.printStackTrace();
                return false;
        }
    }
    
    public boolean updateDeclination(Player user, Player friend) {
        String sql = "UPDATE tblfriends set status=2 WHERE userID = ? AND friendID = ?";
        try {
                PreparedStatement ps = con.prepareStatement(sql.toString());
                ps.setInt(1, friend.getId());
                ps.setInt(2, user.getId());
                ps.executeUpdate();
                return true;
        } catch (Exception e) {
                e.printStackTrace();
                return false;
        }
    }
}

