package com.dti.cornell.events.utils;

import com.dti.cornell.events.Event;
import com.dti.cornell.events.Tag;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jaggerbrulato on 5/1/18.
 */

public class EventUtils {

	public static final DateTimeFormatter timeFormat = DateTimeFormat.forPattern("HH:mm");

    public static void main(String[] args) {
        EventUtils.getEventsBetweenDates(getEvents(), new DateTime(), new DateTime());
    }


    public static List<Event> getEvents() {
        //TODO geteventsfromserver
        ArrayList<Event> returnEvents = new ArrayList<>();
        Event.Builder eventBuilder = new Event.Builder();
        eventBuilder.setTitle("CornellDTI Info Sessions")
                .setDescription("hello there")
                .setAttendees(30)
                .setPublic(true)
                .setLocation("Upson B02")
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

	public static List<Tag> getTags()
	{
		return Arrays.asList(new Tag(1, "#DTI>AppDev"),
				new Tag(2, "#DavidWroteThis"),
				new Tag(3, "#short"),
				new Tag(4, "#noFilter"));
	}


    public static List<Event> getEventsBetweenDates(List<Event> eventList, DateTime date1, DateTime date2){
        List<Event> returnEvents = new ArrayList<>();

        for(Event e : eventList){
            DateTime eventDateTime = e.startTime;
            if(date1.getMillis() <= eventDateTime.getMillis()
                    && date2.getMillis() >= eventDateTime.getMillis()){
                returnEvents.add(e);
            }
        }

        return returnEvents;

    }

    public static String getDateTimeStringHHmm(DateTime dateTime){
        return dateTime.toString(EventUtils.timeFormat);
    }
}
