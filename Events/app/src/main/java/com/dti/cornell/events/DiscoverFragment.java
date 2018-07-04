package com.dti.cornell.events;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dti.cornell.events.utils.EventUtils;

import java.util.List;

/**
 * Created by jaggerbrulato on 3/20/18.
 */

public class DiscoverFragment extends Fragment
{
	private final List<Event> events = EventUtils.getEvents();

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_discover, container, false);

		View popularEvents = view.findViewById(R.id.popularEvents);
		View todayEvents = view.findViewById(R.id.todayEvents);
		View tomorrowEvents = view.findViewById(R.id.tomorrowEvents);

		TextView popularSection = popularEvents.findViewById(R.id.section);
		TextView todaySection = todayEvents.findViewById(R.id.section);
		TextView tomorrowSection = tomorrowEvents.findViewById(R.id.section);

		popularSection.setText(R.string.section_popular);
		todaySection.setText(R.string.section_today_events);
		tomorrowSection.setText(R.string.section_tomorrow_events);

		RecyclerView popularRecycler = popularEvents.findViewById(R.id.recyclerView);
		RecyclerView todayRecycler = todayEvents.findViewById(R.id.recyclerView);
		RecyclerView tomorrowRecycler = tomorrowEvents.findViewById(R.id.recyclerView);

		addEventsToRecycler(popularRecycler);
		addEventsToRecycler(todayRecycler);
		addEventsToRecycler(tomorrowRecycler);
		return view;
	}

	public void addEventsToRecycler(RecyclerView recycler)
	{
		EventCardAdapter adapter = new EventCardAdapter(getContext(), events);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
		layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		recycler.setLayoutManager(layoutManager);
		recycler.setAdapter(adapter);
	}
}
