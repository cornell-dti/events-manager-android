package com.dti.cornell.events;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.DividerItemDecoration;
import com.dti.cornell.events.utils.EventBusUtils;
import com.google.common.eventbus.Subscribe;

import org.joda.time.DateTime;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyEventsFragment extends Fragment
{
	private RecyclerView recyclerView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_card_sections, container, false);

		int margin = getResources().getDimensionPixelSize(R.dimen.spacing_xxl);
		recyclerView = view.findViewById(R.id.recyclerView);
		recyclerView.addItemDecoration(new DividerItemDecoration(margin));
		recyclerView.setAdapter(new EventAdapter(getContext(), Data.events()));
		setOnScrollListener();

		return view;
	}

	private void setOnScrollListener()
	{
		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
		{
			@Override
			public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
			{
				int topItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
						.findFirstVisibleItemPosition();
				DateTime dateTime = ((EventAdapter) recyclerView.getAdapter())
						.getDateAtPosition(topItemPosition);

				Data.selectedDate = dateTime;
				EventBusUtils.SINGLETON.post(new EventBusUtils.DateChanged());
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
		//scroll to the new date
//		int position = UserData.DATES.indexOf(UserData.selectedDate);
//		recyclerView.scrollToPosition(position);
	}
}
