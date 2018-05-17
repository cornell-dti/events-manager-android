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
import com.dti.cornell.events.Utils.EventUtils;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaggerbrulato on 3/20/18.
 */

public class DiscoveryActivity extends AppCompatActivity{

    private ListView eventListView;
    private List<Event> events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_discover);
        findViews();
        events = EventUtils.getEvents();
        addEventsToListView();
    }

    private void findViews(){
        eventListView = findViewById(R.id.event_list);
    }



    public void addEventsToListView(){
        EventAdapter adapter = new EventAdapter(this, this.events);
        this.eventListView.setAdapter(adapter);
    }
}
