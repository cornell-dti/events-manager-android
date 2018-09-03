package com.dti.cornell.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.EventBusUtils;
import com.dti.cornell.events.utils.SettingsUtil;
import com.dti.cornell.events.utils.SpacingItemDecoration;
import com.dti.cornell.events.utils.TagUtil;
import com.google.common.eventbus.Subscribe;

//import javax.swing.text.html.HTML;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener
{
	private Toolbar toolbar;
	private Toolbar profileToolbar;
	private RecyclerView datePicker;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (SettingsUtil.SINGLETON.getFirstRun())
			startActivity(new Intent(this, OnboardingActivity.class));

		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		profileToolbar = findViewById(R.id.profileToolbar);

		datePicker = findViewById(R.id.datePicker);
		int horizMargin = getResources().getDimensionPixelSize(R.dimen.spacing_xl);
		datePicker.addItemDecoration(new SpacingItemDecoration(horizMargin, 0));
		datePicker.setAdapter(new DateAdapter(this));

		BottomNavigationView tabBar = findViewById(R.id.tabBar);
		tabBar.setOnNavigationItemSelectedListener(this);
		tabBar.setSelectedItemId(R.id.tab_discover);    //select discover page first

		if(!TagUtil.tagsLoaded){
			SettingsUtil.loadTags(this);
			for (Integer loadedTag : TagUtil.tagsInterested){
				Log.e("TAG LOADED", String.valueOf(loadedTag));
			}
		}

	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item)
	{
		Fragment fragment;
		switch (item.getItemId())
		{
			case R.id.tab_discover:
				toolbar.setTitle(R.string.tab_discover);
				fragment = new DiscoverFragment();
				toolbar.setVisibility(View.VISIBLE);
				datePicker.setVisibility(View.GONE);
				profileToolbar.setVisibility(View.GONE);
				break;
			case R.id.tab_for_you:
				toolbar.setTitle(R.string.tab_for_you);
				fragment = new ForYouFragment();
				toolbar.setVisibility(View.VISIBLE);
				datePicker.setVisibility(View.GONE);
				profileToolbar.setVisibility(View.GONE);
				break;
			case R.id.tab_my_events:
				toolbar.setTitle(R.string.tab_my_events);
				fragment = new MyEventsFragment();
				toolbar.setVisibility(View.VISIBLE);
				datePicker.setVisibility(View.VISIBLE);
				profileToolbar.setVisibility(View.GONE);
				break;
			case R.id.tab_profile:
				fragment = new ProfileFragment();
				toolbar.setVisibility(View.GONE);
				datePicker.setVisibility(View.GONE);
				profileToolbar.setVisibility(View.VISIBLE);
				break;
			default:
				return false;
		}

		transitionToFragment(fragment);
		return true;
	}

	public void transitionToFragment(Fragment fragment)
	{
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.fragmentContainer, fragment)
				.commit();
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		EventBusUtils.SINGLETON.register(this);
	}

	@Override
	protected void onDestroy()
	{
		SettingsUtil.saveTags(this);
		super.onDestroy();
		EventBusUtils.SINGLETON.unregister(this);
	}

	@Subscribe
	public void onDateChanged(EventBusUtils.DateChanged dateChanged)
	{
		datePicker.getAdapter().notifyDataSetChanged();

		int position = Data.DATES.indexOf(Data.selectedDate);
		datePicker.scrollToPosition(position);
	}

	@Override
	public void onClick(View view) {
		if(view.getId() == R.id.toolbar){
			SearchActivity.start(this);
		}
	}
}
