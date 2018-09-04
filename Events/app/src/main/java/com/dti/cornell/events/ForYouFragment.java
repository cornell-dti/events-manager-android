package com.dti.cornell.events;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dti.cornell.events.models.CardList;
import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.RecyclerUtil;
import com.dti.cornell.events.utils.TagUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ForYouFragment extends Fragment
{
	private RecyclerView recyclerView;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_card_sections, container, false);

		recyclerView = view.findViewById(R.id.recyclerView);
		List<Integer> bestTagIDs = TagUtil.getMostPopularTags(3);
		List<CardList> data = new ArrayList<>();
		int indexer = 0;
		for(int tagID : bestTagIDs){
			data.add(new CardList(Data.tagForID.get(tagID), false, TagUtil.suggestEventsForTagID(tagID)));
			indexer++;
		}
		RecyclerUtil.addVerticalSpacing(recyclerView);
		recyclerView.setAdapter(new CardSectionAdapter(getContext(), data, false));

		return view;
	}
}