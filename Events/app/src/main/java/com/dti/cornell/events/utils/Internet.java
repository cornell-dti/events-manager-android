package com.dti.cornell.events.utils;

import android.os.AsyncTask;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.common.io.CharStreams;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jaggerbrulato on 3/20/18.
 */

public class Internet {
    private static final String TAG = Internet.class.getSimpleName();

    /**
     * Retrieves data from {@link #URL_STRING} and calls {@link #CALLBACK} with it.
     * Must be private static class to prevent memory leaks from inner classes.
     * Extends AsyncTask since going on internet may lag app & should be done in bg.
     *
     * @see #get(String, Callback)
     * @see <a href="https://stackoverflow.com/questions/44309241/warning-this-asynctask-class-should-be-static-or-leaks-might-occur">StackOverFlow</a>
     */
    private static class GET extends AsyncTask<Void, Void, String>
    {
        private final String URL_STRING;
        private final Callback<String> CALLBACK;

        private GET(String urlString, Callback<String> callback)
        {
            URL_STRING = urlString;
            CALLBACK = callback;
        }
        @Override
        @WorkerThread
        protected String doInBackground(Void... params)
        {
            try
            {
                URL url = new URL(URL_STRING);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                String body = CharStreams.toString(new InputStreamReader(connection.getInputStream()));
                connection.disconnect();
                Log.i(TAG, "GET from database succeeded");
                return body;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.e(TAG, "GET from database failed :(");
            }
            return "";
        }

        @Override
        protected void onPostExecute(String body)
        {
            CALLBACK.execute(body);
        }
    }

    /**
     * Connects to the website given, then calls {@link Callback#execute(Object)} with the output
     * received from the website as the String parameter.
     * Identical to a GET request.
     *
     * @param urlString Link to the website
     * @param callback Contains method to execute once the website responds
     */
    private static void get(final String urlString, final Callback<String> callback)
    {
        new GET(urlString, callback).execute();
    }
}
