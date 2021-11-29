package model;

import java.io.Serializable;

public class Friendship implements Serializable{
    private int id;
    private int userID;
    private int friendID;
    private int status;

    public Friendship() {
    }

    public Friendship(int userID, int friendID, int status) {
        this.userID = userID;
        this.friendID = friendID;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getFriendID() {
        return friendID;
    }

    public void setFriendID(int friendID) {
        this.friendID = friendID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    
}
