package model;

import java.io.Serializable;

public class Player implements Serializable{
    private int id;
    private String name;
    private String username;
    private String password;
    
    public Player() {
    }

    public Player(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    
    @Override
    public boolean equals(Object object) {
        if (object != null && object instanceof Player) {
            Player player = (Player) object;
            return (id == player.getId());
        }
        return false;
    }
}
