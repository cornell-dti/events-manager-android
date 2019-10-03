package com.dti.cornell.events.utils;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.models.Location;
import com.dti.cornell.events.models.Organization;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jboss925 on 9/3/18.
 */

public class SearchUtil {

    public static List<Event> getEventsFromSearch(String search, DateTime date){
        List<Event> returnList = new ArrayList<>();
        for(Event e : Data.events()){
            if(eventIsRelatedToSearch(e, search) && eventIsAfterDate(e, date)){
                returnList.add(e);
            }
        }
        return returnList;
    }

    public static List<Organization> getOrgsFromSearch(String search){
        List<Organization> returnList = new ArrayList<>();
        for(Organization org : Data.organizations()){
            if(organizationIsRelatedToSearch(org, search)){
                returnList.add(org);
            }
        }
        return returnList;
    }

    public static List<Integer> getTagsFromSearch(String search){
        List<Integer> returnList = new ArrayList<>();
        for(int tagID : Data.tagForID.keySet()){
            if(tagIsRelatedToSearch(tagID, search)){
                returnList.add(tagID);
            }
        }
        return returnList;
    }

    private static boolean tagIsRelatedToSearch(Integer tagID, String search){
        String searchL = search.toLowerCase();
        String[] searchSplits = searchL.split("\\s+");
        for(String searchLC : searchSplits){
            if(Data.tagForID.get(tagID).toLowerCase().replaceAll("’","'").contains(searchLC)){
                return true;
            }
        }
        return false;
    }


    private static boolean organizationIsRelatedToSearch(Organization org, String search){
        String searchL = search.toLowerCase();
        String[] searchSplits = searchL.split("\\s+");
        for(String searchLC : searchSplits){
            if(org.name.toLowerCase().replaceAll("’","'").contains(searchLC)
                    || org.description.toLowerCase().replaceAll("’","'").contains(searchLC)
                    || org.email.toLowerCase().replaceAll("’","'").contains(searchLC)
                    || org.website.toLowerCase().replaceAll("’","'").contains(searchLC)){
                return true;
            }
            for(Integer tagID : org.tagIDs){
                if(Data.tagForID.get(tagID).toLowerCase().replaceAll("’","'").contains(searchLC)){
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean eventIsRelatedToSearch(Event e, String search){
        String searchL = search.toLowerCase();
        String[] searchSplits = searchL.split("\\s+");
        Location loc = Data.locationForID.get(e.locationID);
        for(String searchLC : searchSplits){
            if(cleanString(e.title).contains(searchLC)
                    || cleanString(e.description).contains(searchLC)
                    || cleanString(loc.room).contains(searchLC)
                    || cleanString(loc.building).contains(searchLC)
                    || cleanString(Data.organizationForID.get(e.organizerID).name).contains(searchLC)){
                return true;
            }
            for(Integer tagID : e.tagIDs){
                if(cleanString(Data.tagForID.get(tagID)).contains(searchLC)){
                    return true;
                }
            }
        }
        return false;
    }

    public static String cleanString(String input){
        String output = input.toLowerCase();
        output = output.replaceAll("’","'");
        output.trim();
        return output;
    }

    public static boolean eventIsAfterDate(Event e, DateTime date){
        if (e.startTime.isAfter(date)) {
            return true;
        }
        return false;
    }

}
