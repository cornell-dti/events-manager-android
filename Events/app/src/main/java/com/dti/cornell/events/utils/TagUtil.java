package com.dti.cornell.events.utils;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dti.cornell.events.models.Event;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;

import java.io.NotActiveException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jboss925 on 8/29/18.
 */

public class TagUtil {

    public static List<Integer> tagsInterested;
    public static Map<Integer, Integer> tagIDsAndImportance;
    public static boolean tagsLoaded = false;
    private List<Event> suggestableEvents = new ArrayList<>();


    public static void addTagToList(Integer tagID){
        if(!tagsLoaded){
            Log.e("TAGS", "Tags have not yet been loaded! You can't add a tag to the list unless" +
                    "the tags are loaded!");
            //Needs context to load. Must call loadTags() from activity.
            return;
        }
        if(tagsInterested.contains(tagID)){
            return;
        }
        tagsInterested.add(tagID);
        if(tagIDsAndImportance.containsKey(tagID)){
            tagIDsAndImportance.put(tagID, tagIDsAndImportance.get(tagID) + 1);
        } else {
            tagIDsAndImportance.put(tagID, 1);
        }
        Log.e("DEBUG TAGUTIL", tagIDsAndImportance + "");
    }

    public static void resetTags(){
        tagsInterested = new ArrayList<>();
        tagIDsAndImportance = new HashMap<>();
    }

    public static List<Event> suggestEventsForTagID(int tagID){
        if(!tagsLoaded){
            Log.e("TAGS","Tags have not yet been loaded! You can't ask for events unless" +
                    "the tags are loaded!");
//            loadTags();
            //Needs context to load. Must call loadTags() from activity.
            return new ArrayList<>();
        }

        //Check events for tagID, add them.
        List<Integer> suggestedEvents = new ArrayList<>();
        for (Event event : Data.events()){
            if (event.tagIDs.contains(tagID)){
                suggestedEvents.add(event.id);
            }
        }

        // Establish a list of IDAndFrequency Objects to be sorted by frequency later
        // Establish a Hashmap of EventID, Frequency to track events and how many user-suggested
        // tags they contain.
        List<IDAndFrequency> frequencyOfEventIDs = new ArrayList<>();
        HashMap<Integer, Integer> IDsAddedToFrequencyOfEventIDs = new HashMap<>();

        // For each tag that isn't the one initially provided, find events in the suggestedEvents
        // list that also contain the new tag. If it contains that tag, add 1 to it's frequency.

        // This takes the list of events that match the primary tagID, and then checks them to
        // see if they contain any more tagIDs that the user likes.
        for(Integer otherTagID : tagsInterested){
            for(Integer eventID : suggestedEvents){
                if(Data.getEventFromID(eventID).tagIDs.contains(otherTagID)){
                    if(IDsAddedToFrequencyOfEventIDs.containsKey(eventID)){
                        IDsAddedToFrequencyOfEventIDs.put(eventID, IDsAddedToFrequencyOfEventIDs.get(eventID)+1);
                    } else{
                        IDsAddedToFrequencyOfEventIDs.put(eventID, 1);
                    }
                }
            }
        }

        // Add IDAndFrequency Objects to a List to be sorted
        for(Integer eventID : IDsAddedToFrequencyOfEventIDs.keySet()){
            frequencyOfEventIDs.add(new IDAndFrequency(eventID, IDsAddedToFrequencyOfEventIDs.get(eventID)));
        }

        // Sort the events by frequency of user-liked tags.
        Collections.sort(frequencyOfEventIDs, Comparators.FREQUENCY);

        //Get the events from each eventID
        List<Event> returnEvents = new ArrayList<>();
        for(IDAndFrequency idf : frequencyOfEventIDs){
            returnEvents.add(Data.getEventFromID(idf.ID));
            Log.e("SUGGESTED EVENT", Data.getEventFromID(idf.ID) + "::" + idf.frequency);
        }


        return returnEvents;
    }

    public static String encodeTagIDs(){
        StringBuilder sb = new StringBuilder();
        boolean firstLoop = true;
        for(Integer i : tagIDsAndImportance.keySet()){
            if(firstLoop){
                sb.append(i);
                sb.append(";");
                sb.append(String.valueOf(tagIDsAndImportance.get(i)));
                firstLoop = false;
            } else {
                sb.append("::");
                sb.append(i);
                sb.append(";");
                sb.append(String.valueOf(tagIDsAndImportance.get(i)));
            }

        }
        Log.e("DEBUG TAGUTIL", sb.toString());
        return sb.toString();
    }

    public static Map<Integer, Integer> decodeTagIDs(String tagIDsString){
        if(tagIDsString.isEmpty() || !tagIDsString.contains("::")){
            return new HashMap<>();
        }
        String[] tagIDs = tagIDsString.split("::");
        HashMap<Integer, Integer> tagIDsInts = new HashMap<>();
        for (String tagIDAndImportance : tagIDs){
            String[] breakup = tagIDAndImportance.split(";");
            String tagID = breakup[0];
            String importance = breakup[1];
            tagIDsInts.put(Integer.valueOf(tagID), Integer.valueOf(importance));
            // WILL CAUSE NPE tagsInterested.add(Integer.valueOf(tagID));
        }
        return tagIDsInts;
    }

    public static List<Integer> getMostPopularTags(int numberOfTagsDesired){
        if(tagsInterested.size() == 0){
            return new ArrayList<>();
        }
        List<Integer> tagIDs = new ArrayList<>();
        for(int i = 0; i < numberOfTagsDesired; i++){
            tagIDs.add(tagsInterested.get(i));
        }
        return tagIDs;
    }

    public static class IDAndFrequency implements Comparable<IDAndFrequency>{

        public Integer ID;
        public Integer frequency;

        public IDAndFrequency(Integer ID, Integer frequency){
            this.frequency = frequency;
            this.ID = ID;
        }

        @Override
        public String toString(){
            return String.valueOf(ID) + ";" + String.valueOf(frequency);
        }

        public IDAndFrequency fromString(String encoded){
            String[] splits = encoded.split(";");
            return new IDAndFrequency(Integer.valueOf(splits[0]), Integer.valueOf(splits[1]));
        }

        @Override
        public int compareTo(IDAndFrequency o) {
            return Comparators.FREQUENCY.compare(this, o);
        }

    }


}
