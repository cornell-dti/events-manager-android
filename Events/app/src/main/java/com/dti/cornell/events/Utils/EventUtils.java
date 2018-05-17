package com.dti.cornell.events.Utils;

import com.dti.cornell.events.DiscoveryActivity;
import com.dti.cornell.events.Event;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaggerbrulato on 5/1/18.
 */

public class EventUtils {

    public static final String timePattern = "HH:mm";

    public static void main(String[] args) {
        EventUtils.getEventsBetweenDates(getEvents(), new DateTime(), new DateTime());
    }


    public static List<Event> getEvents() {
        //TODO geteventsfromserver
        ArrayList<Event> returnEvents = new ArrayList<>();
        Event.Builder eventBuilder = new Event.Builder();
        eventBuilder.setName("CornellDTI Info Sessions")
                .setDescription("hello there")
                .setAttendees(30)
                .setPublic(true)
                .setLocation("UpsonB02")
                .setStartTime(new DateTime())
                .setEndTime(new DateTime())
                .setOrganizerPK(123456)
                .setEventID(348289)
                .setPictureID(359826359)
                .setGooglePlaceId("google place id");
        for(int i = 0; i < 10; i++){
            returnEvents.add(eventBuilder.build());
        }
        return returnEvents;
    }

    public static List<Event> getEventsBetweenDates(List<Event> eventList, DateTime date1, DateTime date2){
        List<Event> returnEvents = new ArrayList<>();

        for(Event event : eventList){
            System.out.println(event.startTime.monthOfYear());
        }

        return returnEvents;

    }




}
