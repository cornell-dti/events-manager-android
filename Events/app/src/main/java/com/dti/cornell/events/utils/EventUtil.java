package com.dti.cornell.events.utils;

import android.util.Log;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.models.Location;
import com.dti.cornell.events.models.Organization;
import com.google.common.collect.ImmutableList;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by jboss925 on 9/10/18.
 */

public class EventUtil {

    public static List<Integer> allAttendanceEvents = new ArrayList<>();
//    public static List<Integer> interestedEvents = new ArrayList<>();
//    public static List<Integer> goingEvents = new ArrayList<>();
    public static boolean attendanceLoaded = false;


    public static void setBookmarked(Integer eventID){
        if(!attendanceLoaded){
            Log.e("ATTENDANCE", "Attendance has not yet been loaded! You can't remove an event from the list unless" +
                    "the attendance is loaded!");
            //Needs context to load. Must call loadAttendance() from activity.
            return;
        }
        if(!allAttendanceEvents.contains(eventID)){
            allAttendanceEvents.add(eventID);
        }
    }

    public static void setNotBookmarked(Integer eventID){
        if(!attendanceLoaded){
            Log.e("ATTENDANCE", "Attendance has not yet been loaded! You can't remove an event from the list unless" +
                    "the attendance is loaded!");
            //Needs context to load. Must call loadAttendance() from activity.
            return;
        }
        if(allAttendanceEvents.contains(eventID)){
            allAttendanceEvents.remove(eventID);
        }
    }
//
//    public static void setGoing(Integer eventID){
//        if(!attendanceLoaded){
//            Log.e("ATTENDANCE", "Attendance has not yet been loaded! You can't remove an event from the list unless" +
//                    "the attendance is loaded!");
//            //Needs context to load. Must call loadAttendance() from activity.
//            return;
//        }
//        if(!goingEvents.contains(eventID)){
//            goingEvents.add(eventID);
//            if(!allAttendanceEvents.contains(eventID)) allAttendanceEvents.add(eventID);
//        }
//    }
//
//
//    public static void setNotGoing(Integer eventID){
//        if(!attendanceLoaded){
//            Log.e("ATTENDANCE", "Attendance has not yet been loaded! You can't remove an event from the list unless" +
//                    "the attendance is loaded!");
//            //Needs context to load. Must call loadAttendance() from activity.
//            return;
//        }
//        if(goingEvents.contains(eventID)){
//            goingEvents.remove(eventID);
//            if(!interestedEvents.contains(eventID)) allAttendanceEvents.remove(eventID);
//        }
//    }




    public static String encodeEventIDs(){
        StringBuilder sb = new StringBuilder();
        boolean firstLoop = true;
        for(Integer i : allAttendanceEvents){
            if(firstLoop){
                sb.append(i);
                sb.append(";");
                if(allAttendanceEvents.contains(i)){
                    sb.append("t");
                } else {
                    sb.append("f");
                }
            } else {
                sb.append("::");
                sb.append(i);
                sb.append(";");
                if(allAttendanceEvents.contains(i)){
                    sb.append("t");
                } else {
                    sb.append("f");
                }
            }
            firstLoop = false;
        }
        Log.e("ENCODED", sb.toString());
        return sb.toString();
    }

    public static List<Integer> decodeEventIDs(String eventIDsString){
        Log.e("EVENT UTIL", eventIDsString);
        if(eventIDsString.isEmpty()){
            return new ArrayList<>();
        }
        String[] eventIDStrings = eventIDsString.split("::");
        List<Integer> eventIDs = new ArrayList<>();
        for(String s : eventIDStrings){
//            if(attendance == ATTENDANCE.INTERESTED){
                String[] splits = s.split(";");
                String encodedTF = splits[1];
                if(encodedTF.equalsIgnoreCase("t")){
                    eventIDs.add(Integer.valueOf(splits[0]));
                    Log.e("EVENT LOADED", splits[0]);
                }
//            }
//            if(attendance == ATTENDANCE.GOING){
//                String[] splits = s.split(";");
//                String encodedTF = splits[1];
//                if(encodedTF.endsWith("t")){
//                    eventIDs.add(Integer.valueOf(splits[0]));
//                    Log.e("EVENT LOADED", splits[0]);
//                }
//            }
        }
        return eventIDs;
    }

    public static boolean userHasBookmarked(Integer eventID){
        if(allAttendanceEvents.contains(eventID)){
            return true;
        }
        return false;
    }

//    public static boolean userIsGoing(Integer eventID){
//        if(allAttendanceEvents.contains(eventID)){
//            return true;
//        }
//        return false;
//    }

    // Removes duplicates from allAttendanceEvents
    public static void removeDuplicates(){
        Set<Integer> set = new LinkedHashSet<>(allAttendanceEvents);
        allAttendanceEvents = new ArrayList<>(set);
    }

    public static Event eventFromJSON(JSONObject eventJSON){
        try {
            int id = eventJSON.getInt("pk");

            String startDate = eventJSON.getString("start_date");
            List<Integer> startDateSplits = new ArrayList<>();
            for(String split : startDate.split("-")){
                startDateSplits.add(Integer.valueOf(split));
            }

            String startTime = eventJSON.getString("start_time");
            List<Integer> startTimeSplits = new ArrayList<>();
            for(String split : startTime.split(":")){
                startTimeSplits.add(Integer.valueOf(split));
            }

            DateTime startDateTime = new DateTime(startDateSplits.get(0), startDateSplits.get(1),
                    startDateSplits.get(2), startTimeSplits.get(0), startTimeSplits.get(1),
                    startDateSplits.get(2));

//            System.out.println(startDateTime);

            String endDate = eventJSON.getString("end_date");
            List<Integer> endDateSplits = new ArrayList<>();
            for(String split : endDate.split("-")){
                endDateSplits.add(Integer.valueOf(split));
            }

            String endTime = eventJSON.getString("end_time");
            List<Integer> endTimeSplits = new ArrayList<>();
            for(String split : endTime.split(":")){
                endTimeSplits.add(Integer.valueOf(split));
            }

            DateTime endDateTime = new DateTime(endDateSplits.get(0), endDateSplits.get(1),
                    endDateSplits.get(2), endTimeSplits.get(0), endTimeSplits.get(1),
                    endTimeSplits.get(2));

//            System.out.println(endDateTime);

            String name = eventJSON.getString("name");
            String description = eventJSON.getString("description");
            if(description.isEmpty()){
                description = "No description.";
            }
            int numAttendees = eventJSON.getInt("num_attendees");
            boolean isPublic = eventJSON.getBoolean("is_public");
            int organizerID = eventJSON.getJSONObject("organizer").getInt("owner");

            // SET THE ORGANIZER

            JSONObject orgJSON = eventJSON.getJSONObject("organizer");
            int orgID = orgJSON.getInt("owner");
            String orgName = orgJSON.getString("name");
            String orgDescription = orgJSON.getString("bio");
            if(orgDescription.isEmpty()){
                orgDescription = "No description.";
            }
            String orgWebsite = orgJSON.getString("website");
            if(orgWebsite.isEmpty()){
                orgWebsite = "No website.";
            }
            JSONArray orgPhotos = orgJSON.getJSONArray("photo");
            int orgPhotoID;
            if(orgPhotos.length() > 0){
                orgPhotoID = orgPhotos.getInt(0);
            } else {
                orgPhotoID = -1;
            }
            JSONArray orgTagsJSON = orgJSON.getJSONArray("tags");

//            String orgEmail = orgJSON.getString("email");

            List<Integer> orgTagIDs = new ArrayList<>();

            // REGISTER THE TAGS
            for(int i = 0; i < orgTagsJSON.length(); i++){
                JSONObject tagObject = orgTagsJSON.getJSONObject(i);
                int tagID = tagObject.getInt("id");
                String tagName = tagObject.getString("name");
                Data.tagForID.put(tagID, tagName);
                orgTagIDs.add(tagID);
            }
            // END REGISTERING THE TAGS

            Organization org = new Organization(orgID, orgName, orgDescription, orgPhotoID,
                    ImmutableList.<Integer>of(), ImmutableList.<String>of(), ImmutableList.copyOf(orgTagIDs), orgWebsite, "Loading...");
            Data.organizationForID.put(orgID, org);

            // END SETTING THE ORGANIZER

            List<Integer> tagIDs = new ArrayList<>();
            JSONArray tagJSONArray = eventJSON.getJSONArray("tags");
            if(tagJSONArray.length() > 0){
                for(int i = 0; i < tagJSONArray.length(); i++){
                    JSONObject tagJSONObj = tagJSONArray.getJSONObject(i);
                    tagIDs.add(tagJSONObj.getInt("id"));
                    int tagID = tagJSONObj.getInt("id");
                    String tagName = tagJSONObj.getString("name");
                    Data.tagForID.put(tagID, tagName);
                }
            }

            // REGISTER THE LOCATION

            JSONObject locationJSON = eventJSON.getJSONObject("location");
            int locID = locationJSON.getInt("id");
            String locRoom = locationJSON.getString("room");
            String locBuilding = locationJSON.getString("building");
            if(locBuilding.isEmpty()){
                locBuilding = "No location.";
            }
            String locPlaceID = locationJSON.getString("place_id");

            Location locationObj = new Location(locID, locRoom, locBuilding, locPlaceID);
            Data.locationForID.put(locID, locationObj);

            // END REGISTERING THE LOCATION

            String location = eventJSON.getJSONObject("location").getString("building");
            String googlePlaceID = eventJSON.getJSONObject("location").getString("place_id");
            String pictureID;
            if(eventJSON.getJSONArray("media").length() > 0){
                pictureID = eventJSON.getJSONArray("media").getJSONObject(0).getString("link");
                Log.e("EVENTUTIL", pictureID);
//                pictureID = "https://i." + pictureID.split("amazonaws.com/")[1].split("https://")[1] + "/image.png";
            } else {
                pictureID = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/1024px-No_image_available.svg.png";
            }

            Event event = new Event(id, startDateTime, endDateTime, name, description, location,
                    googlePlaceID, ImmutableList.copyOf(new ArrayList<String>()), pictureID, organizerID, ImmutableList.copyOf(tagIDs));

            return event;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}

//enum ATTENDANCE {
//    INTERESTED, GOING
//}
