/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.client;

/**
 *
 * @author Mohamed_Ali
 */

import javafx.beans.property.*;

public class Player {
    private final SimpleStringProperty playerName;
    private final SimpleIntegerProperty rank;
    private final SimpleStringProperty status;
    private final SimpleStringProperty avatar;
    private final SimpleStringProperty statusColor;
    
   
    public Player(String playerName, int rank, String status, String avatar, String statusColor) {
        this.playerName = new SimpleStringProperty(playerName);
        this.rank = new SimpleIntegerProperty(rank);
        this.status = new SimpleStringProperty(status);
        this.avatar = new SimpleStringProperty(avatar);
        this.statusColor = new SimpleStringProperty(statusColor);
    }
    
    
    public StringProperty playerNameProperty() {
        return playerName;
    }
    
    public IntegerProperty rankProperty() {
        return rank;
    }
    
    public StringProperty statusProperty() {
        return status;
    }
    
    public StringProperty avatarProperty() {
        return avatar;
    }
    
    public StringProperty statusColorProperty() {
        return statusColor;
    }
    
   
    public String getPlayerName() {
        return playerName.get();
    }
    
    public int getRank() {
        return rank.get();
    }
    
    public String getStatus() {
        return status.get();
    }
    
    public String getAvatar() {
        return avatar.get();
    }
    
    public String getStatusColor() {
        return statusColor.get();
    }
    
    // Setters
    public void setPlayerName(String value) {
        playerName.set(value);
    }
    
    public void setRank(int value) {
        rank.set(value);
    }
    
    public void setStatus(String value) {
        status.set(value);
    }
    
    public void setAvatar(String value) {
        avatar.set(value);
    }
    
    public void setStatusColor(String value) {
        statusColor.set(value);
    }
}