package jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import model.Player;

public class PlayerDAO extends DAO{
    
    public PlayerDAO() {
        super();
    }
     
//    public Player getPlayerById(int groupId){
//    
//    }
    
    public Player checkLogin(Player user) {
        String sql = "SELECT  id, name FROM tblplayer WHERE username = ? AND password = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
             
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setUsername(null);
                user.setPassword(null);
                return user;
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addPlayer(Player player){
        String sql = "INSERT INTO tblplayer (name, username, password) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, player.getName());
            ps.setString(2, player.getUsername());
            ps.setString(3, player.getPassword());
             
            ps.executeUpdate();

        }catch(Exception e) {
            e.printStackTrace();
            return false;

        }
        return true;

    }
    
    public ArrayList<Player> searchPlayer(String key){
        ArrayList<Player> players = new ArrayList<>();
        String sql = "SELECT id, name FROM tblplayer WHERE name LIKE ?";

        try{
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1,"%" + key + "%");
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
}
