package com.example.micha.newsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DataQuery {
    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = DataQuery.class.getSimpleName();

    /**
     * Create a private constructor
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private DataQuery() {
    }

    /**
     * Query the Guardian API and return a list of {@link News} objects
     */
    public static List<News> fetchNewsData(String requestUrl) throws JSONException {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //   Create URL object
        URL url = createUrl(requestUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.~*~*", e);
        }
        // Extract relevant fields from the JSON response and create a list of {@link News}
        List<News> news = parseJsonData(jsonResponse);
        // Return the list of {@link News}
        return news;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "*^*^Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            //(better way of doing this avail.look up..)
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "~~~Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "~~~Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies that an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<News> parseJsonData(String JsonResponse) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(JsonResponse)) {
            return null;
        }

        List<News> newsList = new ArrayList<>();

        try {
            //Create JSONObject from the JsonResponse String
            JSONObject responseObject = new JSONObject(JsonResponse);
            //Extract the JSONObject "response"
            JSONObject response = responseObject.getJSONObject("response");
            //Extract JSONArray results which hold individual news stories needed
            JSONArray resultsArray = response.getJSONArray("results");
            //
            //For each news array create an {@link News} object
            for (int i = 0; i < resultsArray.length(); i++) {
                //Retrieve JSONObject at (i) position in list
                JSONObject resultObject = resultsArray.getJSONObject(i);
                //Extract info at String to return type of News (world etc)
                String section = resultObject.getString("sectionName");
                //Extract date and format to useful string for user display
                String date = resultObject.getString("webPublicationDate");
                date = formatDate(date);
                //Extract info at String webTitle to get headline and AUTHOR (if available, will be displayed)
                String title = resultObject.getString("webTitle");
                //get url for individual story so when clicked will take user to web url
                String url = resultObject.getString("webUrl");
                //
                JSONArray tagArray = resultObject.getJSONArray("tags");

                String authorName = "";
                if (tagArray.length() == 0) {
                    authorName = null;
                } else {
                    for (int j = 0; j < tagArray.length(); j++) {
                        JSONObject jsonObject = tagArray.getJSONObject(j);
                        authorName = jsonObject.getString("webTitle");
                    }
                }

                JSONObject fieldObject = resultObject.getJSONObject("fields");
//                JSONObject thumb = resultObject.getJSONObject("thumbnail");
                String thumbnail = fieldObject.getString("thumbnail");


//                    thumbnail = fieldObject.getString("thumbnail");


                newsList.add(new News(section, date, title, url, authorName, getBitmap(thumbnail)));
            }
        } catch (JSONException e) {
            Log.e("DataQuery", "Error parsing JSON response", e);
        }
        return newsList;
    }
    private static Bitmap getBitmap(String originalUrl) {
        Bitmap bitmap = null;
        if(!"".equals(originalUrl)) {
            InputStream inputStream = null;
            try {
                inputStream = new URL(originalUrl).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
    //Format the date for proper display
    private static String formatDate(String dateFormat) {
        String jsonDate = "yyyy-mm-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(jsonDate, Locale.getDefault());
        try {
            Date parsedDate = simpleDateFormat.parse(dateFormat);
            String parsedDatePattern = "MMM dd yyyy";
            SimpleDateFormat formatJsonDate = new SimpleDateFormat(parsedDatePattern, Locale.getDefault());
            return formatJsonDate.format(parsedDate);
        } catch (ParseException e) {
            Log.e("DataQuery", "~*&~*&~*&Error parsing JSON date: ", e);
            return "";
        }
    }
}
//    String thumbnail = "";
//                if(results.getJSONObject(i).has("fields")) {
//                        JSONObject fields = currentArticle.getJSONObject("fields");
//                        thumbnail = fields.getString("thumbnail");
//                        }