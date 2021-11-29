package jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import static jdbc.dao.DAO.con;
import model.Player;
import model.Tournament;

public class TournamentDAO extends DAO{
    public TournamentDAO() {
        super();
    }
    
    public ArrayList<Tournament> getTournaments(Player player) {
        String sql = "SELECT t.* FROM tbltournament t "
                + "JOIN tblplayertournament pt ON t.id = pt.tournamentID "
                + "WHERE pt.isAccepted = 1 AND pt.playerID = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql.toString());
            ps.setInt(1, player.getId());
            ResultSet rs = ps.executeQuery();
            ArrayList<Tournament> tournaments = new ArrayList<>();
            while (rs.next()) {
                    Tournament tournament = new Tournament();
                    tournament.setId(rs.getInt("id"));
                    tournament.setName(rs.getString("name"));
                    tournament.setStarttime(rs.getTimestamp("starttime"));
                    tournament.setEndtime(rs.getTimestamp("endtime"));
//                    tournament.setDesc(rs.getString("desc"));
                    
                    tournaments.add(tournament);
            }
            return tournaments;
        } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
        }
    }
}
