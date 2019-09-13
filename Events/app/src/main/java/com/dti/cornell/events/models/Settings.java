package com.dti.cornell.events.models;

public class Settings {

    public String notifyMeTime;

    public Settings(String notifyMeTime){
        this.notifyMeTime = notifyMeTime;
    }

    @Override
    public String toString(){
        return notifyMeTime;
    }

    public static Settings fromString(String s){
        return new Settings(s);
    }

}
