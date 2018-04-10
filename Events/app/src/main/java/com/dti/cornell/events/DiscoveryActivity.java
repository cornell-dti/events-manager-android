package com.dti.cornell.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.dti.cornell.events.R.layout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaggerbrulato on 3/20/18.
 */

public class DiscoveryActivity extends AppCompatActivity implements TabHost.OnTabChangeListener{

    private ListView eventListView;
    private ListView eventListView2;
    private List<Event> events = new ArrayList<>();
    TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_discover);
        findViews();
        events = getEvents();
        addEventsToListView();
        setUpTabHost();
    }

    private void findViews(){
        eventListView = findViewById(R.id.event_list);
        eventListView2 = findViewById(R.id.event_list_2);
        tabHost =  findViewById(R.id.tabhost);
    }

    private List<Event> getEvents() {
        //TODO geteventsfromserver
        ArrayList<Event> returnEvents = new ArrayList<>();
        Event.Builder eventBuilder = new Event.Builder();
        eventBuilder.setName("CornellDTI Info Sessions")
                .setDescription("hello there")
                .setAttendees(30)
                .setPublic(true)
                .setLocation("UpsonB02")
                .setStartTime("9:00am")
                .setEndTime("10:00pm")
                .setOrganizerPK(123456)
                .setEventID(348289)
                .setPictureID(359826359)
                .setGooglePlaceId("google place id");
        for(int i = 0; i < 5; i++){
            returnEvents.add(eventBuilder.build());
        }
        return returnEvents;
    }

    public void addEventsToListView(){
        EventAdapter adapter = new EventAdapter(this, this.events);
        this.eventListView.setAdapter(adapter);
        this.eventListView2.setAdapter(adapter);
    }

    public void setUpTabHost(){
        tabHost.setup();
        TabHost.TabSpec tab1 = tabHost.newTabSpec("tab1");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("tab2");
        tab1.setIndicator("Today");
        tab1.setContent(R.id.tab1);
        tab2.setIndicator("Upcoming");
        tab2.setContent(R.id.tab2);
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
    }

    @Override
    public void onTabChanged(String s) {

    }
}
