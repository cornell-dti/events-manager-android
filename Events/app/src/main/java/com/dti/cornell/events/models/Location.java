package com.dti.cornell.events.models;

/**
 * Created by jboss925 on 9/1/19.
 */

public class Location {

    public int id;
    public String room;
    public String building;
    public String placeID;
    public float xCoord;
    public float yCoord;


    public Location(int id, String room, String building, String placeID){
        this.id = id;
        this.room = room;
        this.building = building;
        this.placeID = placeID;
    }

}
