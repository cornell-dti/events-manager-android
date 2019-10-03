package com.dti.cornell.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dti.cornell.events.models.CardList;
import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.models.Organization;
import com.dti.cornell.events.utils.Comparators;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.EventBusUtils;
import com.dti.cornell.events.utils.RecyclerUtil;
import com.dti.cornell.events.utils.TagUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class ForYouFragment extends Fragment implements Data.DataUpdateListener
{
	public RecyclerView recyclerView;
	public ViewGroup container;
	private int mTotalScrolled;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_recycler, container, false);
		Data.registerListener(this);

		RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
		this.recyclerView = recyclerView;
		this.container = container;

		populate();
		RecyclerUtil.addVerticalSpacing(recyclerView);
		setOnScrollListener();

		return view;
	}

	public void populate(){
		List<Integer> bestTagIDs = TagUtil.getMostPopularTags(3);
		List<CardList> data = new ArrayList<>();
		for(int tagID : bestTagIDs){
			List<Event> events = TagUtil.suggestEventsForTagID(tagID);
			events.sort(Comparators.NUM_ATTENDEES);
			Collections.reverse(events);
			data.add(new CardList(Data.tagForID.get(tagID), true, events));
		}
		if(data.size() == 0){
			container.findViewById(R.id.noEventsForYouLayout).setVisibility(View.VISIBLE);
		} else {
			container.findViewById(R.id.noEventsForYouLayout).setVisibility(View.GONE);
		}
		recyclerView.setAdapter(new CardSectionAdapter(recyclerView.getContext(), data, false));
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
	public void eventUpdate(List<Event> e) {
		populate();
	}

	@Override
	public void orgUpdate(List<Organization> o) {

	}

	@Override
	public void tagUpdate(List<String> t) {

	}
}