package com.dti.cornell.events;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dti.cornell.events.models.CardList;
import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.models.Organization;
import com.dti.cornell.events.utils.Comparators;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.EventBusUtils;
import com.dti.cornell.events.utils.EventUtil;
import com.dti.cornell.events.utils.RecyclerUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class DiscoverFragment extends Fragment implements Data.DataUpdateListener
{
	public static final String TAG = DiscoverFragment.class.getName();
	public RecyclerView recyclerView;
	private int mTotalScrolled;
	private Context createContext;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.fragment_recycler, container, false);

		Data.registerListener(this);

		this.createContext = this.getContext();

		this.recyclerView = view.findViewById(R.id.recyclerView);
		updateData();
		RecyclerUtil.addVerticalSpacing(recyclerView);

		setOnScrollListener();

		return view;
	}

	public void setOnScrollListener(){
		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
		{
			@Override
			public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
			{
				super.onScrolled(recyclerView, dx, dy);
				mTotalScrolled += dy;
				EventBusUtils.SINGLETON.post(new EventBusUtils.MainActivityScrolled(mTotalScrolled));
			}
		});
	}

	@Override
	public void onPause(){
		super.onPause();
		Data.unregisterListener(this);
	}

	@Override
	public void onResume(){
		super.onResume();
		Data.registerListener(this);
	}

	@Override
	public void onStop(){
		super.onStop();
		Data.unregisterListener(this);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.search, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.tab_search:
				SearchActivity.start(getContext());
				return true;
		}
		return false;
	}


	@Override
	public void eventUpdate(List<Event> e) {
		updateData();
	}

	@Override
	public void orgUpdate(List<Organization> o) {

	}

	@Override
	public void tagUpdate(List<String> t) {

	}

	public void updateData(){
		RecyclerView recyclerView = this.recyclerView;
		List<Event> events = EventUtil.getEventsOnOrAfterToday();
		List<Event> popularEvents = new ArrayList<>(events);
		popularEvents.sort(Comparators.NUM_ATTENDEES);
		Collections.reverse(popularEvents);
		List<CardList> data = Arrays.asList(new CardList(getString(R.string.section_popular), true, popularEvents),
				new CardList(getString(R.string.section_today_events), true,
						EventUtil.getEventsBetween(DateTime.now().withTimeAtStartOfDay(), DateTime.now().plusDays(1).withTimeAtStartOfDay())),
				new CardList(getString(R.string.section_tomorrow_events), true,
						EventUtil.getEventsBetween(DateTime.now().plusDays(1).withTimeAtStartOfDay(), DateTime.now().plusDays(2).withTimeAtStartOfDay()).stream().filter((val) -> !val.startTime.withTimeAtStartOfDay().isEqual(DateTime.now().withTimeAtStartOfDay())).collect(Collectors.toList())));
		recyclerView.setAdapter(new CardSectionAdapter(recyclerView.getContext(), data, true));
		this.recyclerView = recyclerView;
	}
}