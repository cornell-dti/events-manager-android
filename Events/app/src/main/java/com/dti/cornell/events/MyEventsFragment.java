package com.dti.cornell.events;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.DividerItemDecoration;

import java.util.List;


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
		List<Event> events = Data.events();
		recyclerView.setAdapter(new EventAdapter(getContext(), events));

		return view;
	}

}
