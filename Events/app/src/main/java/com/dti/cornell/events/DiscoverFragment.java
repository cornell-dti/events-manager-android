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

import com.dti.cornell.events.utils.EventUtils;

import java.util.List;

/**
 * Created by jaggerbrulato on 3/20/18.
 */

public class DiscoverFragment extends Fragment
{
	private RecyclerView eventRecycler;
	private List<Event> events;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_discover, container, false);

		eventRecycler = view.findViewById(R.id.eventRecycler);
		events = EventUtils.getEvents();
		addEventsToRecycler();
		return view;
	}

	public void addEventsToRecycler()
	{
		EventAdapter adapter = new EventAdapter(getContext(), events);
		eventRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
		eventRecycler.setAdapter(adapter);
	}
}
