package com.dti.cornell.events;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by jaggerbrulato on 3/20/18.
 */

public class User {

    public ArrayList<Event> interestedEvents = new ArrayList<>();
    public ArrayList<Event> goingEvents = new ArrayList<>();
    public UUID userID;


    public User(UUID id){
        this.userID = id;
        getEventsForUser(id);
    }

    public void getEventsForUser(UUID id){
        //this hooks into the backend
    }

}
