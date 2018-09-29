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
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.EventBusUtils;
import com.dti.cornell.events.utils.RecyclerUtil;
import com.dti.cornell.events.utils.TagUtil;

import java.util.ArrayList;
import java.util.List;

public class ForYouFragment extends Fragment
{
	public RecyclerView recyclerView;
	private int mTotalScrolled;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_recycler, container, false);

		RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
		List<Integer> bestTagIDs = TagUtil.getMostPopularTags(3);
		List<CardList> data = new ArrayList<>();
		for(int tagID : bestTagIDs){
			data.add(new CardList(Data.tagForID.get(tagID), false, TagUtil.suggestEventsForTagID(tagID)));
		}
		if(data.size() == 0){
			container.findViewById(R.id.noEventsForYouLayout).setVisibility(View.VISIBLE);
		} else {
			container.findViewById(R.id.noEventsForYouLayout).setVisibility(View.GONE);
		}
		RecyclerUtil.addVerticalSpacing(recyclerView);
		recyclerView.setAdapter(new CardSectionAdapter(getContext(), data, false));

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
}