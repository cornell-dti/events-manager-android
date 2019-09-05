package com.dti.cornell.events.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.WorkerThread;
import android.support.design.widget.Snackbar;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dti.cornell.events.DetailsActivity;
import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.models.Location;
import com.dti.cornell.events.models.Organization;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jaggerbrulato on 3/20/18.
 */

public class Internet {
	private static final DateTimeZone timeZone = DateTimeZone.forID("EST");
	public static final String TIME_FORMAT = "yyyyMMddHHmmss";
	private static final String DATABASE = "http://cuevents-app.herokuapp.com/";
    private static final String TAG = Internet.class.getSimpleName();

	private static RequestQueue requestQueue;
	private static ImageLoader imageLoader;
	private static final Response.ErrorListener ERROR_LISTENER = new Response.ErrorListener()
	{
		@Override
		public void onErrorResponse(VolleyError error)
		{
			Log.e(TAG, "onErrorResponse: ", error);
		}
	};

	public static String addTToTimestamp(String timestamp){
		return timestamp.substring(0,8) + "T" + timestamp.substring(8, timestamp.length());
	}

	public static void createRequestQueue(Context context)
	{
		if (requestQueue != null)
			return;

		requestQueue = Volley.newRequestQueue(context);
		imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache()
		{
			private final LruCache<String, Bitmap> cache = new LruCache<>(20);

			@Override
			public Bitmap getBitmap(String url)
			{
				return cache.get(url);
			}

			@Override
			public void putBitmap(String url, Bitmap bitmap)
			{
				cache.put(url, bitmap);
			}
		});
	}

	public static void downloadImage(String url, final ImageView image)
	{
		imageLoader.get(url, new ImageLoader.ImageListener()
		{
			@Override
			public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate)
			{
				image.setImageBitmap(response.getBitmap());
			}
			@Override
			public void onErrorResponse(VolleyError error)
			{
				ERROR_LISTENER.onErrorResponse(error);
			}
		});
	}

	public static void getEventFeed()
	{
		DateTime startTime = new DateTime(timeZone);
		final DateTime endTime = startTime.plusDays(Data.NUM_DAYS_IN_FEED);
		String timestamp = addTToTimestamp(SettingsUtil.SINGLETON.getTimestamp());
		String url = DATABASE + "feed/events/?timestamp=" + timestamp + "&start=" +
				addTToTimestamp(startTime.toString(TIME_FORMAT)) + "&end=" + addTToTimestamp(endTime.toString(TIME_FORMAT));
		Log.i("INTERNET", addTToTimestamp(startTime.toString(TIME_FORMAT)));
		Log.i("INTERNET", url);

		JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
				"http://cuevents-app.herokuapp.com/feed/events/?timestamp=2017-02-19T01:43:40.753131-05:00&start=19990219T014510&end=20210321T014510",
				null, new Response.Listener<JSONObject>()
		{
			@Override
			public void onResponse(JSONObject response)
			{
				try
				{
					Log.e("INTERNET", response.toString());
					JSONArray events = response.getJSONArray("events");
					String newTimestamp = response.getString("timestamp");

//					Map<Integer, Event> savedEvents = SettingsUtil.SINGLETON.getEvents();
//					for (int i = 0; i < updated.length(); i++)
//					{
//						Event event = Event.fromJSON(updated.getJSONObject(i));
//						savedEvents.put(event.id, event);
//					}
//					for (int i = 0; i < deleted.length(); i++)
//						savedEvents.remove(deleted.getInt(i));

					Map<Integer, Event> allEvents = new HashMap<>();

					if(events.length() > 0){
						for(int i = 0; i < events.length(); i++){
							JSONObject jsonEvent = events.getJSONObject(i);
							Event event = EventUtil.eventFromJSON(jsonEvent);
							if(event == null){
								Log.i("INTERNET", jsonEvent.toString());
							}
							allEvents.put(event.id, event);
						}
					}




					SettingsUtil.SINGLETON.setEvents(allEvents);

					for(Event e : allEvents.values()){
						Data.eventForID.put(e.id, e);
					}
					Data.emitEventUpdate();
					Log.i("INTERNET SINGLETON", SettingsUtil.SINGLETON.getEvents().toString());
					Log.i("INTERNET SINGLETON", Data.events().toString());


					SettingsUtil.SINGLETON.setTimestamp(newTimestamp);
				}
				catch (JSONException e)
				{
					Log.e(TAG, "getEventFeed: ", e);
				}
			}
		}, ERROR_LISTENER);

		requestQueue.add(request);
	}

	public static void getOrganizations()
	{
		String timestamp = addTToTimestamp(SettingsUtil.SINGLETON.getTimestamp());
		String url = DATABASE + "feed/org/timestamp=" + timestamp;
		JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>()
		{
			@Override
			public void onResponse(JSONObject response)
			{
				try
				{
					JSONArray updated = response.getJSONArray("updated");
					JSONArray deleted = response.getJSONArray("deleted");
					String newTimestamp = response.getString("timestamp");

					Map<Integer, Organization> savedOrgs = SettingsUtil.SINGLETON.getOrganizations();

					for (int i = 0; i < updated.length(); i++)
					{
						Organization org = Organization.fromJSON(updated.getJSONObject(i));
						savedOrgs.put(org.id, org);
					}
					for (int i = 0; i < deleted.length(); i++)
						savedOrgs.remove(deleted.getInt(i));
					SettingsUtil.SINGLETON.setOrganizations(savedOrgs);

					SettingsUtil.SINGLETON.setTimestamp(newTimestamp);
				}
				catch (JSONException e)
				{
					Log.e(TAG, "getOrganizations: ", e);
				}
			}
		}, ERROR_LISTENER);

		requestQueue.add(request);
	}

	// IMAGE HANDLER

	/**
	 * Try to download image from the internet to the given {@link ImageView}.
	 *
	 * @param org Organization to get image for
	 * @param imageView View to display image
	 */
	public static void getImageForOrganization(final Organization org, final ImageView imageView)
	{
		Log.e("INTERNET DATA HERE!!!!!", DATABASE + "org/" + org.id + "/");
		JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
				DATABASE + "org/" + org.id + "/",
				null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				JSONArray photos;
				String orgProfilePicURL = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/1024px-No_image_available.svg.png";
				try {
					photos = response.getJSONArray("photo");
					for(int i = 0; i < photos.length(); i++){
						JSONObject obj = photos.getJSONObject(i);
						int photoID = obj.getInt("id");
						if(!Data.mediaForID.containsKey(photoID)){
							Data.mediaForID.put(photoID, obj.getString("link"));
						}
						if(photoID == org.pictureID){
							orgProfilePicURL = obj.getString("link");
						}
					}
					final String orgProfilePicURLFinal = orgProfilePicURL;
					if(Data.bitmapForURL.containsKey(orgProfilePicURLFinal)){
						imageView.setImageBitmap(Data.bitmapForURL.get(orgProfilePicURLFinal));
						return;
					}
					new GetImage(orgProfilePicURLFinal, new Callback<Bitmap>()
					{
						@Override
						public void execute(Bitmap bitmap)
						{
							if (bitmap == null) {
								Log.e(TAG, "Image could not be loaded: " + orgProfilePicURLFinal);
							} else{
								imageView.setImageBitmap(bitmap);
								Data.bitmapForURL.put(orgProfilePicURLFinal, bitmap);
							}
						}
					}, "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/1024px-No_image_available.svg.png").execute();
				} catch (JSONException e) {
					Log.e(TAG, "Could not get orgProfilePic");
					e.printStackTrace();
				}
			}
		}, ERROR_LISTENER);
		requestQueue.add(request);
	}

	/**
	 * Try to download image from the internet to the given {@link ImageView}.
	 *
	 * @param event Event to get image for
	 * @param imageView View to display image
	 */
	public static void getImageForEvent(final Event event, final ImageView imageView)
	{
		if(Data.bitmapForURL.containsKey(event.pictureID)){
			imageView.setImageBitmap(Data.bitmapForURL.get(event.pictureID));
			return;
		}
		new GetImage(event.pictureID, new Callback<Bitmap>()
		{
			@Override
			public void execute(Bitmap bitmap)
			{
				if (bitmap == null){
					Log.e(TAG, "Image could not be loaded: " + event.pictureID);
				}
				else{
					imageView.setImageBitmap(bitmap);
					Data.bitmapForURL.put(event.pictureID, bitmap);
				}
			}
		}, "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/1024px-No_image_available.svg.png").execute();
	}

	/**
	 * Try to download image from the internet to the given {@link ImageView}.
	 *
	 * @param event Event to get image for
	 * @param imageView View to display image
	 */
	public static void getImageForEventStopProgress(final Event event, final ImageView imageView, ProgressBar progress1)
	{
		final ProgressBar progress = progress1;
		if(Data.bitmapForURL.containsKey(event.pictureID)){
			imageView.setImageBitmap(Data.bitmapForURL.get(event.pictureID));
			progress.setVisibility(View.INVISIBLE);
			return;
		}
		new GetImage(event.pictureID, new Callback<Bitmap>()
		{
			@Override
			public void execute(Bitmap bitmap)
			{
				if (bitmap == null){
					Log.e(TAG, "Image could not be loaded: " + event.pictureID);
				}
				else{
					imageView.setImageBitmap(bitmap);
					Data.bitmapForURL.put(event.pictureID, bitmap);
				}
				progress.setVisibility(View.INVISIBLE);
			}
		}, "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/1024px-No_image_available.svg.png").execute();
	}

	private static class GetImage extends AsyncTask<Void, Void, Bitmap>
	{
		private final String urlString;
		private final String backupURL;
		private final Callback<Bitmap> callback;

		public GetImage(String urlString, Callback<Bitmap> callback, String backupURL){
			this.urlString = urlString;
			this.callback = callback;
			this.backupURL = backupURL;
		}


		@Override
		@WorkerThread
		protected Bitmap doInBackground(Void... params) {
			try {
				Log.e("GETIMAGE at URL: ", urlString);
				return BitmapFactory.decodeStream(new URL(urlString).openStream());
			} catch (Exception e) {
				Log.d(TAG, "Get image from url " + urlString + " failed");
				try {
					return BitmapFactory.decodeStream(new URL(backupURL).openStream());
				} catch (IOException e1) {
					e1.printStackTrace();
					return null;
				}
			}
		}

		@Override
		protected void onPostExecute(Bitmap body)
		{
			callback.execute(body);
		}
	}

	// IMAGE HANDLER END

	// LOCATION HANDLER BEGIN


	private static class GetLocation extends AsyncTask<Void, Void, Location>
	{
		private final String placeID;
		private final Callback<Location> callback;

		public GetLocation(String placeID, Callback<Location> callback){
			this.placeID = placeID;
			this.callback = callback;
		}


		@Override
		@WorkerThread
		protected Location doInBackground(Void... params) {
			try {
				// TODO: GET
				return null;
			} catch (Exception e) {
				Log.d(TAG, "Get location from placeID " + placeID + " failed");
				return null;
			}
		}

		@Override
		protected void onPostExecute(Location body)
		{
			callback.execute(body);
		}
	}

	// LOCATION HANDLER END
}
