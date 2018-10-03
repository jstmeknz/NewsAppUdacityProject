package com.example.micha.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;

import java.util.List;

/**
 * Loads a list of News Stories by using AsyncTask (loader) to perform a network request to
 * given URL
 */
public class NewsLoader extends AsyncTaskLoader<List<News>> {

    //Tag for LOG messages
    private static final String LOG_TAG = NewsLoader.class.getName();

    //Query URL
    private String mUrl;

    /**
     * @param context of the activity
     * @param url     to load the data from
     */
    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "TEST: ONSTARTLOADING^~^~");
        forceLoad();
    }

    //Background thread
    @Override
    public List<News> loadInBackground() {
        Log.i(LOG_TAG, "TEST: LOADINBACKGROUND~*~*~");
        if (mUrl == null) {
            return null;
        }

        //perform request, parse response and extract news
        List<News> newsList = null;
        try {
            newsList = DataQuery.fetchNewsData(mUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsList;
    }
}
