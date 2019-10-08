package com.dti.cornell.events;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.models.Organization;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.EventUtil;
import com.dti.cornell.events.utils.Internet;
import com.dti.cornell.events.utils.RecyclerUtil;
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
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ShareCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by jaggerbrulato on 2/27/18.
 */

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, Data.DataUpdateListener
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
	private boolean isBookmarked;
	public ImageView image;
	private ConstraintLayout imageAndButtons;

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
		setStatusBarTranslucent();
		Data.registerListener(this);

		Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
		this.placesClient = Places.createClient(this);

		//get the event
		event = Event.fromString(getIntent().getExtras().getString(EVENT_KEY));
		findViews();

		configure(event);

		organization.setPaintFlags(organization.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

		firebaseAnalytics = FirebaseAnalytics.getInstance(this);
		logFirebaseEvent("eventViewed", event.title);
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
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.share).setOnClickListener(this);

		MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		tagRecycler = findViewById(R.id.tagRecycler);
		RecyclerUtil.addTagHorizontalSpacing(tagRecycler);

		bookmarkedButton = findViewById(R.id.bookmark);
		imageAndButtons = findViewById(R.id.constraintLayout2);

	}

	@Override
	public void onStart(){
		super.onStart();
		setBookmarkedButtonState();
	}

	private void configure(Event event)
	{
		title.setText(event.title);
		description.setText(event.description);
		time.setText(event.startTime.toString("EEEE, MMMM d 'at' h:mm a"));
		numGoing.setText(getString(R.string.numGoing, this.event.numAttendees));
		if(!Data.organizationForID.containsKey(event.organizerID)){
			Data.getData();
		}
		organization.setText(Data.organizationForID.containsKey(event.organizerID) ? Data.organizationForID.get(event.organizerID).name : "No organization available");
		location.setText(event.location);

		TagAdapter adapter = new TagAdapter(this, event.tagIDs, false);
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

		configureDescription();

		image = findViewById(R.id.image);

		imageAndButtons.setY(60);

//		Internet.getImageForEvent(event, image);
		Internet.getImageForEventStopProgress(event, image, imageLoadingBar);
		startMap();
	}

	private void configureDescription()
	{
		description.setMaxLines(DESCRIPTION_MAX_LINES);
		//find out how many lines the description text will be
		description.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
		{
			@Override
			public boolean onPreDraw()
			{
				if (more.getVisibility() != View.VISIBLE)
					return true;

				int lineCount = description.getLayout().getLineCount();
				if (lineCount >= DESCRIPTION_MAX_LINES)
				{
					more.setVisibility(View.VISIBLE);
					moreButtonGradient.setVisibility(View.VISIBLE);
				}
				else
				{
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
			map.addMarker(new MarkerOptions().position(position).title(event.location));
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
				description.setMaxLines(Integer.MAX_VALUE);
				more.setVisibility(View.GONE);
				moreButtonGradient.setVisibility(View.GONE);
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
		organization.setText(Data.organizationForID.containsKey(event.organizerID) ? Data.organizationForID.get(event.organizerID).name : "No organization available");
	}

	@Override
	public void orgUpdate(List<Organization> o) {

	}

	@Override
	public void tagUpdate(List<String> t) {

	}
}
