package com.dti.cornell.events;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.EventBusUtils;
import com.dti.cornell.events.utils.EventUtil;
import com.dti.cornell.events.utils.OrganizationUtil;
import com.dti.cornell.events.utils.SearchUtil;
import com.dti.cornell.events.utils.SettingsUtil;
import com.dti.cornell.events.utils.SpacingItemDecoration;
import com.dti.cornell.events.utils.TagUtil;
import com.google.common.eventbus.Subscribe;

import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener
{
	private Toolbar toolbar;
	private Toolbar profileToolbar;
	private RecyclerView datePicker;
	private boolean toolbarShrunk;
	ConstraintLayout noEventsForYou;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTheme(R.style.AppTheme_NoActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Register for scroll event
		EventBusUtils.SINGLETON.register(this);

		if (SettingsUtil.SINGLETON.getFirstRun())
			startActivity(new Intent(this, OnboardingActivity.class));

		toolbar = findViewById(R.id.toolbar);
		toolbar.setVisibility(View.VISIBLE);
		toolbarShrunk = false;
		setSupportActionBar(toolbar);
		profileToolbar = findViewById(R.id.profileToolbar);
		noEventsForYou = findViewById(R.id.noEventsForYouLayout);
		noEventsForYou.setVisibility(View.GONE);

		datePicker = findViewById(R.id.datePicker);
		int horizMargin = getResources().getDimensionPixelSize(R.dimen.spacing_xl);
		datePicker.addItemDecoration(new SpacingItemDecoration(horizMargin, 0));
		datePicker.setAdapter(new DateAdapter(this));

		BottomNavigationView tabBar = findViewById(R.id.tabBar);
		tabBar.setOnNavigationItemSelectedListener(this);
		tabBar.setSelectedItemId(R.id.tab_discover);    //select discover page first
		toolbar.setTitle(R.string.tab_discover);
//		setTabBarFont(tabBar);

		if(!TagUtil.tagsLoaded){
			SettingsUtil.loadTags(this);
			Log.e("HELP", "TAGS LOADED CALLED");
			for (Integer loadedTag : TagUtil.tagsInterested){
				Log.e("TAG LOADED", String.valueOf(loadedTag));
			}
		}
		if(!OrganizationUtil.organizationsLoaded){
			SettingsUtil.loadOrganizations(this);
			for (Integer loadedOrgID : OrganizationUtil.followedOrganizations){
				Log.e("ORG LOADED", String.valueOf(loadedOrgID));
			}
		}
		if(!EventUtil.attendanceLoaded){
			SettingsUtil.loadAttendance(this);
			for (Integer loadedEventID : EventUtil.allAttendanceEvents){
				Log.e("ATT LOADED", String.valueOf(loadedEventID));
			}
		}
		if(getIntent().getData()!=null){
			Uri data = getIntent().getData();
			String scheme = data.getScheme();
			String fullPath = data.getEncodedSchemeSpecificPart();
			// Handle app link data here
			Log.e("URL GIVEN", scheme+"://"+fullPath);
		}
	}

//	private void setTabBarFont(BottomNavigationView tabBar)
//	{
//		Typeface font = ResourcesCompat.getFont(this, R.font.text_regular);
//		TypefaceSpan span = new TypefaceSpan()
//		for (int i = 0; i < tabBar.getMenu().size(); i++)
//		{
//			MenuItem menuItem = tabBar.getMenu().getItem(i);
//			SpannableStringBuilder title = new SpannableStringBuilder(menuItem.getTitle());
//			title.setSpan();
//		}
//	}

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
				expandToolBar();
				datePicker.setVisibility(View.GONE);
				profileToolbar.setVisibility(View.GONE);
				noEventsForYou.setVisibility(View.GONE);
				break;
			case R.id.tab_for_you:
				toolbar.setTitle(R.string.tab_for_you);
				fragment = new ForYouFragment();
				toolbar.setVisibility(View.VISIBLE);
				expandToolBar();
				datePicker.setVisibility(View.GONE);
				profileToolbar.setVisibility(View.GONE);
				break;
			case R.id.tab_my_events:
				toolbar.setTitle(R.string.tab_my_events);
				fragment = new MyEventsFragment();
				toolbar.setVisibility(View.VISIBLE);
				expandToolBar();
				datePicker.setVisibility(View.VISIBLE);
				profileToolbar.setVisibility(View.GONE);
				noEventsForYou.setVisibility(View.GONE);
				break;
			case R.id.tab_profile:
				fragment = new ProfileFragment();
				toolbar.setVisibility(View.GONE);
				expandToolBar();
				datePicker.setVisibility(View.GONE);
				profileToolbar.setVisibility(View.VISIBLE);
				noEventsForYou.setVisibility(View.GONE);
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
		SettingsUtil.saveFollowedOrganizations(this);
		SettingsUtil.saveAttendance(this);
		EventBusUtils.SINGLETON.unregister(this);
		super.onDestroy();
	}

	@Subscribe
	public void onDateChanged(EventBusUtils.DateChanged dateChanged)
	{
		datePicker.getAdapter().notifyDataSetChanged();

		int position = Data.DATES.indexOf(Data.selectedDate);
		datePicker.scrollToPosition(position);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		Fragment fragment;
		switch (item.getItemId()) {
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
				return super.onOptionsItemSelected(item);
		}
		transitionToFragment(fragment);
		return true;
	}

	public void shrinkToolBar() {
		toolbarShrunk = true;
		Log.i("Toolbar height", Integer.toString(toolbar.getMeasuredHeight()));
		ValueAnimator anim = ValueAnimator.ofInt(toolbar.getMeasuredHeight(), 90);
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				int val = (Integer) valueAnimator.getAnimatedValue();
				ViewGroup.LayoutParams layoutParams = toolbar.getLayoutParams();
				layoutParams.height = val;
				toolbar.setLayoutParams(layoutParams);
			}
		});
		anim.setDuration(200);
		anim.start();
	}

	public void expandToolBar() {
		toolbarShrunk = false;
		Log.i("Toolbar height", Integer.toString(toolbar.getMeasuredHeight()));
		ValueAnimator anim = ValueAnimator.ofInt(toolbar.getMeasuredHeight(), 144);
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				int val = (Integer) valueAnimator.getAnimatedValue();
				ViewGroup.LayoutParams layoutParams = toolbar.getLayoutParams();
				layoutParams.height = val;
				toolbar.setLayoutParams(layoutParams);
			}
		});
		anim.setDuration(200);
		anim.start();
	}

	@Subscribe
	public void onSearchChanged(EventBusUtils.MainActivityScrolled ms)
	{
		//check if ms.scrollY is whatever value and do shit
		Log.d("Y scroll value", Integer.toString(ms.scrollY));
		if (ms.scrollY >= 30 && !toolbarShrunk) {
			shrinkToolBar();
		}
		if (ms.scrollY < 30 && toolbarShrunk) {
			expandToolBar();
		}
	}



//	@Override
//	public boolean onMenuItemClick(MenuItem item) {
//		Log.e("MENU OUT", item.getItemId() + "");
//		if(item.getItemId() == R.id.searchMenuItem){
//
//			//TODO: Make sure that SearchActivity actually starts
//			SearchActivity.start(this);
//			return true;
//		}
//		return false;
//	}
}