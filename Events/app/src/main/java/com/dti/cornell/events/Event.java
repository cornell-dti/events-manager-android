package com.dti.cornell.events;

import android.media.Image;
import android.os.Build;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.regex.Pattern;

/**
 * Created by jaggerbrulato on 2/27/18.
 */

public class Event implements IDable{

    public final String googlePlaceId;
    public final String name;
    public final int eventID;
    public final String description;
    public final int attendees;
    public final boolean isPublic;
    public final String location;
    public final String friendsGoing;
    //Remove temp time strings
    public final DateTime startTime;
    public final DateTime endTime;
    public final int organizerPK;
    public final String organizerName;
    public final int pictureID;
    public final Image picture;
    //TODO public final DateTime startTime;
    //TODO public final DateTime endTime;
    public static final String fieldSeparator = "|";



    public Event(String name, String description, int attendees, boolean isPublic, String location, DateTime startTime, DateTime endTime, int organizerPK, int eventID, int pictureID, String googlePlaceId){
        this.name = name;
        this.description = description;
        this.attendees = attendees;
        this.isPublic = isPublic;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.friendsGoing = getFriendsGoing(this);
        this.organizerPK = organizerPK;
        this.eventID = eventID;
        this.pictureID = pictureID;
        this.googlePlaceId = googlePlaceId;
        this.organizerName = getOrganizerName();
        this.picture = getPicture();
    }

    public Image getPicture(){
        return null;
    }

    public String getOrganizerName(){
        return "get it from database lmao";
    }

//    //This empty constructor is for testing, delete once official event gets are working.
//    public Event(){}

    public String toString(){
        return name + fieldSeparator
                + description + fieldSeparator
                + attendees + fieldSeparator
                + isPublic + fieldSeparator
                + location + fieldSeparator
                + startTime.toString() + fieldSeparator
                + endTime.toString() + fieldSeparator
                + organizerPK + fieldSeparator
                + eventID + fieldSeparator
                + pictureID + fieldSeparator
                + googlePlaceId;
    }

    public static Event fromString(String input){
        String[] values = input.split(Pattern.quote(Event.fieldSeparator));
        String name = String.valueOf(values[0]);
        String description = String.valueOf(values[1]);
        int attendees = Integer.valueOf(values[2]);
        boolean isPublic = Boolean.valueOf(values[3]);
        String location = String.valueOf(values[4]);
        DateTime startTime = DateTime.parse(String.valueOf(values[5]));
        DateTime endTime = DateTime.parse(String.valueOf(values[6]));
        int organizerPK = Integer.valueOf(values[7]);
        int eventID = Integer.valueOf(values[8]);
        int pictureID = Integer.valueOf(values[9]);
        String googlePlaceID = String.valueOf(values[10]);
        return new Event(name, description, attendees, isPublic, location, startTime, endTime, organizerPK, eventID, pictureID, googlePlaceID);
    }

    public String getFriendsGoing(Event event){
        //TODO Get this from backend.
        return "Kelly, Andie, and 10 Friends";
    }

    @Override
    public int getID() {
        return eventID;
    }

    public static class Builder{
        private String googlePlaceId;
        private String name;
        private int eventID = -1;
        private String description;
        private int attendees = -1;
        private boolean isPublic;
        private String location;
        //Remove temp time strings
        private DateTime startTime;
        private DateTime endTime;
        private int organizerPK = -1;
        private int pictureID = -1;

        public Event build(){
            if(googlePlaceId == null ||
                    name == null ||
                    eventID == -1 ||
                    description == null ||
                    location == null ||
                    startTime == null ||
                    endTime == null ||
                    organizerPK == -1 ||
                    pictureID == -1){
                Log.e("EventBuilder::SEVERE", "One or more event specifications is null or unassigned!");
            }
            return new Event(name, description, attendees, isPublic, location, startTime, endTime, organizerPK, eventID, pictureID, googlePlaceId);
        }

        public Builder setGooglePlaceId(String googlePlaceId) {
            this.googlePlaceId = googlePlaceId;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setEventID(int eventID) {
            this.eventID = eventID;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setAttendees(int attendees) {
            this.attendees = attendees;
            return this;
        }

        public Builder setPublic(boolean aPublic) {
            isPublic = aPublic;
            return this;
        }

        public Builder setLocation(String location) {
            this.location = location;
            return this;
        }

        public Builder setStartTime(DateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder setEndTime(DateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder setOrganizerPK(int organizerPK) {
            this.organizerPK = organizerPK;
            return this;
        }

        public Builder setPictureID(int pictureID) {
            this.pictureID = pictureID;
            return this;
        }
    }

}
