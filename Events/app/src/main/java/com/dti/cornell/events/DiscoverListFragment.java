package com.dti.cornell.events;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dti.cornell.events.utils.EventUtils;

import java.util.List;

public class DiscoverListFragment extends Fragment
{
	private final List<Event> events = EventUtils.getEvents();

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_discover_list, container, false);
		RecyclerView recycler = view.findViewById(R.id.recyclerView);
		setUpRecycler(recycler);
		return view;
	}

	public void setUpRecycler(RecyclerView recycler)
	{
		EventAdapter adapter = new EventAdapter(getContext(), events);
		recycler.setAdapter(adapter);
	}
}
