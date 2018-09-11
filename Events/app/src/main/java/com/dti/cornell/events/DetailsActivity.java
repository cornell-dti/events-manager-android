package com.dti.cornell.events;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.models.Organization;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.EventUtil;
import com.dti.cornell.events.utils.OrganizationUtil;
import com.dti.cornell.events.utils.RecyclerUtil;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by jaggerbrulato on 2/27/18.
 */

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener
{
	private static final String TAG = DetailsActivity.class.getSimpleName();
	private static final String EVENT_KEY = "event";
	private ImageView image;
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
	private TextView interestedButton;
	private TextView goingButton;
	private boolean isInterested;
	private boolean isGoing;

	private static final int DESCRIPTION_MAX_LINES = 3;

	//map stuff
	private static final int MAP_ZOOM = 15;
	private GeoDataClient geoDataClient;
	@Nullable
	private String placeName;
	@Nullable
	private String placeAddress;

	public static void startWithEvent(Event event, Context context)
	{
		Intent intent = new Intent(context, DetailsActivity.class);
		intent.putExtra(EVENT_KEY, event.toString());
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		setStatusBarTranslucent();

		geoDataClient = Places.getGeoDataClient(this);

		//get the event
		event = Event.fromString(getIntent().getExtras().getString(EVENT_KEY));
		findViews();
		configure(event);
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
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.share).setOnClickListener(this);

		MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		tagRecycler = findViewById(R.id.tagRecycler);
		RecyclerUtil.addHorizontalSpacing(tagRecycler);

		interestedButton = findViewById(R.id.interested);
		goingButton = findViewById(R.id.going);
	}

	@Override
	public void onStart(){
		super.onStart();
		setInterestedButtonState();
		setGoingButtonState();
	}

	private void configure(Event event)
	{
		title.setText(event.title);
		description.setText(event.description);
		time.setText(event.startTime.toString("EEEE, MMMM d 'at' h:mm a"));
		numGoing.setText(getString(R.string.numGoing, event.participantIDs.size()));
		organization.setText(Data.organizationForID.get(event.organizerID).name);
		location.setText(event.location);

		TagAdapter adapter = new TagAdapter(this, event.tagIDs, false);
		tagRecycler.setAdapter(adapter);

		interestedButton.setOnClickListener(this);
		goingButton.setOnClickListener(this);
		isInterested = EventUtil.userIsInterested(event.id);
		isGoing = EventUtil.userIsGoing(event.id);


		configureDescription();
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
		geoDataClient.getPlaceById(event.googlePlaceID).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>()
		{
			@Override
			public void onComplete(@NonNull Task<PlaceBufferResponse> task)
			{
				if (!task.isSuccessful()) {
					Log.e(TAG, "onMapReady: place not found");
					return;
				}

				PlaceBufferResponse places = task.getResult();
				Place place = places.get(0);
				placeName = place.getName().toString();
				placeAddress = place.getAddress().toString();
				LatLng position = place.getLatLng();
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, MAP_ZOOM));
				map.addMarker(new MarkerOptions().position(position).title(event.location));
				places.release();
			}
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
				Log.i("DetailsActivity", "TODO share pressed");
				break;
			case R.id.more:
				description.setMaxLines(Integer.MAX_VALUE);
				more.setVisibility(View.GONE);
				moreButtonGradient.setVisibility(View.GONE);
				break;
			case R.id.organization:
				Organization organization = Data.organizationForID.get(event.organizerID);
				OrganizationActivity.startWithOrganization(organization, this);
				break;
			case R.id.interested:
				if(EventUtil.userIsInterested(event.id)){
					EventUtil.setNotInterested(event.id);
					isInterested = false;
					setInterestedButtonState();
				} else {
					EventUtil.setInterested(event.id);
					isInterested = true;
					setInterestedButtonState();
				}
				break;
			case R.id.going:
				if(EventUtil.userIsGoing(event.id)){
					EventUtil.setNotGoing(event.id);
					isGoing = false;
					setGoingButtonState();
				} else {
					EventUtil.setGoing(event.id);
					isGoing = true;
					setGoingButtonState();
				}
				break;
		}
	}

	public void setInterestedButtonState(){
		if(isInterested){
			interestedButton.setTextAppearance(R.style.mainButtonSelected);
			interestedButton.setBackgroundResource(R.drawable.bg_round_button_red);
			interestedButton.setText(R.string.button_interested);
		} else {
			interestedButton.setTextAppearance(R.style.mainButton);
			interestedButton.setBackgroundResource(R.drawable.bg_round_button_white);
			interestedButton.setText(R.string.button_interested);
		}
	}

	public void setGoingButtonState(){
		if(isGoing){
			goingButton.setTextAppearance(R.style.mainButtonSelected);
			goingButton.setBackgroundResource(R.drawable.bg_round_button_red);
			goingButton.setText(R.string.button_going);
		} else {
			goingButton.setTextAppearance(R.style.mainButton);
			goingButton.setBackgroundResource(R.drawable.bg_round_button_white);
			goingButton.setText(R.string.button_going);
		}
	}

}
