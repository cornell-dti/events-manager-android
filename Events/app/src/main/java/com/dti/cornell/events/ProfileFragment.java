package com.dti.cornell.events;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.models.Organization;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.OrganizationUtil;
import com.dti.cornell.events.utils.TagUtil;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.stream.Collectors;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements Data.DataUpdateListener
{

	View rootView;
	RecyclerView followingRecycler;
	RecyclerView tagRecycler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{

		View view = inflater.inflate(R.layout.fragment_profile, container, false);
		rootView = view;
		findViews(view);
		configure();
		Data.registerListener(this);
		// Inflate the layout for this fragment
		return view;
	}

	public void findViews(View v){
		followingRecycler = v.findViewById(R.id.followingRecycler);
		tagRecycler = v.findViewById(R.id.tagRecycler);
	}

	public void configure(){
		followingRecycler.setAdapter(new OrganizationAdapter(followingRecycler.getContext(),
				Data.organizations().stream().filter((val)->OrganizationUtil.userIsFollowing(val.id)).collect(Collectors.toList()), false));
		tagRecycler.setAdapter(new TagAdapter(tagRecycler.getContext(), ImmutableList.copyOf(TagUtil.getMostPopularTags(100)), false));
	}

	@Override
	public void eventUpdate(List<Event> e) {
		followingRecycler.setAdapter(new OrganizationAdapter(followingRecycler.getContext(),
				Data.organizations().stream().filter((val)->OrganizationUtil.userIsFollowing(val.id)).collect(Collectors.toList()), false));
		tagRecycler.setAdapter(new TagAdapter(tagRecycler.getContext(), ImmutableList.copyOf(TagUtil.getMostPopularTags(100)), false));
	}

	@Override
	public void orgUpdate(List<Organization> o) {

	}

	@Override
	public void tagUpdate(List<String> t) {

	}
}
