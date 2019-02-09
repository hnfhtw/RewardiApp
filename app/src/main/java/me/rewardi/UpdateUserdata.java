/********************************************************************************************
 * Project    : Rewardi
 * Created on : 12/2018 - 01/2019
 * Author     : Harald Netzer
 * Version    : 001
 *
 * File       : UpdateUserData.java
 * Purpose    : A request for updated user data is sent from Globals.java; Activities that are
 *              interested in getting the updated user data have to implement this interface -
 *              they are informed when updated user data (e.g. new Rewardi balance) are received
 ********************************************************************************************/

package me.rewardi;

public interface UpdateUserdata {
    void onUserDataUpdate(User user);
}
