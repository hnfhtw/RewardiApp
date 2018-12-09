package me.rewardi;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class User {
    private int id;                 // UserID of User
    private String deviceId;           // Firebase Token
    private int totalRewardi;       // Rewardi Account of User
    private int fkPartnerUserId;    // UserID of Supervisor

    public User(){}

    @ParcelConstructor
    public User(int id, String deviceId, int totalRewardi, int fkPartnerUserId){
        this.id = id;
        this.deviceId = deviceId;
        this.totalRewardi = totalRewardi;
        this.fkPartnerUserId = fkPartnerUserId;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getDeviceId() { return deviceId; }

    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public int getTotalRewardi() { return totalRewardi; }

    public void setTotalRewardi(int totalRewardi) { this.totalRewardi = totalRewardi; }

    public int getFkPartnerUserId() { return fkPartnerUserId; }

    public void setFkPartnerUserId(int fkPartnerUserId) { this.fkPartnerUserId = fkPartnerUserId; }
}
