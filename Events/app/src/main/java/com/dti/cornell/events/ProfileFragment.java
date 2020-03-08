package com.dti.cornell.events;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.models.Organization;
import com.dti.cornell.events.models.Settings;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.EventBusUtils;
import com.dti.cornell.events.utils.EventUtil;
import com.dti.cornell.events.utils.OrganizationUtil;
import com.dti.cornell.events.utils.RecyclerUtil;
import com.dti.cornell.events.utils.SettingsUtil;
import com.dti.cornell.events.utils.TagUtil;
import com.dti.cornell.events.utils.workers.NotifyWorker;
import com.google.common.collect.ImmutableList;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements Data.DataUpdateListener
{

	View rootView;
	RecyclerView followingRecycler;
	RecyclerView tagRecycler;
	Spinner dropdown;
	TextView notifyMeTime;
	Switch reminderSwitch;

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

	@Override
	public void onStop(){
		super.onStop();
		try{
			WorkManager.getInstance(this.getContext()).cancelAllWorkByTag(Data.NOTIFICATION_TAG);
			if(SettingsUtil.SINGLETON.getSettings().doSendNotifications){
				for(Event event : EventUtil.allAttendanceEvents.stream().map((val) -> Data.getEventFromID(val)).collect(Collectors.toSet())){

					if(DateTime.now().isBefore(event.startTime.minusMinutes(Integer.valueOf(SettingsUtil.SINGLETON.getSettings().notifyMeTime.split(" ")[0])))){
						androidx.work.Data.Builder builder = new androidx.work.Data.Builder();

                        androidx.work.Data inputData = builder.putString("event", event.toString())
                                .putString("location", (Data.locationForID.get(event.locationID) != null) ? Data.locationForID.get(event.locationID).toString() : "-1|||")
								.build();

						OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotifyWorker.class)
								.setInitialDelay(Data.getDelayUntilDateInMilliseconds(event.startTime.minusMinutes(Integer.valueOf(SettingsUtil.SINGLETON.getNotificationTimeBeforeEvent().split(" ")[0]))), TimeUnit.MILLISECONDS)
								.setInputData(inputData)
								.addTag(Data.NOTIFICATION_TAG)
								.build();

						WorkManager.getInstance(this.getContext()).enqueue(notificationWork);
					}
				}
			}
		} catch (Error e){
			Log.e("NOTIF ERROR", "Error getting context in ProfileFragment to create notification workers.");
		}
	}

	@Override
	public void onPause(){
		super.onPause();
		try{
			WorkManager.getInstance(this.getContext()).cancelAllWorkByTag(Data.NOTIFICATION_TAG);
			if(SettingsUtil.SINGLETON.getSettings().doSendNotifications){
				for(Event event : EventUtil.allAttendanceEvents.stream().map((val) -> Data.getEventFromID(val)).collect(Collectors.toSet())){

					if(DateTime.now().isBefore(event.startTime.minusMinutes(Integer.valueOf(SettingsUtil.SINGLETON.getSettings().notifyMeTime.split(" ")[0])))){
						androidx.work.Data.Builder builder = new androidx.work.Data.Builder();

                        androidx.work.Data inputData = builder.putString("event", event.toString())
                                .putString("location", (Data.locationForID.get(event.locationID) != null) ? Data.locationForID.get(event.locationID).toString() : "-1|||")
								.build();

						OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotifyWorker.class)
								.setInitialDelay(Data.getDelayUntilDateInMilliseconds(event.startTime.minusMinutes(Integer.valueOf(SettingsUtil.SINGLETON.getNotificationTimeBeforeEvent().split(" ")[0]))), TimeUnit.MILLISECONDS)
								.setInputData(inputData)
								.addTag(Data.NOTIFICATION_TAG)
								.build();

						WorkManager.getInstance(this.getContext()).enqueue(notificationWork);
					}
				}
			}
		} catch (Error e){
			Log.e("NOTIF ERROR", "Error getting context in ProfileFragment to create notification workers.");
		}
	}

	public void findViews(View v){

		String[] items = new String[]{"15 Minutes Before", "30 Minutes Before", "45 Minutes Before", "60 Minutes Before"};

		dropdown = v.findViewById(R.id.spinner);
		reminderSwitch = v.findViewById(R.id.reminderSwitch);

		List<String> itemsList = Arrays.asList(items);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(v.getContext(), R.layout.spinner_item, items);
		String notifTime = SettingsUtil.SINGLETON.getSettings().notifyMeTime;
		//set the spinners adapter to the previously created one.
		dropdown.setAdapter(adapter);
		dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				notifyMeTime.setText(items[i]);
				Settings newSettings = SettingsUtil.SINGLETON.getSettings().clone();
				newSettings.notifyMeTime = items[i];
				newSettings.doSendNotifications = reminderSwitch.isChecked();
				SettingsUtil.SINGLETON.setSettings(newSettings);
				EventBusUtils.SINGLETON.post(new EventBusUtils.NotificationUpdate());
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				dropdown.setSelection(itemsList.indexOf(Arrays.stream(items).filter((val) -> val.equalsIgnoreCase(notifTime)).collect(Collectors.toList()).get(0)));
			}
		});
		try{
			dropdown.setSelection(itemsList.indexOf(Arrays.stream(items).filter((val) -> val.equalsIgnoreCase(notifTime)).collect(Collectors.toList()).get(0)));
		} catch (Exception e){
			e.printStackTrace();
			Log.e("ERROR MATCHING NOTIFY TIME OPTION", SettingsUtil.SINGLETON.getSettings().toString());
			dropdown.setSelection(0);
			reminderSwitch.setChecked(true);
			Settings newSettings = new Settings("15 Minutes Before", true);
			SettingsUtil.SINGLETON.setSettings(newSettings);
			EventBusUtils.SINGLETON.post(new EventBusUtils.NotificationUpdate());

		}

		reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				Settings newSettings = SettingsUtil.SINGLETON.getSettings().clone();
				newSettings.doSendNotifications = b;
				newSettings.notifyMeTime = items[Math.max(dropdown.getSelectedItemPosition(), 0)];
				SettingsUtil.SINGLETON.setSettings(newSettings);
				EventBusUtils.SINGLETON.post(new EventBusUtils.NotificationUpdate());
			}
		});
		reminderSwitch.setChecked(SettingsUtil.SINGLETON.getSettings().doSendNotifications);
		notifyMeTime = v.findViewById(R.id.notifyMeTime);
		notifyMeTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dropdown.performClick();
			}
		});


		followingRecycler = v.findViewById(R.id.followingRecycler);
		tagRecycler = v.findViewById(R.id.tagRecycler);
	}

	public List<Organization> getFollowedOrganizations() {
		List<Organization> follow = new ArrayList<>();
		for (Organization org : Data.organizations()) {
			if (OrganizationUtil.userIsFollowing(org.id)) follow.add(org);
		}
		return follow;
	}

	public void configure(){
		followingRecycler.setAdapter(new OrganizationAdapter(followingRecycler.getContext(),
				getFollowedOrganizations(), false, false));
		tagRecycler.setAdapter(new TagAdapter(tagRecycler.getContext(),
				ImmutableList.copyOf(TagUtil.getMostPopularTags(100)), false, true));
	}

	@Override
	public void eventUpdate(List<Event> e) {
		followingRecycler.setAdapter(new OrganizationAdapter(followingRecycler.getContext(),
				getFollowedOrganizations(), false, false));
		tagRecycler.setAdapter(new TagAdapter(tagRecycler.getContext(),
				ImmutableList.copyOf(TagUtil.getMostPopularTags(100)), false, true));
	}

	@Override
	public void orgUpdate(List<Organization> o) {

	}

	@Override
	public void tagUpdate(List<String> t) {

	}
}
