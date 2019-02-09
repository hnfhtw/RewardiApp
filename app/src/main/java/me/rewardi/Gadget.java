/********************************************************************************************
 * Project    : Rewardi
 * Created on : 12/2018 - 01/2019
 * Author     : Harald Netzer
 * Version    : 001
 *
 * File       : Gadget.java
 * Purpose    : Representation of a Rewardi Gadget;
 ********************************************************************************************/

package me.rewardi;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)  // to serialize Gadget object for passing it between activities via intents
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
