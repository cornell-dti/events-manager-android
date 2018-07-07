package com.dti.cornell.events;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.dti.cornell.events.utils.EventUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

/**
 * Created by jaggerbrulato on 2/27/18.
 */

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback
{
	private static final String EVENT_KEY = "event";
	private ImageView image;
	private TextView title;
	private TextView description;
	private TextView time;
	private TextView numGoing;
	private TextView organization;
	private TextView location;
	private Event event;

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
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		findViews();

		//get the event
		event = Event.fromString(getIntent().getExtras().getString(EVENT_KEY));
		configure(event);
	}

	private void findViews()
	{
		image = findViewById(R.id.image);
		title = findViewById(R.id.title);
		description = findViewById(R.id.description);
		time = findViewById(R.id.time);
		numGoing = findViewById(R.id.numGoing);
		organization = findViewById(R.id.organization);
		location = findViewById(R.id.location);
		MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		RecyclerView tagRecycler = findViewById(R.id.tagRecycler);
		TagAdapter adapter = new TagAdapter(this, EventUtils.getTags());
		tagRecycler.setAdapter(adapter);
		int spacing = getResources().getDimensionPixelSize(R.dimen.spacing_l);
		tagRecycler.addItemDecoration(new SpacingItemDecoration(spacing, 0));
	}

	private void configure(Event event)
	{
		title.setText(event.title);
		description.setText(event.description);
		time.setText(event.startTime.toString("eeee, MMMM d 'at' h:mm a"));
		numGoing.setText(getString(R.string.numGoing, event.attendees));
		organization.setText(event.organizerName);
		location.setText(event.location);
	}

	/**
	 * Loads {@link Event#googlePlaceId} and longitude onto the map, adds a marker.
	 *
	 * @param map {@inheritDoc}
	 */
	@Override
	public void onMapReady(GoogleMap map)
	{
//        LatLng position = new LatLng(event.latitude, event.longitude);
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, MAP_ZOOM));
//        map.addMarker(new MarkerOptions().position(position).title(event.caption));
	}

}
