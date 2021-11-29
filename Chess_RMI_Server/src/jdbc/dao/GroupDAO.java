package jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.Group;
import model.GroupMember;
import model.Invitation;
import model.Player;

public class GroupDAO extends DAO{
    public GroupDAO() {
        super();
    }
    
    public Group createGroup(Group group, Player player) {
        String sqlInsertGroup = "INSERT INTO tblgroup (`name`, `desc`) VALUES(?, ?)";
        String sqlGetId = "SELECT LAST_INSERT_ID()";
        String sqlInsertMember = "INSERT INTO tblgroupmember (playerid, groupid, isaccepted) VALUES(?, ?, 1)";

        try {
            PreparedStatement ps = con.prepareStatement(sqlInsertGroup);
            ps.setString(1, group.getName());
            ps.setString(2, group.getDesc());
            ps.executeUpdate();

            ps = con.prepareStatement(sqlGetId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            group.setId(rs.getInt(1));

            ps = con.prepareStatement(sqlInsertMember);
            ps.setInt(1, player.getId());
            ps.setInt(2, group.getId());
            ps.executeUpdate();

            return group;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public ArrayList<Group> getGroupList(Player player) {
        String sql = "SELECT g.* FROM tblgroup g "
                + "JOIN tblgroupmember gm ON g.id = gm.groupID "
                + "WHERE gm.isAccepted = 1 AND gm.playerID = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql.toString());
            ps.setInt(1, player.getId());
            ResultSet rs = ps.executeQuery();
            ArrayList<Group> groups = new ArrayList<>();
            while (rs.next()) {
                    Group group = new Group();
                    group.setId(rs.getInt("id"));
                    group.setName(rs.getString("name"));
                    group.setDesc(rs.getString("desc"));
                    groups.add(group);
            }
            return groups;
        } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
        }
    }
    
    public ArrayList<Invitation> getGroupInvitations(Player player) {
        ArrayList<Invitation> invitations = new ArrayList<>();
        String sql = "select p.id as pid, p.name as pname, g.id as gid, g.name as gname, g.desc as gdesc " 
                + "from tblplayer p " 
                + "inner join tblgroupinvitation i on i.invitedBy = p.id "
                + "inner join tblgroupmember m on m.id = i.groupMemberId " 
                + "inner join tblgroup g on g.id = m.groupID "
                + "where m.playerID = ? and isAccepted = 0";
        
        try{
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, player.getId());
                ResultSet rs = ps.executeQuery();

                while (rs.next()){
                    Player inviter = new Player();
                    inviter.setId(rs.getInt("pid"));
                    inviter.setName(rs.getString("pname"));
                    
                    Group group = new Group();
                    group.setId(rs.getInt("gid"));
                    group.setName(rs.getString("gname"));
                    group.setDesc(rs.getString("gdesc"));
                    
                    Invitation inv = new Invitation();
                    inv.setType(Invitation.GROUP_INVITATION);
                    inv.setInviter(inviter);
                    inv.setTarget(group);
                    
                    invitations.add(inv);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return invitations;
    }
    
    public ArrayList<Player> getGroupMembers(Group group){
        ArrayList<Player> players = new ArrayList<>();
            String sql = "SELECT p.id, p.name from tblplayer p "
                    + "join tblgroupmember gm on gm.playerID = p.id "
                    + "where gm.groupID = ? and gm.isAccepted = 1";
            try{
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, group.getId());
                ResultSet rs = ps.executeQuery();

                while (rs.next()){
                    Player player = new Player();
                    player.setId(rs.getInt("id"));
                    player.setName(rs.getString("name"));
                    players.add(player);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        
            return players;
    }
    
    public boolean invitePlayerToGroup(Player inviter, GroupMember groupMember) {
            try {
                    con.setAutoCommit(false);
                    GroupMember isExisted = getGroupMember(groupMember);
                    if (isExisted == null){
                        int idGroupMember = createNewMemberGroup(groupMember);
                        isExisted = new GroupMember();
                        isExisted.setId(idGroupMember);
                        System.out.println(isExisted.getId() + "");
                    }else if (isExisted.getIsAccepted() == 2){
                        String sql = "UPDATE tblgroupmember set isAccepted=0 WHERE playerID = ? AND groupID = ?";
                            PreparedStatement ps = con.prepareStatement(sql.toString());
                            ps.setInt(2, groupMember.getGroupid());
                            ps.setInt(1, groupMember.getPlayerid());
                            ps.executeUpdate();
                    }
                    if (!isInviatationExisted(isExisted, inviter)){
                        createNewInviation(isExisted, inviter);
                    }
                    con.commit();
                    con.setAutoCommit(true);

                    return true;
            } catch (Exception e) {
                    e.printStackTrace();
                    try {
                            con.rollback();
                    } catch (SQLException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                    }
                    return false;
            }
	}
    
    public boolean acceptGroupInvitation(Group group, Player player) {
            String sqlQuery = "UPDATE tblgroupmember set isAccepted=1 WHERE playerID = ? AND groupID = ?";
            try {
                    PreparedStatement ps = con.prepareStatement(sqlQuery.toString());
                    ps.setInt(1, player.getId());
                    ps.setInt(2, group.getId());
                    ps.executeUpdate();
                    return true;
            } catch (Exception e) {
                    e.printStackTrace();
                    return false;
            }
        }
        
        public boolean declineGroupInvitation(Group group, Player player) {
            String sqlQuery = "UPDATE tblgroupmember set isAccepted=2 WHERE playerID = ? AND groupID = ?";
            try {
                    PreparedStatement ps = con.prepareStatement(sqlQuery.toString());
                    ps.setInt(1, player.getId());
                    ps.setInt(2, group.getId());
                    ps.executeUpdate();
                    return true;
            } catch (Exception e) {
                    e.printStackTrace();
                    return false;
            }
           
        }
    
    private GroupMember getGroupMember(GroupMember groupMember){
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append("SELECT id, playerID, groupID, isAccepted ");
        sqlQuery.append("FROM tblgroupmember ");
        sqlQuery.append("where playerID = ? and groupID  = ?");
        try {
                PreparedStatement ps = con.prepareStatement(sqlQuery.toString());
                ps.setInt(2, groupMember.getGroupid());
                ps.setInt(1, groupMember.getPlayerid());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    GroupMember member = new GroupMember();
                    member.setId(rs.getInt("id"));
                    member.setPlayerid(rs.getInt("playerID"));
                    member.setGroupid(rs.getInt("groupID"));
                    member.setIsAccepted(rs.getInt("isAccepted"));
                    return member;
                }
                else return null;
        } catch (Exception e) {
                e.printStackTrace();
                return null;
        }
        }
        
    private boolean isInviatationExisted(GroupMember groupMember, Player player){
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append("SELECT id ");
        sqlQuery.append("FROM tblgroupinvitation ");
        sqlQuery.append("where invitedBy = ? and groupMemberId  = ?");
        try {
                PreparedStatement ps = con.prepareStatement(sqlQuery.toString());
                ps.setInt(1, player.getId());
                ps.setInt(2, groupMember.getId());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return true;
                }
                else return false;
        } catch (Exception e) {
                e.printStackTrace();
                return false;
        }
    }

    private int createNewMemberGroup(GroupMember groupMember) throws Exception {
        String sql = "INSERT INTO tblgroupmember (playerid, groupid, isaccepted) VALUES(?, ?, 0)";
        String sqlGetId = "SELECT LAST_INSERT_ID()";

        try {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, groupMember.getPlayerid());
                ps.setInt(2, groupMember.getGroupid());
                ps.executeUpdate();

                ps = con.prepareStatement(sqlGetId);
                ResultSet rs = ps.executeQuery();
                rs.next();
                return rs.getInt(1);
        } catch (Exception e) {
                e.printStackTrace();
                throw e;
        }
    }

    private void createNewInviation(GroupMember groupMember, Player player) throws Exception {
            String sql = "INSERT INTO tblgroupinvitation (groupMemberId, invitedBy) VALUES(?, ?)";
            try {
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, groupMember.getId());
                    ps.setInt(2, player.getId());
                    ps.executeUpdate();
            } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
            }
    }

}
