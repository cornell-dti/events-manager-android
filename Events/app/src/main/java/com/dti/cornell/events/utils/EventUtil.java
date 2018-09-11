package com.dti.cornell.events.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jboss925 on 9/10/18.
 */

public class EventUtil {

    public static List<Integer> allAttendanceEvents = new ArrayList<>();
    public static List<Integer> interestedEvents = new ArrayList<>();
    public static List<Integer> goingEvents = new ArrayList<>();
    public static boolean attendanceLoaded = false;


    public static void setInterested(Integer eventID){
        if(!attendanceLoaded){
            Log.e("ATTENDANCE", "Attendance has not yet been loaded! You can't remove an event from the list unless" +
                    "the attendance is loaded!");
            //Needs context to load. Must call loadAttendance() from activity.
            return;
        }
        if(interestedEvents.contains(eventID)){
            return;
        }
        interestedEvents.add(eventID);
    }

    public static void setNotInterested(Integer eventID){
        if(!attendanceLoaded){
            Log.e("ATTENDANCE", "Attendance has not yet been loaded! You can't remove an event from the list unless" +
                    "the attendance is loaded!");
            //Needs context to load. Must call loadAttendance() from activity.
            return;
        }
        if(interestedEvents.contains(eventID)){
            interestedEvents.remove(eventID);
        }
    }

    public static void setGoing(Integer eventID){
        if(!attendanceLoaded){
            Log.e("ATTENDANCE", "Attendance has not yet been loaded! You can't remove an event from the list unless" +
                    "the attendance is loaded!");
            //Needs context to load. Must call loadAttendance() from activity.
            return;
        }
        if(goingEvents.contains(eventID)){
            return;
        }
        goingEvents.add(eventID);
    }


    public static void setNotGoing(Integer eventID){
        if(!attendanceLoaded){
            Log.e("ATTENDANCE", "Attendance has not yet been loaded! You can't remove an event from the list unless" +
                    "the attendance is loaded!");
            //Needs context to load. Must call loadAttendance() from activity.
            return;
        }
        if(goingEvents.contains(eventID)){
            goingEvents.remove(eventID);
        }
    }


    public static String encodeEventIDs(){
        StringBuilder sb = new StringBuilder();
        boolean firstLoop = true;
        for(Integer i : allAttendanceEvents){
            if(firstLoop){
                sb.append(i);
                sb.append(";");
                if(interestedEvents.contains(i)){
                    sb.append("t");
                } else {
                    sb.append("f");
                }
                if(goingEvents.contains(i)){
                    sb.append("t");
                } else {
                    sb.append("f");
                }
                firstLoop = false;
            } else {
                sb.append("::");
                sb.append(i);
                sb.append(";");
                if(interestedEvents.contains(i)){
                    sb.append("t");
                } else {
                    sb.append("f");
                }
                if(goingEvents.contains(i)){
                    sb.append("t");
                } else {
                    sb.append("f");
                }
            }

        }
        return sb.toString();
    }

    public static List<Integer> decodeEventIDs(String eventIDsString, ATTENDANCE attendance){
        if(eventIDsString.isEmpty()){
            return new ArrayList<>();
        }
        String[] eventIDStrings = eventIDsString.split("::");
        List<Integer> eventIDs = new ArrayList<>();
        for(String s : eventIDStrings){
            if(attendance == ATTENDANCE.INTERESTED){
                String[] splits = s.split(";");
                String encodedTF = splits[1];
                if(encodedTF.startsWith("t")){
                    eventIDs.add(Integer.valueOf(splits[0]));
                    Log.e("EVENT LOADED", splits[0]);
                }
            }
            if(attendance == ATTENDANCE.GOING){
                String[] splits = s.split(";");
                String encodedTF = splits[1];
                if(encodedTF.endsWith("t")){
                    eventIDs.add(Integer.valueOf(splits[0]));
                    Log.e("EVENT LOADED", splits[0]);
                }
            }
        }
        return eventIDs;
    }

    public static boolean userIsInterested(Integer eventID){
        if(interestedEvents.contains(eventID)){
            return true;
        }
        return false;
    }

    public static boolean userIsGoing(Integer eventID){
        if(goingEvents.contains(eventID)){
            return true;
        }
        return false;
    }

}

enum ATTENDANCE {
    INTERESTED, GOING
}
