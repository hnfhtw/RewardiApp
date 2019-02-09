/********************************************************************************************
 * Project    : Rewardi
 * Created on : 12/2018 - 01/2019
 * Author     : Harald Netzer
 * Version    : 001
 *
 * File       : HistoryItemGadget.java
 * Purpose    : Base class for history items for spent Rewardi (by using Gadgets)
 ********************************************************************************************/

package me.rewardi;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)  // to serialize HistoryItemGadget object for passing it between activities via intents
public class HistoryItemGadget implements Comparable<HistoryItemGadget>{
    private int id;
    private String timestamp;

    public HistoryItemGadget() {}

    @ParcelConstructor
    public HistoryItemGadget(int id, String timestamp){
        this.id = id;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(HistoryItemGadget historyItemGadget) { // used to list the history items ordered by timestamp
        return this.timestamp.compareTo(historyItemGadget.getTimestamp());
    }
}
