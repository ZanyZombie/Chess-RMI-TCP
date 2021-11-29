package jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import static jdbc.dao.DAO.con;
import model.Match;
import model.Player;
import model.StandingsDetail;

public class MatchDAO extends DAO{
    public MatchDAO() {
        super();
    }
    
    public ArrayList<StandingsDetail> getStandings(){
        String sql = "SELECT p.id, p.name, count(m.id) as played,  sum(case when m.result = p.id then 1 else 0 end) as won "
                + "FROM chess.tblplayer p "
                + "left join chess.tblplayermatch pm on pm.playerID = p.id "
                + "left join chess.tblmatch m on m.id = pm.matchID "
                + "where m.tournamentID is null "
                + "group by p.id order by won DESC , played DESC;";
        try {
            PreparedStatement ps = con.prepareStatement(sql.toString());
            ResultSet rs = ps.executeQuery();
            ArrayList<StandingsDetail> standings = new ArrayList<>();
            while (rs.next()) {
                    StandingsDetail row = new StandingsDetail();
                    Player p = new Player();
                    p.setId(rs.getInt("id"));
                    p.setName(rs.getString("name"));
                    row.setPlayer(p);
                    row.setPlayed(rs.getInt("played"));
                    row.setWon(rs.getInt("won"));
                    standings.add(row);
            }
            return standings;
        } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
        }
    }
    
    public boolean saveMatch(Match match, Player player1, Player player2){
        String matchSql = "INSERT INTO tblmatch (result, startdate, enddate) VALUES (?, ?, ?)";
        String sqlGetId = "SELECT LAST_INSERT_ID()";

        String matchPlayerSql =  "INSERT INTO tblplayermatch (playerID, matchID) VALUES (?, ?)";
        try {
            con.setAutoCommit(false);
            
            PreparedStatement ps = con.prepareStatement(matchSql);
            ps.setInt(1, match.getResult());
            ps.setTimestamp(2, match.getStarttime());
            ps.setTimestamp(3, match.getEndtime());
            ps.executeUpdate();
            
            ps = con.prepareStatement(sqlGetId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            match.setId(rs.getInt(1));
            
            ps = con.prepareStatement(matchPlayerSql);
            ps.setInt(1, player1.getId());
            ps.setInt(2, match.getId());
            ps.executeUpdate();

            ps = con.prepareStatement(matchPlayerSql);
            ps.setInt(1, player2.getId());
            ps.setInt(2, match.getId());
            ps.executeUpdate();

            con.commit();
            con.setAutoCommit(true);
        }catch(Exception e) {
            e.printStackTrace();
            try {
                    con.rollback();
            } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
            }
            return false;
        }
        return true;
    } 
}
