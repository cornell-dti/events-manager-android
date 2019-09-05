package com.dti.cornell.events;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.EventBusUtils;
import com.dti.cornell.events.utils.EventUtil;
import com.dti.cornell.events.utils.RecyclerUtil;
import com.google.common.eventbus.Subscribe;

import org.joda.time.DateTime;

import java.util.stream.Collectors;

public class MyEventsFragment extends Fragment
{
	private static final String TAG = MyEventsFragment.class.getSimpleName();
	private RecyclerView recyclerView;
	private EventAdapter adapter;
	private LinearLayoutManager layoutManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_recycler, container, false);

		recyclerView = view.findViewById(R.id.recyclerView);
		RecyclerUtil.addDivider(recyclerView);
		adapter = new EventAdapter(getContext(), Data.events().stream().filter(
				(val)->EventUtil.userHasBookmarked(val.id)).collect(Collectors.toList()));
		recyclerView.setAdapter(adapter);
		layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
		setOnScrollListener();

		return view;
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
}
