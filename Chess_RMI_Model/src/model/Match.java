package model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Match implements Serializable{
    private int id;
    private int tournamentId;
    private int result;
    private Timestamp starttime;
    private Timestamp endtime;

    public Match() {
    }

    public Match(int id, int tournamentId, int result, Timestamp starttime, Timestamp endtime) {
        this.id = id;
        this.tournamentId = tournamentId;
        this.result = result;
        this.starttime = starttime;
        this.endtime = endtime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(int tournamentId) {
        this.tournamentId = tournamentId;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public Timestamp getStarttime() {
        return starttime;
    }

    public void setStarttime(Timestamp starttime) {
        this.starttime = starttime;
    }

    public Timestamp getEndtime() {
        return endtime;
    }

    public void setEndtime(Timestamp endtime) {
        this.endtime = endtime;
    }
    
    
}
