package com.dti.cornell.events.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jboss925 on 9/10/18.
 */

public class OrganizationUtil {

    public static List<Integer> followedOrganizations = new ArrayList<>();
    public static boolean organizationsLoaded = false;


    public static void followOrganization(Integer orgID){
        if(!organizationsLoaded){
            Log.e("ORGS", "Orgs have not yet been loaded! You can't add an org to the list unless" +
                    "the orgs are loaded!");
            //Needs context to load. Must call loadOrganizations() from activity.
            return;
        }
        if(followedOrganizations.contains(orgID)){
            return;
        }
        followedOrganizations.add(orgID);
    }


    public static void unfollowOrganization(Integer orgID){
        if(!organizationsLoaded){
            Log.e("ORGS", "Orgs have not yet been loaded! You can't remove an org from the list unless" +
                    "the orgs are loaded!");
            //Needs context to load. Must call loadOrganizations() from activity.
            return;
        }
        if(followedOrganizations.contains(orgID)){
            followedOrganizations.remove(orgID);
        }
    }


    public static String encodeOrganizationIDs(){
        StringBuilder sb = new StringBuilder();
        boolean firstLoop = true;
        for(Integer i : followedOrganizations){
            if(firstLoop){
                sb.append(i);
                firstLoop = false;
            } else {
                sb.append("::");
                sb.append(i);
            }

        }
        return sb.toString();
    }

    public static List<Integer> decodeTagIDs(String organizationIDsString){
        if(organizationIDsString.isEmpty()){
            return new ArrayList<>();
        }
        String[] organizationIDStrings = organizationIDsString.split("::");
        List<Integer> organizationIDs = new ArrayList<>();
        for(String s : organizationIDStrings){
            organizationIDs.add(Integer.valueOf(s));
        }
        return organizationIDs;
    }

    public static boolean userIsFollowing(Integer orgID){
        if(followedOrganizations.contains(orgID)){
            return true;
        }
        return false;
    }

}
