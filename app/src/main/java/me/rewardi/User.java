package me.rewardi;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class User {
    enum supervisorStatusTypes {NONE,LINK_PENDING, LINKED, UNLINK_PENDING};

    private int id;                 // UserID of User
    private String deviceId;           // Firebase Token
    private double totalRewardi;       // Rewardi Account of User
    private int fkPartnerUserId;    // UserID of Supervisor
    private String userName;        // Rewardi user name
    private String email;           // email address of user
    private String supervisorName; // Rewardi user name of Supervisor
    private String supervisorMailAddress;  // mail address of supervisor
    private supervisorStatusTypes supervisorStatus;


    public User(){}

    @ParcelConstructor
    public User(int id, String deviceId, double totalRewardi, int fkPartnerUserId, String userName, String email, String supervisorName, String supervisorMailAddress, supervisorStatusTypes supervisorStatus){
        this.id = id;
        this.deviceId = deviceId;
        this.totalRewardi = totalRewardi;
        this.fkPartnerUserId = fkPartnerUserId;
        this.userName = userName;
        this.email = email;
        this.supervisorName = supervisorName;
        this.supervisorMailAddress = supervisorMailAddress;
        this.supervisorStatus = supervisorStatus;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getDeviceId() { return deviceId; }

    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public double getTotalRewardi() { return totalRewardi; }

    public void setTotalRewardi(double totalRewardi) { this.totalRewardi = totalRewardi; }

    public int getFkPartnerUserId() { return fkPartnerUserId; }

    public void setFkPartnerUserId(int fkPartnerUserId) { this.fkPartnerUserId = fkPartnerUserId; }

    public String getUserName() { return userName; }

    public void setUserName(String userName) { this.userName = userName; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getSupervisorName() { return supervisorName; }

    public void setSupervisorName(String supervisorName) { this.supervisorName = supervisorName; }

    public String getSupervisorMailAddress() { return supervisorMailAddress; }

    public void setSupervisorMailAddress(String supervisorMailAddress) { this.supervisorMailAddress = supervisorMailAddress; }

    public supervisorStatusTypes getSupervisorStatus() { return supervisorStatus; }

    public void setSupervisorStatus(supervisorStatusTypes supervisorStatus) { this.supervisorStatus = supervisorStatus; }

}
