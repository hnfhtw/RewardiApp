package me.rewardi;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class Gadget {
    private int id;
    private String trustNumber;

    private String name;

    public Gadget(){
        this.id = 0;
        this.trustNumber = null;
        this.name = null;
    }

    @ParcelConstructor
    public Gadget(int id, String trustNumber, String name){
        this.id = id;
        this.trustNumber = trustNumber;
        this.name = name;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTrustNumber() { return trustNumber; }
    public void setTrustNumber(String trustNumber) { this.trustNumber = trustNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
