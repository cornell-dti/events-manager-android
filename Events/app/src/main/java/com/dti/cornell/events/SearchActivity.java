package com.dti.cornell.events;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.TextView;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.EventBusUtils;
import com.dti.cornell.events.utils.RecyclerUtil;
import com.dti.cornell.events.utils.SearchUtil;
import com.google.common.eventbus.Subscribe;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by jboss925 on 9/3/18.
 */

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener{

    private static final String TAG = MyEventsFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private SearchView searchBar;
    private EventAdapter adapter;
    private TextView backgroundTextView;
    private ConstraintLayout backgound;
    private LinearLayoutManager layoutManager;
    private boolean noResults = false;

    public static void start(Context context)
    {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchBar = (SearchView) findViewById(R.id.searchView);
        searchBar.setOnQueryTextListener(this);
        backgound = findViewById(R.id.searchBackground);
        backgroundTextView = findViewById(R.id.searchBackgroundText);


        recyclerView = findViewById(R.id.recyclerView);
        //DEPRECATED: RecyclerUtil.configureEvents(recyclerView);
        setOnScrollListener();

    }

    //change selected date when scrolling through events
    private void setOnScrollListener()
    {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
            {
                int topItemPosition = layoutManager.findFirstVisibleItemPosition();
                DateTime dateTime = adapter.getDateAtPosition(topItemPosition);

                if (Data.equalsSelectedDate(dateTime))
                    return;

                Data.selectedDate = dateTime;
                EventBusUtils.SINGLETON.post(new EventBusUtils.DateChanged(TAG));
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit (String query){
        List<Event> events = SearchUtil.getEventsFromSearch(query);
        if(events.isEmpty()){
            noResults();
            noResults = true;
            return true;
        }
        adapter = new EventAdapter(this, events);
        recyclerView.setAdapter(adapter);
        layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        someResults();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public void noResults(){
        backgound.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        backgroundTextView.setText("No results found.");
    }

    public void someResults(){
        backgound.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    //events subscription

    @Override
    public void onStart()
    {
        super.onStart();
        EventBusUtils.SINGLETON.register(this);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        EventBusUtils.SINGLETON.unregister(this);
    }

    @Subscribe
    public void onDateChanged(EventBusUtils.DateChanged dateChanged)
    {
        if (dateChanged.sender.equals(TAG))
            return;

        //scroll to the new date
        int position = adapter.getPositionForDate(Data.selectedDate);
        layoutManager.scrollToPositionWithOffset(position, 0);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.back2){
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
