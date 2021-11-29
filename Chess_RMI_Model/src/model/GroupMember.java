package model;

import java.io.Serializable;

public class GroupMember implements Serializable{
    private static final long serialVersionUID = 1L;
    private int id;
    private int playerid;
    private int groupid;
    private int isAccepted;

    public GroupMember() {
    }

    public GroupMember(int id, int playerid, int groupid, int isAccepted) {
        this.id = id;
        this.playerid = playerid;
        this.groupid = groupid;
        this.isAccepted = isAccepted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayerid() {
        return playerid;
    }

    public void setPlayerid(int playerid) {
        this.playerid = playerid;
    }

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public int getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(int isAccepted) {
        this.isAccepted = isAccepted;
    }
}
