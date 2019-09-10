package com.dti.cornell.events;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.OrganizationUtil;

import java.util.stream.Collectors;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment
{

	View rootView;
	RecyclerView followingRecycler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{

		View view = inflater.inflate(R.layout.fragment_profile, container, false);
		rootView = view;
		findViews(view);
		configure();
		// Inflate the layout for this fragment
		return view;
	}

	public void findViews(View v){
		followingRecycler = v.findViewById(R.id.followingRecycler);
	}

	public void configure(){
		followingRecycler.setAdapter(new OrganizationAdapter(this.getContext(),
				Data.organizations().stream().filter((val)->OrganizationUtil.userIsFollowing(val.id)).collect(Collectors.toList()), false));
	}

}
