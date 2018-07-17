package com.dti.cornell.events;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.SpacingItemDecoration;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

/**
 * Created by jaggerbrulato on 2/27/18.
 */

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener
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
		setStatusBarTranslucent();

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
		location = findViewById(R.id.location);
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.share).setOnClickListener(this);

		MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		RecyclerView tagRecycler = findViewById(R.id.tagRecycler);
		TagAdapter adapter = new TagAdapter(this, event.tagIDs);
		tagRecycler.setAdapter(adapter);
		int spacing = getResources().getDimensionPixelSize(R.dimen.spacing_l);
		tagRecycler.addItemDecoration(new SpacingItemDecoration(spacing, 0));
	}

	private void configure(Event event)
	{
		title.setText(event.title);
		description.setText(event.description);
		time.setText(event.startTime.toString("EEEE, MMMM d 'at' h:mm a"));
		numGoing.setText(getString(R.string.numGoing, event.participantIDs.size()));
		organization.setText(Data.organizationForID.get(event.organizerID).name);
		location.setText(event.location);
	}

	/**
	 * Loads {@link Event#googlePlaceID} and longitude onto the map, adds a marker.
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
		}
	}
}
