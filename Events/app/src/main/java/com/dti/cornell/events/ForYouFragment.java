package com.dti.cornell.events;

import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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
			data.add(new CardList(Data.tagForID.get(tagID), true, TagUtil.suggestEventsForTagID(tagID)));
		}
		if(data.size() == 0){
			container.findViewById(R.id.noEventsForYouLayout).setVisibility(View.VISIBLE);
		} else {
			container.findViewById(R.id.noEventsForYouLayout).setVisibility(View.GONE);
		}
		RecyclerUtil.addVerticalSpacing(recyclerView);
		recyclerView.setAdapter(new CardSectionAdapter(recyclerView.getContext(), data, false));

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