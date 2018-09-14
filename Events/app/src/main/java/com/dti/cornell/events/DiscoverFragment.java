package com.dti.cornell.events;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dti.cornell.events.models.CardList;
import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.RecyclerUtil;

import java.util.Arrays;
import java.util.List;

public class DiscoverFragment extends Fragment
{
	public static final String TAG = DiscoverFragment.class.getName();
	private RecyclerView recyclerView;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_card_sections, container, false);

		recyclerView = view.findViewById(R.id.recyclerView);
		List<Event> events = Data.events();
		List<CardList> data = Arrays.asList(new CardList(R.string.section_popular, true, events),
				new CardList(R.string.section_today_events, true, events),
				new CardList(R.string.section_tomorrow_events, true, events));
		RecyclerUtil.addVerticalSpacing(recyclerView);
		recyclerView.setAdapter(new CardSectionAdapter(getContext(), data, true));

		return view;
	}
}