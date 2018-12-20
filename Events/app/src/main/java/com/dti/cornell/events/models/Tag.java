package com.dti.cornell.events.models;

/**
 * Created by jboss925 on 11/3/18.
 */

public class Tag {

    public final int pk;
    public final String name;

    public Tag(Integer pk, String name){
        this.pk = pk;
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public Integer getID(){
        return pk;
    }

    public String toString(){
        return pk + ";" + name;
    }

    public static Tag fromString(String tag){
        String[] splits = tag.split(";");
        Integer pk = Integer.valueOf(splits[0]);
        String name = splits[1];
        return new Tag(pk, name);
    }

}
