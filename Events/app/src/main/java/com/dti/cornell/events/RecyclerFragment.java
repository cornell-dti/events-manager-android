package com.dti.cornell.events;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dti.cornell.events.models.CardList;
import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.SpacingItemDecoration;

import java.util.Arrays;
import java.util.List;


/**
 * Created by jaggerbrulato on 3/20/18.
 */

public class RecyclerFragment extends Fragment
{
	private Type type;
	private RecyclerView recyclerView;

	enum Type
	{
		DiscoverCard, ForYouCard, DiscoverList
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_card_sections, container, false);

		recyclerView = view.findViewById(R.id.recyclerView);
		int cardMargin = getResources().getDimensionPixelSize(R.dimen.spacing_xxl);
		recyclerView.addItemDecoration(new SpacingItemDecoration(0, cardMargin));
		refreshViews();

		return view;
	}

	public void setType(Type type)
	{
		this.type = type;
	}

	public void refreshViews()
	{
		switch (type)
		{
			case DiscoverCard:
				setDiscoverCards();
				break;
			case DiscoverList:
				setDiscoverList();
				break;
			case ForYouCard:
				setForYouCards();
				break;
		}
	}

	private void setDiscoverCards()
	{
		List<Event> events = Data.events();
		List<CardList> data = Arrays.asList(new CardList(R.string.section_popular, true, events),
				new CardList(R.string.section_today_events, true, events),
				new CardList(R.string.section_tomorrow_events, true, events));
		recyclerView.setAdapter(new CardSectionAdapter(getContext(), data, true));
	}

	private void setDiscoverList()
	{
		List<Event> events = Data.events();
		recyclerView.setAdapter(new EventAdapter(getContext(), events));
	}

	private void setForYouCards()
	{
		List<Event> events = Data.events();
		List<CardList> data = Arrays.asList(new CardList(R.string.section_popular, false, events),
				new CardList(R.string.section_today_events, false, events),
				new CardList(R.string.section_tomorrow_events, false, events));
		recyclerView.setAdapter(new CardSectionAdapter(getContext(), data, false));
	}
}
