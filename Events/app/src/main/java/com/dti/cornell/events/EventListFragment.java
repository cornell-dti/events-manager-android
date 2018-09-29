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
import com.dti.cornell.events.utils.RecyclerUtil;

public class EventListFragment extends Fragment
{

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_recycler, container, false);

		RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
		RecyclerUtil.addDivider(recyclerView);
		recyclerView.setAdapter(new EventAdapter(getContext(), Data.events()));

		return view;
	}
}