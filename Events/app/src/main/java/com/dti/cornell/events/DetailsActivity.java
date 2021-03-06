package com.dti.cornell.events;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.models.Location;
import com.dti.cornell.events.models.Organization;
import com.dti.cornell.events.utils.Callback;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.EventBusUtils;
import com.dti.cornell.events.utils.EventUtil;
import com.dti.cornell.events.utils.Internet;
import com.dti.cornell.events.utils.RecyclerUtil;
import com.dti.cornell.events.utils.SettingsUtil;
import com.dti.cornell.events.utils.workers.NotifyWorker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.common.eventbus.Subscribe;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ShareCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

/**
 * Created by jaggerbrulato on 2/27/18.
 */

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, Data.SingleEventUpdateListener, Data.DataUpdateListener
{
	private static final String TAG = DetailsActivity.class.getSimpleName();
	public static final String EVENT_KEY = "event";
	private TextView title;
	private TextView description;
	private TextView more;
	private View moreButtonGradient;
	private TextView time;
	private TextView numGoing;
	private TextView organization;
	private TextView location;
	private RecyclerView tagRecycler;
	private Event event;
	private TextView bookmarkedButton;
	private TextView tagsText;
	private ProgressBar imageLoadingBar;
	private CardView mapCard;
	private View map;
	private boolean isBookmarked;
	public ImageView image;
	private NestedScrollView scrollView;
	private int descriptionLineCount = 0;

	private static final int DESCRIPTION_MAX_LINES = 3;

	//map stuff
	private static final int MAP_ZOOM = 15;
	private PlacesClient placesClient;
//	private GeoDataClient geoDataClient;
	@Nullable
	private String placeName;
	@Nullable
	private String placeAddress;

	private FirebaseAnalytics firebaseAnalytics;

	public static void startWithEvent(Event event, Context context)
	{
		Intent intent = new Intent(context, DetailsActivity.class);
		intent.putExtra(EVENT_KEY, event.toString());
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTheme(R.style.AppTheme_NoActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
//		setStatusBarTranslucent();
		Data.registerListener((Data.DataUpdateListener)this);
		Data.registerListener((Data.SingleEventUpdateListener)this);
		EventBusUtils.SINGLETON.register(this);

		Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
		this.placesClient = Places.createClient(this);

		//get the event
		event = Event.fromString(getIntent().getExtras().getString(EVENT_KEY));
		Internet.getSingleEvent(event.id, new Callback<Event>() {
			@Override
			public void execute(Event event) {
				configure(event);
				EventBusUtils.SINGLETON.post(new EventBusUtils.NotificationUpdate());
			}
		});

		findViews();

		configure(event);

		scrollView.smoothScrollTo(0,0);

		firebaseAnalytics = FirebaseAnalytics.getInstance(this);
		logFirebaseEvent("eventViewed", event.title);
		if(Data.organizationForID.get(event.organizerID) != null && !Data.organizationForID.get(event.organizerID).email.equalsIgnoreCase("donotdisplay@cornell.edu")){
			organization.setPaintFlags(organization.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		}
	}

	private void setStatusBarTranslucent()
	{
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		View view = getWindow().getDecorView();
		int noLightStatus = view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
		view.setSystemUiVisibility(noLightStatus);
	}

	private void findViews()
	{
		image = findViewById(R.id.image);
		title = findViewById(R.id.title);
		description = findViewById(R.id.description);
		time = findViewById(R.id.time);
		numGoing = findViewById(R.id.numGoing);
		organization = findViewById(R.id.organization);
		organization.setOnClickListener(this);
		location = findViewById(R.id.location);
		more = findViewById(R.id.more);
		more.setOnClickListener(this);
		moreButtonGradient = findViewById(R.id.moreButtonGradient);
		imageLoadingBar = findViewById(R.id.imageLoadingBar);
		tagsText = findViewById(R.id.tags);
		map = findViewById(R.id.map);
		mapCard = findViewById(R.id.toolbarImageContainer);
		if(event.googlePlaceID.isEmpty() || event.googlePlaceID.equalsIgnoreCase("null") || event.googlePlaceID == null){
			map.setVisibility(View.GONE);
			mapCard.setVisibility(View.GONE);
		}
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.share).setOnClickListener(this);

		MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		tagRecycler = findViewById(R.id.tagRecycler);
		RecyclerUtil.addTagHorizontalSpacing(tagRecycler);

		bookmarkedButton = findViewById(R.id.bookmark);
		scrollView = findViewById(R.id.scrollView);

		Rect rectangle = new Rect();
		Window window = getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
		int statusBarHeight = rectangle.top;
		int contentViewTop =
				window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		int titleBarHeight = contentViewTop - statusBarHeight;
		image.setPadding(0, titleBarHeight, 0, 0);

	}

	@Override
	public void onStart(){
		super.onStart();
		setBookmarkedButtonState();
	}

	/**
	 * Returns the correct suffix for the last digit (1st, 2nd, .. , 13th, .. , 23rd)
	 */
	public static String getLastDigitSufix(int number) {
		switch( (number>10 && number<20) ? number : number%10 ) {
			case 1 : return "st";
			case 2 : return "nd";
			case 3 : return "rd";
			default : return "th";
		}
	}

	private void configure(Event event)
	{
		title.setText(event.title);
		description.setText(event.description);
		if(event.startTime.withTimeAtStartOfDay().isEqual(event.endTime.withTimeAtStartOfDay())){
			time.setText(event.startTime.toString("EEE. MMMM d") + getLastDigitSufix(event.startTime.dayOfMonth().get()) + ", " + event.startTime.toString("h:mm a") + " - " + event.endTime.toString("h:mm a"));

		} else {
			time.setText(event.startTime.toString("MMM. d") + getLastDigitSufix(event.startTime.dayOfMonth().get()) +event.startTime.toString(", h:mm a") + " - " + event.endTime.toString("MMM. d") + getLastDigitSufix(event.startTime.dayOfMonth().get()) + event.endTime.toString(", h:mm a"));
		}
		numGoing.setText(getString(R.string.numGoing, this.event.numAttendees));
		if(!Data.organizationForID.containsKey(event.organizerID)){
			Data.getData();
		}
		organization.setText(Data.organizationForID.containsKey(event.organizerID) ? Data.organizationForID.get(event.organizerID).name : "No organization available");
		Location loc = Data.locationForID.get(event.locationID);
		location.setText(loc == null ? "Loading..." : loc.room + ", " + loc.building);

		TagAdapter adapter = new TagAdapter(this, event.tagIDs, false, true);
		tagRecycler.setAdapter(adapter);

		if(event.tagIDs.isEmpty()){
			tagRecycler.setVisibility(View.GONE);
			tagsText.setVisibility(View.GONE);
		} else {
			tagRecycler.setVisibility(View.VISIBLE);
			tagsText.setVisibility(View.VISIBLE);
		}

		bookmarkedButton.setOnClickListener(this);
		isBookmarked = EventUtil.userHasBookmarked(event.id);
		this.setBookmarkedButtonState();

		configureDescription();

		image = findViewById(R.id.image);

//		Internet.getImageForEvent(event, image);
		Internet.getImageForEventStopProgress(event, image, imageLoadingBar);
		startMap();
	}

	private void configureDescription()
	{
		//find out how many lines the description text will be
		description.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
		{
			@Override
			public boolean onPreDraw()
			{
				if (more.getVisibility() != View.VISIBLE)
					return true;
				// save description line count only first time
				int lineCount = description.getLayout().getLineCount();
				if (descriptionLineCount > lineCount)
					return true;

				descriptionLineCount = lineCount;
				if (descriptionLineCount >= DESCRIPTION_MAX_LINES)
				{
					description.setLines(DESCRIPTION_MAX_LINES);
					more.setVisibility(View.VISIBLE);
					moreButtonGradient.setVisibility(View.VISIBLE);
				}
				else
				{
					description.setLines(descriptionLineCount);
					more.setVisibility(View.GONE);
					moreButtonGradient.setVisibility(View.GONE);
				}
				return true;
			}
		});
	}

	/**
	 * Loads {@link #placeName} and {@link #placeAddress} from {@link Event#googlePlaceID} and adds a marker to the map.
	 *
	 * @param map {@inheritDoc}
	 */
	@Override
	public void onMapReady(final GoogleMap map)
	{

		String placeId = event.googlePlaceID;

		List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

		FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();

		this.placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
			Place place = response.getPlace();
			placeName = place.getName();
			placeAddress = place.getAddress();
			LatLng position = place.getLatLng();
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, MAP_ZOOM));
			map.addMarker(new MarkerOptions().position(position).title(Data.locationForID.get(event.locationID).building));
		}).addOnFailureListener((exception) -> {
			Log.e(TAG, "Error on PlaceID fetch request: " + exception.getMessage());
		});
	}

	/**
	 * Open the map with the event's location. Only possible after loading {@link #placeName} and
	 * {@link #placeAddress} in {@link #onMapReady(GoogleMap)}.
	 */
	private void startMap()
	{
		if (placeName == null || placeAddress == null)
			return;

		Uri uri = Uri.parse("geo:0,0?q=" + placeName + ", " + placeAddress);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		intent.setPackage("com.google.android.apps.maps");
		startActivity(intent);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.back:
				EventBusUtils.SINGLETON.unregister(this);
				onBackPressed();
				break;
			case R.id.share:
				ShareCompat.IntentBuilder.from(this)
						.setType("text/plain")
						.setChooserTitle("Choose how to share this event")
						.setText("https://www.cuevents.org/event/" + this.event.id)
						.startChooser();
				//logFirebaseEvent("share", event.title);
				break;
			case R.id.more:
				description.setMaxLines(120);
				more.setVisibility(View.GONE);
				moreButtonGradient.setVisibility(View.GONE);
				ObjectAnimator animation = ObjectAnimator.ofInt(description, "lines", DESCRIPTION_MAX_LINES,descriptionLineCount);
				animation.setDuration(200).start();
				break;
			case R.id.organization:
				Organization organization = Data.organizationForID.get(event.organizerID);
				if(organization != null){
					OrganizationActivity.startWithOrganization(organization, this);
				}
				break;
			case R.id.bookmark:
				if(EventUtil.userHasBookmarked(event.id)){
					EventUtil.setNotBookmarked(event.id);
					isBookmarked = false;
					setBookmarkedButtonState();
					Internet.unincrementEventAttendance(this.event.id);
					this.event.numAttendees--;
					numGoing.setText(getString(R.string.numGoing, this.event.numAttendees));
					Data.eventForID.put(this.event.id, this.event);
					logFirebaseEvent("unbookmarked", event.title);
				} else {
					EventUtil.setBookmarked(event.id);
					isBookmarked = true;
					setBookmarkedButtonState();
					Internet.incrementEventAttendance(this.event.id);
					this.event.numAttendees++;
					numGoing.setText(getString(R.string.numGoing, this.event.numAttendees));
					Data.eventForID.put(this.event.id, this.event);
					logFirebaseEvent("bookmarked", event.title);
				}
				EventBusUtils.SINGLETON.post(new EventBusUtils.NotificationUpdate());
				break;
		}
	}

	private void setBookmarkedButtonState(){
		if(isBookmarked){
			bookmarkedButton.setTextAppearance(R.style.mainButtonSelected);
			bookmarkedButton.setBackgroundResource(R.drawable.bg_round_button_red);
			bookmarkedButton.setText(R.string.button_bookmarked);
		} else {
			bookmarkedButton.setTextAppearance(R.style.mainButton);
			bookmarkedButton.setBackgroundResource(R.drawable.bg_round_button_white);
			bookmarkedButton.setText(R.string.button_bookmark);
		}
	}

	private void logFirebaseEvent(String event, String eventName) {
		Bundle bundle = new Bundle();
		bundle.putString("eventName", eventName);
		firebaseAnalytics.logEvent(event, bundle);
		firebaseAnalytics.setAnalyticsCollectionEnabled(true);
	}


	@Override
	public void eventUpdate(List<Event> e) {
		String eventString = event.toString();
		List<Event> findEventRes = e.stream().filter((val) -> val.toString().equalsIgnoreCase(eventString)).collect(Collectors.toList());
		List<Event> findEventIDRes = e.stream().filter((val) -> val.id == event.id).collect(Collectors.toList());
		if(findEventIDRes.size() > 0 && findEventRes.size() == 0){
			Event newEvent = findEventIDRes.get(0);
			this.event = newEvent;
			this.configure(newEvent);
		}
		organization.setText(Data.organizationForID.containsKey(event.organizerID) ? Data.organizationForID.get(event.organizerID).name : "No organization available");
	}

	@Override
	public void orgUpdate(List<Organization> o) {

	}

	@Override
	public void tagUpdate(List<String> t) {

	}

	@Override
	public void singleEventUpdate(Event e) {
		if(this.event.id == e.id){
			this.configure(e);
			EventBusUtils.SINGLETON.post(new EventBusUtils.NotificationUpdate());
		}
	}

	@Override
	public void onStop(){
		SettingsUtil.SINGLETON.doSave();
		super.onStop();
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
