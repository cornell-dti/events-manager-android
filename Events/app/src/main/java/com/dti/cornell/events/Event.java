package com.dti.cornell.events;

import android.util.Log;

import org.joda.time.DateTime;

import java.util.regex.Pattern;

/**
 * Created by jaggerbrulato on 2/27/18.
 */

public class Event {

    public double latitude = 20;
    public double longitude = 40;
    public String caption = "daddy";


    public String name;
    public String description;
    public int attendees;
    public boolean isPublic;
    public String location;
    public String friendsGoing;
    //Remove temp time strings
    public String startTime;
    public String endTime;
    //TODO public final DateTime startTime;
    //TODO public final DateTime endTime;
    public static final String fieldSeparator = "|";


    public Event(String name, String description, int attendees, boolean isPublic, String location, String startTime, String endTime){
        this.name = name;
        this.description = description;
        this.attendees = attendees;
        this.isPublic = isPublic;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.friendsGoing = getFriendsGoing(this);
    }

    public Event(){}

    public String toString(){
        return name + fieldSeparator + description + fieldSeparator + attendees + fieldSeparator + isPublic + fieldSeparator + location + fieldSeparator + startTime + fieldSeparator + endTime;
    }

    public static Event fromString(String input){
        String[] values = input.split(Pattern.quote(Event.fieldSeparator));
        String name = String.valueOf(values[0]);
        String description = String.valueOf(values[1]);
        int attendees = Integer.valueOf(values[2]);
        boolean isPublic = Boolean.valueOf(values[3]);
        String location = String.valueOf(values[4]);
        String startTime = String.valueOf(values[5]);
        String endTime = String.valueOf(values[6]);
        return new Event(name, description, attendees, isPublic, location, startTime, endTime);
    }

    public String getFriendsGoing(Event event){
        //Get this from backend.
        return "Kelly, Andie, and 10 Friends";
    }

}
