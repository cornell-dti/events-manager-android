package com.dti.cornell.events.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

}

//enum ATTENDANCE {
//    INTERESTED, GOING
//}
