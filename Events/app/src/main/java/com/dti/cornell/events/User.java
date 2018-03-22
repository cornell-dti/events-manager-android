package com.dti.cornell.events;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by jaggerbrulato on 3/20/18.
 */

public class User {

    private Set<Event> interestedEvents = new HashSet<>();
    private Set<Event> goingEvents = new HashSet<>();
    private String netID;


    public User(String id){
        this.netID = id;
        getEventsForUser(id);
    }

    public void getEventsForUser(String id){
        //this hooks into the backend
    }



    public void addInterested(Event event){
        interestedEvents.add(event);
    }

    public void removeInterested(Event event){
        interestedEvents.remove(event);
    }

    public void addGoing(Event event){
        goingEvents.add(event);
    }

    public void removeGoing(Event event){
        goingEvents.remove(event);
    }

}
