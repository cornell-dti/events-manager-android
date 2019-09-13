package com.dti.cornell.events;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.models.Organization;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.OrganizationUtil;
import com.dti.cornell.events.utils.RecyclerUtil;
import com.dti.cornell.events.utils.SettingsUtil;
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
	Spinner dropdown;
	TextView notifyReminderAmount;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{

		View view = inflater.inflate(R.layout.fragment_profile, container, false);
		rootView = view;
		findViews(view);
		configure();
		RecyclerUtil.addTagHorizontalSpacing(tagRecycler);
		Data.registerListener(this);
		// Inflate the layout for this fragment
		return view;
	}

	public void findViews(View v){
		notifyReminderAmount = v.findViewById(R.id.notifyMeAmount);
		notifyReminderAmount.setText(SettingsUtil.SINGLETON.getSettingsObject().notifyMeTime);
		notifyReminderAmount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(dropdown.getVisibility() == View.VISIBLE){
					dropdown.setVisibility(View.GONE);
				} else if(dropdown.getVisibility() == View.GONE){
					dropdown.setVisibility(View.VISIBLE);
				}
			}
		});
		dropdown = v.findViewById(R.id.spinner);
		String[] items = new String[]{"15 Minutes Before", "30 Minutes Before", "45 Minutes Before", "1 Hour Before"};
		ArrayAdapter<String> adapter = new ArrayAdapter<>(v.getContext(), android.R.layout.simple_spinner_dropdown_item, items);
		dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				notifyReminderAmount.setText(((String)adapterView.getSelectedItem()));
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				notifyReminderAmount.setText(items[0]);
			}
		});
//set the spinners adapter to the previously created one.
		dropdown.setAdapter(adapter);
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
