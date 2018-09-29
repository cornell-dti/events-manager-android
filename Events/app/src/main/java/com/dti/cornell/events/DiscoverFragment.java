package com.dti.cornell.events;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dti.cornell.events.models.CardList;
import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.EventBusUtils;
import com.dti.cornell.events.utils.RecyclerUtil;
import com.google.common.eventbus.EventBus;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.List;

public class DiscoverFragment extends Fragment
{
	public static final String TAG = DiscoverFragment.class.getName();
	public RecyclerView recyclerView;
	private int mTotalScrolled;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.fragment_recycler, container, false);

		RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
		List<Event> events = Data.events();
		List<CardList> data = Arrays.asList(new CardList(R.string.section_popular, true, events),
				new CardList(R.string.section_today_events, true, events),
				new CardList(R.string.section_tomorrow_events, true, events));
		RecyclerUtil.addVerticalSpacing(recyclerView);
		recyclerView.setAdapter(new CardSectionAdapter(getContext(), data, true));

		this.recyclerView = recyclerView;

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


}