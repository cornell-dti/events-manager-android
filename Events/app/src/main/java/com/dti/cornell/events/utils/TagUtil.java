package com.dti.cornell.events.utils;

import android.content.Context;
import android.preference.PreferenceManager;

import com.dti.cornell.events.models.Event;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jboss925 on 8/29/18.
 */

public class TagUtil {

    public List<Integer> tagsInterested;
    private boolean tagsLoaded = false;
    private List<Event> suggestableEvents = new ArrayList<>();


    public void addTagToList(Integer tagID){
        if(!tagsLoaded){
//            loadTags();
        }
        tagsInterested.add(tagID);
    }

    public List<Event> suggestEvents(){
        if(!tagsLoaded){
//            loadTags();
        }
        List<Event> suggestedEvents = new ArrayList<>();
        for (Integer i : tagsInterested){
            for (Event event : Data.events()){
                if (event.tagIDs.contains(i)){
                    suggestedEvents.add(event);
                }
            }
        }
        return suggestedEvents;
    }


    public void loadTags(Context context){
        String toBeDecoded = PreferenceManager.getDefaultSharedPreferences(context).getString("TAG_STRING", "");
        tagsInterested = decodeTagIDs(toBeDecoded);
        tagsLoaded = true;
    }

    public void saveTags(Context context){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("TAG_STRING", encodeTagIDs()).apply();
    }

    public String encodeTagIDs(){
        StringBuilder sb = new StringBuilder();
        boolean firstLoop = true;
        for(Integer i : tagsInterested){
            if(firstLoop){
                sb.append(i);
                sb.append(":");
            } else {
                sb.append(":");
                sb.append(i);
            }

        }
        return sb.toString();
    }

    public List<Integer> decodeTagIDs(String tagIDsString){
        String[] tagIDs = tagIDsString.split(tagIDsString);
        List<Integer> tagIDsInts = new ArrayList<>();
        for (String tagID : tagIDs){
            tagIDsInts.add(Integer.valueOf(tagID));
        }
        return tagIDsInts;
    }


}
