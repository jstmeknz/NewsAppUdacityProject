package com.example.micha.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    public static final String LOG_TAG = MainActivity.class.getName();
    private static final int NEWS_LOADER_ID = 1;
    private static final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search?";
    Context context;
    //    search?from-date=2018-09-09&q=extreme%20weather%2C%20natural%20disasters%2C%20hurricane%2C%20typhoon%2C%20earthquake%2C%20sinkhole%2C%20landslide%2C%20disease%2C%20climate%2C%20flood%2C%20tornado%2C%20wildfire%2C%20extinction%2C%20pollution%2C%20deforestation%2C%20magnetic%20pole%20shift%2C%20air%20quality%2C%20uv%20index&show-tags=contributor&show-fields=thumbnail&api-key=f45ca4ef-8077-4746-9a98-021e29ad633a"
    private NewsAdapter mAdapter;
    private TextView mEmptyStateTextView;

    //    private static final String SCHEME = "https";
//    private static final String AUTHORITY = "content.guardianapis.com";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //find list in xml layout
        ListView newsList = (ListView) findViewById(R.id.list);

        //view for displaying a lack of data to user
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsList.setEmptyView(mEmptyStateTextView);

        //create adapter with empty list as input
        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        //set adapter so data can populate in ui
        newsList.setAdapter(mAdapter);

        //set click listener for each news story at each individual url
        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News news = (News) mAdapter.getItem(position);

                //convert the string URL into URI in pass into intent constructor
                Uri newsUri = Uri.parse(news.getUrl());

                //create intent to view news story
                Intent webIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                //starts new activity with news uri
                startActivity(webIntent);
            }
        });

        //gets reference to ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //get details on current network connectivity
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        //if connected, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            //get reference to loader to interact with loader
            LoaderManager loaderManager = getLoaderManager();
            //Initialize the loader. Pass in the int ID constant defined above and pass in null for
            //the bundle. Pass in this activity for the loaderCallbacks parameter (which is valid
            //because this activity implements the loaderCallbacks interface.
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            //hides loading indicator, informs user of no connection
            View loadingIndicator = findViewById(R.id.loadingIndicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.network_bad_text);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        Log.i(LOG_TAG, "TEST: On Create Load called...~+~+~+");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String editTextEntry = sharedPrefs.getString(getString(R.string.edit_text_search_key), getString(R.string.edit_text_search_default));

        String orderBy = sharedPrefs.getString(getString(R.string.orderBy_key), getString(R.string.orderBy_default));

        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("q", editTextEntry);
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("show-fields", "thumbnail");

        uriBuilder.appendQueryParameter("api-key", "f45ca4ef-8077-4746-9a98-021e29ad633a");
        //create new loader for given url
        Log.i(LOG_TAG, "%@%@%@%@%@%@% TEST" + uriBuilder);
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsList) {

        View loadingIndicator = findViewById(R.id.loadingIndicator);
        loadingIndicator.setVisibility(View.GONE);
        Log.i(LOG_TAG, "TEST: On Load Finished called...~@~@~@~");

        mEmptyStateTextView.setText(R.string.News_unavailable);

        //clear previous data
        mAdapter.clear();

        //if valid data exists it will be loaded into the adapter
        //+triggers ListView xml to update
        if (newsList != null && !newsList.isEmpty()) {
            mAdapter.addAll(newsList);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        //loader reset to data clears
        mAdapter.clear();
    }


}




