package com.dti.cornell.events.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.models.Organization;
import com.dti.cornell.events.models.Tag;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jaggerbrulato on 3/20/18.
 */

public class Internet {
	private static final DateTimeZone timeZone = DateTimeZone.forID("EST");
	public static final String TIME_FORMAT = "yyyyMMddTHHmmss";
	public static final String TIME_FORMAT_NO_T = "yyyyMMddHHmmss";
	private static final String DATABASE = "http://cuevents-app.herokuapp.com/app/";
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
		DateTime endTime = startTime.plusDays(Data.NUM_DAYS_IN_FEED);
		String timestamp = SettingsUtil.SINGLETON.getTimestamp();
		String url = DATABASE + "feed/events/timestamp=" + timestamp + "&start=" +
				addTToTimestamp(startTime.toString(TIME_FORMAT_NO_T)) + "&end=" + addTToTimestamp(endTime.toString(TIME_FORMAT_NO_T));
		Log.e("getEventFeed", url);
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

					Map<Integer, Event> savedEvents = SettingsUtil.SINGLETON.getEvents();
					for (int i = 0; i < updated.length(); i++)
					{
						Event event = Event.fromJSON(updated.getJSONObject(i));
						savedEvents.put(event.id, event);
					}
					for (int i = 0; i < deleted.length(); i++){
						savedEvents.remove(deleted.getInt(i));
						EventUtil.setNotBookmarked(deleted.getInt(i));
					}
					SettingsUtil.SINGLETON.setEvents(savedEvents);
					SettingsUtil.SINGLETON.setTimestamp(newTimestamp);
				}
				catch (JSONException e)
				{
					Log.e(TAG, "getEventFeed: ", e);
				}
			}
		}, ERROR_LISTENER) { //no semicolon or coma
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				Log.e("getEventFeedToken", SettingsUtil.SINGLETON.getToken() + ".TOK");
				params.put("Authorization", "Token " + SettingsUtil.SINGLETON.getToken());
				return params;
			}
		};


		requestQueue.add(request);
	}

	public static void getOrganizations()
	{
		String timestamp = SettingsUtil.SINGLETON.getTimestamp();
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
					for (int i = 0; i < deleted.length(); i++){
						savedOrgs.remove(deleted.getInt(i));
						OrganizationUtil.unfollowOrganization(deleted.getInt(i));
					}
					SettingsUtil.SINGLETON.setOrganizations(savedOrgs);

					SettingsUtil.SINGLETON.setTimestamp(newTimestamp);
				}
				catch (JSONException e)
				{
					Log.e(TAG, "getOrganizations: ", e);
				}
			}
		}, ERROR_LISTENER) { //no semicolon or coma
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				Log.e("getOrganizations Token", SettingsUtil.SINGLETON.getToken() + ".TOK");
				params.put("Authorization", "Token " + SettingsUtil.SINGLETON.getToken());
				return params;
			}
		};

		requestQueue.add(request);
	}

	public static void getAllTags(){
		String url = DATABASE + "tag/all/";
		JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>()
		{
			@Override
			public void onResponse(JSONObject response)
			{
				try
				{
					JSONArray tags = response.toJSONArray(response.names());

					for (int i = 0; i < tags.length(); i++){
						JSONObject tag = tags.getJSONObject(i);
						Integer tagID = tag.getInt("pk");
						String tagName = tag.getString("name");
						TagUtil.allTags.add(new Tag(tagID, tagName));
					}
				}
				catch (JSONException e)
				{
					Log.e(TAG, "getTags: ", e);
				}
			}
		}, ERROR_LISTENER) { //no semicolon or coma
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				Log.e("getTags Token", SettingsUtil.SINGLETON.getToken() + ".TOK");
				params.put("Authorization", "Token " + SettingsUtil.SINGLETON.getToken());
				return params;
			}
		};

		requestQueue.add(request);
	}

	public static void resetToken(String googleAccountToken){
		String url = DATABASE + "reset_token/" + googleAccountToken +
				"/Clny9jLW4lf2gAvOZ27fJYnPJNyEPqKiMfmkAkFgXzksjoTEkeL9o5M4lHNbyrUCsVnglEv89pTcs" +
				"1o787xt17KF6HZaHuqDMEjNPfpDdJBeB8nHbFLqJuqGTsEqT5NAai7UGJxgSPcszLTmpMT6PhRY7VATm" +
				"Ecbbqvf3McjsvfZ0Um9UHUVzCwpnj67n6rSbiy4kZm8/";
		JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				SettingsUtil.SINGLETON.resetFirstRun();
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "resetTokenError", error);
				Log.e("resetTokenError", SettingsUtil.SINGLETON.getToken() + "<---");
				error.printStackTrace();
			}
		});

		requestQueue.add(request);
	}

	public static void setFirebaseToken(String googleAccountToken){
		String url = DATABASE + "generate_token/firebase_id?firebaseid=" + googleAccountToken;
		Log.e("googleToken: ",  googleAccountToken);
		JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					Log.e("Firebase Token", response.toString() + "<<<<<<");
					SettingsUtil.SINGLETON.setToken
							(response.getString("Token"));
					response.getJSONObject("Token");
				} catch (JSONException e) {
					Log.e(TAG, "getToken: ", e);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "setFireBaseTokenERROR", error);
				Log.e("setFireBaseToken", SettingsUtil.SINGLETON.getToken() + "<---");
				error.printStackTrace();
			}
		});

		requestQueue.add(request);
	}


	public static String addTToTimestamp(String timestamp){
		return timestamp.substring(0, 8) + "T" + timestamp.substring(8, timestamp.length());
	}
}
