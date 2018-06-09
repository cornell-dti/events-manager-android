package com.dti.cornell.events;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dti.cornell.events.utils.EventUtils;

import java.util.List;

/**
 * Created by jaggerbrulato on 3/20/18.
 */

public class DiscoveryActivity extends AppCompatActivity
{
	private RecyclerView eventRecycler;
	private List<Event> events;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discover);

		findViews();
		events = EventUtils.getEvents();
		addEventsToRecycler();
	}

	private void findViews()
	{
		eventRecycler = findViewById(R.id.eventRecycler);
	}


	public void addEventsToRecycler()
	{
		EventAdapter adapter = new EventAdapter(this, events);
		eventRecycler.setLayoutManager(new LinearLayoutManager(this));
		eventRecycler.setAdapter(adapter);
	}
}
