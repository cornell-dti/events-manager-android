package com.dti.cornell.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;

import com.dti.cornell.events.R.layout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaggerbrulato on 3/20/18.
 */

public class DiscoveryActivity extends AppCompatActivity {

    private ListView eventListView;
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
        tabHost =  findViewById(R.id.tabhost);
    }

    private List<Event> getEvents() {
        //geteventsfromserver
        ArrayList<Event> returnEvents = new ArrayList<>();
        returnEvents.add(new Event("Cornell DTI Info Sessions", "hello there", 30, true, "Upson B02", "9:00am", "10:00pm"));
        returnEvents.add(new Event("Some Other Event idk", "hello there", 30, true, "Upson B00000003", "9:43am", "10:00pm"));
        returnEvents.add(new Event("Yo, another event? Interesting", "hello there", 30, true, "Rockefeller 6000", "2:00am", "10:00pm"));
        return returnEvents;
    }

    public void addEventsToListView(){
        EventAdapter adapter = new EventAdapter(this, this.events);
        this.eventListView.setAdapter(adapter);
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


}
