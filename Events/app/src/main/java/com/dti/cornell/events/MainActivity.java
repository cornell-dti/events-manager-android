package com.dti.cornell.events;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.models.Organization;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.EventBusUtils;
import com.dti.cornell.events.utils.EventUtil;
import com.dti.cornell.events.utils.Internet;
import com.dti.cornell.events.utils.OrganizationUtil;
import com.dti.cornell.events.utils.SearchUtil;
import com.dti.cornell.events.utils.SettingsUtil;
import com.dti.cornell.events.utils.SpacingItemDecoration;
import com.dti.cornell.events.utils.TagUtil;
import com.google.common.eventbus.Subscribe;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener, Data.DataUpdateListener
{
	private Toolbar toolbar;
	private FloatingActionButton backButton;
	private Toolbar profileToolbar;
	private TextView toolbarTitleSmall;
	private TextView toolbarTitleBig;
	private RecyclerView datePicker;
	private boolean toolbarShrunk;
	ConstraintLayout noEventsForYou;
	SwipeRefreshLayout swipeRefreshLayout;
	private ImageView progressBlocker;
	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTheme(R.style.AppTheme_NoActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Register for scroll event
		EventBusUtils.SINGLETON.register(this);
		Data.registerListener(this);

//		if (SettingsUtil.SINGLETON.getFirstRun())
//		startActivity(new Intent(this, OnboardingActivity.class));

		toolbar = findViewById(R.id.toolbar);
		toolbar.setVisibility(View.VISIBLE);
		toolbarShrunk = false;
		setSupportActionBar(toolbar);
		backButton = findViewById(R.id.back2);
		backButton.setOnClickListener(this);

		progressBlocker = findViewById(R.id.loadBlocker);
		progressBar = findViewById(R.id.progressBar);

		profileToolbar = findViewById(R.id.profileToolbar);
		toolbarTitleSmall = findViewById(R.id.toolbarTitleSmall);
		toolbarTitleBig = findViewById(R.id.toolbarTitleBig);
		noEventsForYou = findViewById(R.id.noEventsForYouLayout);
		noEventsForYou.setVisibility(View.GONE);
		swipeRefreshLayout = findViewById(R.id.swiperefresh);
		swipeRefreshLayout.setOnRefreshListener(
				new SwipeRefreshLayout.OnRefreshListener() {
					@Override
					public void onRefresh() {
						Log.i("REFRESHED", "onRefresh called from SwipeRefreshLayout");
						swipeRefreshLayout.setRefreshing(false);
//						// This method performs the actual data-refresh operation.
//						// The method calls setRefreshing(false) when it's finished.
//						myUpdateOperation();
					}
				}
		);

		datePicker = findViewById(R.id.datePicker);
		int horizMargin = getResources().getDimensionPixelSize(R.dimen.spacing_xl);
		datePicker.addItemDecoration(new SpacingItemDecoration(horizMargin, 0));
		datePicker.setAdapter(new DateAdapter(this));

		BottomNavigationView tabBar = findViewById(R.id.tabBar);
		tabBar.setOnNavigationItemSelectedListener(this);
		tabBar.setSelectedItemId(R.id.tab_discover);    //select discover page first
		setToolbarText(R.string.tab_discover);
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

//		Internet.getEventFeed();
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
				setToolbarText(R.string.tab_discover);
				fragment = new DiscoverFragment();
				toolbar.setVisibility(View.VISIBLE);
				expandToolBar();
				datePicker.setVisibility(View.GONE);
				profileToolbar.setVisibility(View.GONE);
				noEventsForYou.setVisibility(View.GONE);
				hideBackButton();
				break;
			case R.id.tab_for_you:
				setToolbarText(R.string.tab_for_you);
				fragment = new ForYouFragment();
				toolbar.setVisibility(View.VISIBLE);
				expandToolBar();
				datePicker.setVisibility(View.GONE);
				profileToolbar.setVisibility(View.GONE);
				hideBackButton();
				break;
			case R.id.tab_my_events:
				setToolbarText(R.string.tab_my_events);
				fragment = new MyEventsFragment();
				toolbar.setVisibility(View.VISIBLE);
				expandToolBar();
				datePicker.setVisibility(View.VISIBLE);
				profileToolbar.setVisibility(View.GONE);
				noEventsForYou.setVisibility(View.GONE);
				hideBackButton();
				break;
			case R.id.tab_profile:
				fragment = new ProfileFragment();
				setToolbarText(R.string.tab_profile);
				toolbar.setVisibility(View.VISIBLE);
				expandToolBar();
				datePicker.setVisibility(View.GONE);
				profileToolbar.setVisibility(View.GONE);
				noEventsForYou.setVisibility(View.GONE);
				hideBackButton();
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
				setToolbarText(R.string.tab_discover);
				fragment = new DiscoverFragment();
				toolbar.setVisibility(View.VISIBLE);
				datePicker.setVisibility(View.GONE);
				profileToolbar.setVisibility(View.GONE);
				hideBackButton();
				break;
			case R.id.tab_for_you:
				setToolbarText(R.string.tab_for_you);
				fragment = new ForYouFragment();
				toolbar.setVisibility(View.VISIBLE);
				datePicker.setVisibility(View.GONE);
				profileToolbar.setVisibility(View.GONE);
				hideBackButton();
				break;
			case R.id.tab_my_events:
				setToolbarText(R.string.tab_my_events);
				fragment = new MyEventsFragment();
				toolbar.setVisibility(View.VISIBLE);
				datePicker.setVisibility(View.VISIBLE);
				profileToolbar.setVisibility(View.GONE);
				hideBackButton();
				break;
			case R.id.tab_profile:
				fragment = new ProfileFragment();
				toolbar.setVisibility(View.GONE);
				datePicker.setVisibility(View.GONE);
				profileToolbar.setVisibility(View.VISIBLE);
				hideBackButton();
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
		Resources r = this.getResources();
		int newHeight = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				50,
				r.getDisplayMetrics()
		);
		ValueAnimator anim = ValueAnimator.ofInt(toolbar.getMeasuredHeight(), newHeight);
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
		// Big text animation animation
		if (toolbarTitleBig.getAlpha() != 0) {
			ValueAnimator bigTextAnim = ValueAnimator.ofFloat(1f, 0f);
			bigTextAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator) {
					float alpha = (float) valueAnimator.getAnimatedValue();
					toolbarTitleBig.setAlpha(alpha);
				}
			});
			bigTextAnim.setDuration(400);
			bigTextAnim.start();
		}
		// Small text animation animation
		if (toolbarTitleSmall.getAlpha() != 1) {
			ValueAnimator smallTextAnim = ValueAnimator.ofFloat(0f, 1f);
			smallTextAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator) {
					float alpha = (float) valueAnimator.getAnimatedValue();
					toolbarTitleSmall.setAlpha(alpha);
				}
			});
			smallTextAnim.setDuration(400);
			smallTextAnim.start();
		}
	}

	public void expandToolBar() {
		toolbarShrunk = false;
		Log.i("Toolbar height", Integer.toString(toolbar.getMeasuredHeight()));
		// Height animation
		Resources r = this.getResources();
		int newHeight = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				70,
				r.getDisplayMetrics()
		);
		ValueAnimator anim = ValueAnimator.ofInt(toolbar.getMeasuredHeight(), newHeight);
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
		// Small text animation animation
		if (toolbarTitleSmall.getAlpha() != 0) {
			ValueAnimator smallTextAnim = ValueAnimator.ofFloat(1f, 0f);
			smallTextAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator) {
					float alpha = (float) valueAnimator.getAnimatedValue();
					toolbarTitleSmall.setAlpha(alpha);
				}
			});
			smallTextAnim.setDuration(400);
			smallTextAnim.start();
		}
		// Big text animation animation
		if (toolbarTitleBig.getAlpha() != 1) {
			ValueAnimator bigTextAnim = ValueAnimator.ofFloat(0f, 1f);
			bigTextAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator) {
					float alpha = (float) valueAnimator.getAnimatedValue();
					toolbarTitleBig.setAlpha(alpha);
				}
			});
			bigTextAnim.setDuration(400);
			bigTextAnim.start();
		}
	}

	public void setToolbarText(int text) {
		toolbar.setTitle(text);
		toolbarTitleSmall.setText(text);
		toolbarTitleBig.setText(text);
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

	public void showBackButton() {
		expandToolBar();
		Resources r = this.getResources();
		int leftMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				50,
				r.getDisplayMetrics()
		);
		int topMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				12,
				r.getDisplayMetrics()
		);
		ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) toolbarTitleBig.getLayoutParams();
		params.leftMargin = leftMargin;
		toolbarTitleBig.setLayoutParams(params);
		backButton.setAlpha(1.0f);
	}

	public void hideBackButton() {
		Resources r = this.getResources();
		int leftMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				0,
				r.getDisplayMetrics()
		);
		int topMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				12,
				r.getDisplayMetrics()
		);
		ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) toolbarTitleBig.getLayoutParams();
		params.leftMargin = leftMargin;
		toolbarTitleBig.setLayoutParams(params);
		backButton.setAlpha(0.0f);
	}

	public void onClick(View v)
	{
		hideBackButton();
		Fragment fragment = new DiscoverFragment();
		toolbar.setVisibility(View.VISIBLE);
		expandToolBar();
		datePicker.setVisibility(View.GONE);
		profileToolbar.setVisibility(View.GONE);
		noEventsForYou.setVisibility(View.GONE);
		setToolbarText(R.string.tab_discover);
		transitionToFragment(fragment);
	}

	@Override
	public void eventUpdate(List<Event> e) {
		progressBar.setVisibility(View.GONE);
		progressBlocker.setVisibility(View.GONE);
	}

	@Override
	public void orgUpdate(List<Organization> o) {
		progressBar.setVisibility(View.GONE);
		progressBlocker.setVisibility(View.GONE);
	}

	@Override
	public void tagUpdate(List<String> t) {
		progressBar.setVisibility(View.GONE);
		progressBlocker.setVisibility(View.GONE);
	}
}