package com.dti.cornell.events.models;

/**
 * Created by jboss925 on 9/1/19.
 */

public class Location {

    public int id;
    public String room;
    public String building;
    public String placeID;

    private static String SEPARATOR = ">>";


    public Location(int id, String room, String building, String placeID){
        this.id = id;
        this.room = room;
        this.building = building;
        this.placeID = placeID;
    }

    public String toString(){
        String res = id + SEPARATOR +
                room + SEPARATOR +
                building + SEPARATOR
                + placeID;
        return res;
    }

    public static Location fromString(String locString){
        String[] splits = locString.split(SEPARATOR);
        int id = Integer.valueOf(splits[0]);
        String room = splits[1];
        String building = splits[2];
        String placeID = splits[3];
        return new Location(id, room, building, placeID);
    }

}
