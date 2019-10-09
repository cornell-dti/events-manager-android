package com.dti.cornell.events.models;

public class Settings {

    public String notifyMeTime;
    public boolean doSendNotifications;
    private static String SEPARATOR = ">>";

    public Settings(String notifyMeTime, boolean doSendNotifications){
        this.notifyMeTime = notifyMeTime;
        this.doSendNotifications = doSendNotifications;
    }

    @Override
    public String toString(){
        return notifyMeTime + SEPARATOR + doSendNotifications;
    }

    public static Settings fromString(String s){
        try{
            String[] splits = s.split(SEPARATOR);
            String nMeTime = splits[0];
            boolean dSNotifications = Boolean.valueOf(splits[1]);
            return new Settings(nMeTime, dSNotifications);
        } catch (ArrayIndexOutOfBoundsException e){
            return new Settings("15 Minutes Before", true);
        }
    }

    @Override
    public Settings clone(){
        return Settings.fromString(this.toString());
    }

}
