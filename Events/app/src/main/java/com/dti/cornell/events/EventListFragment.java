package com.dti.cornell.events;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dti.cornell.events.utils.Data;

public class EventListFragment extends Fragment
{
	private RecyclerView recyclerView;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_card_sections, container, false);

		int margin = getResources().getDimensionPixelSize(R.dimen.spacing_xxl);
		recyclerView = view.findViewById(R.id.recyclerView);
		recyclerView.addItemDecoration(new com.dti.cornell.events.utils.DividerItemDecoration(margin));
		recyclerView.setAdapter(new EventAdapter(getContext(), Data.events()));

		return view;
	}
}