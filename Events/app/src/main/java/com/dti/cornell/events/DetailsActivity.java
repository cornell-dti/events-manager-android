package com.dti.cornell.events;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by jaggerbrulato on 2/27/18.
 */

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnTouchListener
{

	private static final int MAP_ZOOM = 16;

	ConstraintLayout _view;
	ConstraintLayout _root;
	private int _xDelta;
	private float tagX;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
		_view = findViewById(R.id.tagsLayout);
		this.tagX = _view.getX();

//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(150, 50);
//        layoutParams.leftMargin = 50;
//        layoutParams.topMargin = 50;
//        layoutParams.bottomMargin = -250;
//        layoutParams.rightMargin = -250;
//        _view.setLayoutParams(layoutParams);

		_view.setOnTouchListener(this);
		_root = findViewById(R.id.tagsLayout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void findViews()
	{
		MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
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


	public boolean onTouch(View view, MotionEvent event)
	{
		final int X = (int) event.getRawX();
		switch (event.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_DOWN:
				ConstraintLayout.LayoutParams lParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
				_xDelta = X - lParams.leftMargin;
				break;
			case MotionEvent.ACTION_UP:
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				break;
			case MotionEvent.ACTION_POINTER_UP:
				break;
			case MotionEvent.ACTION_MOVE:
				ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
				layoutParams.leftMargin = X - _xDelta;
				view.setLayoutParams(layoutParams);
//                int move = X - (_xDelta / 50);
//                _root.setX(tagX + move);
				break;
		}
		return true;
	}

}
