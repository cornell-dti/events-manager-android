package com.dti.cornell.events;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.dti.cornell.events.utils.Callback;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.EventBusUtils;
import com.dti.cornell.events.utils.EventUtil;
import com.dti.cornell.events.utils.Internet;
import com.dti.cornell.events.utils.SettingsUtil;
import com.dti.cornell.events.utils.SpacingItemDecoration;
import com.dti.cornell.events.utils.workers.NotifyWorker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.eventbus.Subscribe;

import org.joda.time.DateTime;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener, Data.DataUpdateListener
{
	private Toolbar toolbar;
	public boolean isCurrentlySeeMore = false;
	private FloatingActionButton backButton;
	private Toolbar profileToolbar;
	private TextView toolbarTitleSmall;
	private TextView toolbarTitleBig;
	private RecyclerView datePicker;
	private boolean toolbarShrunk;
	ConstraintLayout noEventsForYou;
	SwipeRefreshLayout swipeRefreshLayout;
	private ImageView progressBlocker;
	private ImageView noConnection;
	private ProgressBar progressBar;
	private BottomNavigationView tabBar;
	public static String OPEN_EVENT = "open_event";

	private boolean hasReturned = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTheme(R.style.AppTheme_NoActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//		EventBusUtils.SINGLETON.register(this);
		//Register for scroll event
		EventBusUtils.SINGLETON.register(this);
		Data.registerListener(this);
		SettingsUtil.SINGLETON.doLoad();

//		if (SettingsUtil.SINGLETON.getFirstRun())
//			startActivity(new Intent(this, OnboardingActivity.class));

		if(getIntent().getData()!=null){
			Uri data = getIntent().getData();
			String scheme = data.getScheme();
			String fullPath = data.getEncodedSchemeSpecificPart();
			int id = Integer.valueOf((scheme +":"+fullPath).split("/")[(scheme +":"+fullPath).split("/").length-1]);
			String type = (scheme +":"+fullPath).split("/")[(scheme +":"+fullPath).split("/").length-2];
			if(type.equalsIgnoreCase("event")){
				int eventID = id;
				if(Data.eventForID.containsKey(eventID)){
					DetailsActivity.startWithEvent(Data.getEventFromID(eventID),
							this);
				} else {
					Internet.getEventsThenOpenEvent(eventID, this);
				}
			}
			else if (type.equalsIgnoreCase("org")){
				int orgID = id;
				if(Data.organizationForID.containsKey(orgID)){
					OrganizationActivity.startWithOrganization(Data.organizationForID.get(orgID), this);
				} else {
					Context context = this;
					Internet.getSingleOrganization(orgID, new Callback<Organization>() {
						@Override
						public void execute(Organization organization) {
							OrganizationActivity.startWithOrganization(Data.organizationForID.get(orgID), context);
						}
					});
				}
			}
			getIntent().setData(null);
		}

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
		tabBar = findViewById(R.id.tabBar);
		swipeRefreshLayout = findViewById(R.id.swiperefresh);
		swipeRefreshLayout.setOnRefreshListener(
				new SwipeRefreshLayout.OnRefreshListener() {
					@Override
					public void onRefresh() {
						Log.i("REFRESHED", "onRefresh called from SwipeRefreshLayout");
						swipeRefreshLayout.setRefreshing(false);
						if(!hasReturned){
							progressBar.setVisibility(View.VISIBLE);
							progressBlocker.setVisibility(View.VISIBLE);
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									if(!hasReturned){
										noConnection.setVisibility(View.VISIBLE);
										progressBar.setVisibility(View.GONE);
										progressBlocker.setVisibility(View.GONE);
									}
								}
							}, 4000);
						}
						hasReturned = false;
						Data.getData();
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

		noConnection = findViewById(R.id.imageView3);
		noConnection.setVisibility(View.GONE);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if(!hasReturned){
					noConnection.setVisibility(View.VISIBLE);
				}
			}
		}, 5000);

		BottomNavigationView tabBar = findViewById(R.id.tabBar);
		tabBar.setOnNavigationItemSelectedListener(this);
		tabBar.setSelectedItemId(R.id.tab_discover);    //select discover page first
		setToolbarText(R.string.tab_discover);
//		setTabBarFont(tabBar);

		if(Data.events().size() > 0){
			progressBar.setVisibility(View.GONE);
			progressBlocker.setVisibility(View.GONE);
		}

		if (getIntent().getExtras() != null){
			boolean mustOpen = getIntent().getExtras().containsKey(OPEN_EVENT) ? getIntent().getExtras().getBoolean(OPEN_EVENT) : false;
			if(mustOpen){
				String eventString = getIntent().getExtras().getString(DetailsActivity.EVENT_KEY);
				DetailsActivity.startWithEvent(Event.fromString(eventString), this);
			}
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
				hideBackButton();
				setToolbarText(R.string.tab_discover);
				fragment = new DiscoverFragment();
				toolbar.setVisibility(View.VISIBLE);
				expandToolBar();
				datePicker.setVisibility(View.GONE);
				profileToolbar.setVisibility(View.GONE);
				noEventsForYou.setVisibility(View.GONE);
				break;
			case R.id.tab_for_you:
				hideBackButton();
				setToolbarText(R.string.tab_for_you);
				fragment = new ForYouFragment();
				toolbar.setVisibility(View.VISIBLE);
				expandToolBar();
				datePicker.setVisibility(View.GONE);
				profileToolbar.setVisibility(View.GONE);
				break;
			case R.id.tab_my_events:
				hideBackButton();
				setToolbarText(R.string.tab_my_events);
				fragment = new MyEventsFragment();
				toolbar.setVisibility(View.VISIBLE);
				expandToolBar();
				datePicker.setVisibility(View.VISIBLE);
				profileToolbar.setVisibility(View.GONE);
				noEventsForYou.setVisibility(View.GONE);
				break;
			case R.id.tab_profile:
				hideBackButton();
				fragment = new ProfileFragment();
				setToolbarText(R.string.tab_profile);
				toolbar.setVisibility(View.VISIBLE);
				expandToolBar();
				datePicker.setVisibility(View.GONE);
				profileToolbar.setVisibility(View.GONE);
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
//        transitionToAppropriateFragment();
	}

	@Override
	protected void onStop(){
		SettingsUtil.doSave();
		EventBusUtils.SINGLETON.unregister(this);
		super.onStop();
	}

	@Override
	protected void onDestroy()
	{
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

	public void setSeeMoreTitle() {
		toolbarShrunk = true;
		toolbarTitleBig.setAlpha(0);
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
			smallTextAnim.addListener(new Animator.AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animator) {
					toolbarTitleBig.setAlpha(0);
				}

				@Override
				public void onAnimationEnd(Animator animator) {
					toolbarTitleSmall.setAlpha(1);
				}

				@Override
				public void onAnimationCancel(Animator animator) {

				}

				@Override
				public void onAnimationRepeat(Animator animator) {

				}
			});
			smallTextAnim.setDuration(400);
			smallTextAnim.start();
		}
	}

	public void expandToolBar() {
		toolbarShrunk = false;
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

	public void setToolbarText(String text) {
		toolbar.setTitle(text);
		toolbarTitleSmall.setText(text);
		toolbarTitleBig.setText(text);
	}

	@Subscribe
	public void onSearchChanged(EventBusUtils.MainActivityScrolled ms)
	{
		//check if ms.scrollY is whatever value and do stuff
		if (ms.scrollY >= 30 && !toolbarShrunk) {
			shrinkToolBar();
		}
		if (ms.scrollY < 30 && toolbarShrunk) {
			expandToolBar();
		}
	}

	public void showBackButton() {
		backButton.setClickable(true);
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
		backButton.setClickable(false);
	}

	@Override
	public void onBackPressed(){
		if(this.isCurrentlySeeMore){
			this.isCurrentlySeeMore = false;
			hideBackButton();
			transitionToAppropriateFragment();
		} else {
			super.onBackPressed();
		}
	}

	public void onClick(View v)
	{
		hideBackButton();
		transitionToAppropriateFragment();
		this.isCurrentlySeeMore = false;
//		Fragment fragment = new DiscoverFragment();
//		toolbar.setVisibility(View.VISIBLE);
//		expandToolBar();
//		datePicker.setVisibility(View.GONE);
//		profileToolbar.setVisibility(View.GONE);
//		noEventsForYou.setVisibility(View.GONE);
//		setToolbarText(R.string.tab_discover);
//		transitionToFragment(fragment);
	}

	@Override
	public void eventUpdate(List<Event> e) {
		noConnection.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);
		progressBlocker.setVisibility(View.GONE);
		hasReturned = true;

		EventUtil.allAttendanceEvents = EventUtil.allAttendanceEvents.stream().filter((val) -> Data.getEventFromID(val) != null).collect(Collectors.toList());

		WorkManager.getInstance(this).cancelAllWorkByTag(Data.NOTIFICATION_TAG);
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

					WorkManager.getInstance(this).enqueue(notificationWork);
				}
			}
		}
//		if(getIntent().getData()!=null){
//			Uri data = getIntent().getData();
//			String scheme = data.getScheme();
//			String fullPath = data.getEncodedSchemeSpecificPart();
//			// Handle app link data here
////			DetailsActivity.startWithEvent(Data.getEventFromID(Integer.valueOf((scheme +":"+fullPath).split("/")[(scheme +":"+fullPath).split("/").length-1])),
//					this);
//			Log.e("URL GIVEN", scheme +":"+fullPath);
//		}
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

	public void transitionToAppropriateFragment(){
		Fragment fragment;
        switch(tabBar.getSelectedItemId()){
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
                setToolbarText(R.string.tab_discover);
                fragment = new DiscoverFragment();
                toolbar.setVisibility(View.VISIBLE);
                datePicker.setVisibility(View.GONE);
                profileToolbar.setVisibility(View.GONE);
                hideBackButton();
                break;
        }
        transitionToFragment(fragment);
    }

	@Override
	public void onResume(){
		super.onResume();

//		transitionToAppropriateFragment();
//		progressBar.setVisibility(View.GONE);
//		progressBlocker.setVisibility(View.GONE);
//		Data.getData();
		if(getIntent().getData()!=null){
			Uri data = getIntent().getData();
			String scheme = data.getScheme();
			String fullPath = data.getEncodedSchemeSpecificPart();
			int id = Integer.valueOf((scheme +":"+fullPath).split("/")[(scheme +":"+fullPath).split("/").length-1]);
			String type = (scheme +":"+fullPath).split("/")[(scheme +":"+fullPath).split("/").length-2];
			if(type.equalsIgnoreCase("event")){
				int eventID = id;
				if(Data.eventForID.containsKey(eventID)){
					DetailsActivity.startWithEvent(Data.getEventFromID(eventID),
							this);
				} else {
					Internet.getEventsThenOpenEvent(eventID, this);
				}
			}
			else if (type.equalsIgnoreCase("org")){
				int orgID = id;
				if(Data.organizationForID.containsKey(orgID)){
					OrganizationActivity.startWithOrganization(Data.organizationForID.get(orgID), this);
				} else {
					Context context = this;
					Internet.getSingleOrganization(orgID, new Callback<Organization>() {
						@Override
						public void execute(Organization organization) {
							OrganizationActivity.startWithOrganization(Data.organizationForID.get(orgID), context);
						}
					});
				}
			}
			getIntent().setData(null);
		}

	}

	@Subscribe
	public void onNotificationUpdate(EventBusUtils.NotificationUpdate notifChange)
	{
		WorkManager.getInstance(this).cancelAllWorkByTag(Data.NOTIFICATION_TAG);
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


					WorkManager.getInstance(this).enqueue(notificationWork);
				}
			}
		}
	}
}