package com.smartconnect.locationsockets.Model;

public class TabListing {
    private String UUID;
    private String roomName;
    private String roomLocation;
    private Boolean onlineStatus;

    public TabListing() {
    }

    public TabListing(String UUID, String roomName, String roomLocation, Boolean onlineStatus) {
        this.UUID = UUID;
        this.roomName = roomName;
        this.roomLocation = roomLocation;
        this.onlineStatus = onlineStatus;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomLocation() {
        return roomLocation;
    }

    public void setRoomLocation(String roomLocation) {
        this.roomLocation = roomLocation;
    }

    public Boolean getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(Boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
    }
}

