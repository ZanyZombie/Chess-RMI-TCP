/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

public class StandingsDetail implements Serializable{
    private Player player;
    private int played;
    private int won;

    public StandingsDetail() {
    }

    public StandingsDetail(Player player, int played, int won) {
        this.player = player;
        this.played = played;
        this.won = won;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getPlayed() {
        return played;
    }

    public void setPlayed(int played) {
        this.played = played;
    }

    public int getWon() {
        return won;
    }

    public void setWon(int won) {
        this.won = won;
    }
}
