package com.dti.cornell.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.utils.RecyclerUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class EventListFragment extends Fragment
{

	List<Event> events;

	public EventListFragment(List<Event> initEvents){
		this.events = initEvents;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_recycler, container, false);

		RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
		RecyclerUtil.addDivider(recyclerView);
		recyclerView.setAdapter(new EventAdapter(recyclerView.getContext(), this.events));

		return view;
	}
}