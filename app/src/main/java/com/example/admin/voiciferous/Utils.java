package com.example.admin.voiciferous;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 7/8/2017.
 */

public final class Utils {

    private static final String GOOGLE_BASE_SEARCH_REQUEST_URL = "https://www.googleapis.com/customsearch/v1?key=AIzaSyCQZg_RfycP2ZzF6yCPc5k7ugXqs1L6rmg&cx=001324367313203204672:b6hagt4hjyo";
    private static final String YOUTUBE_BASE_SEARCH_REQUEST_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&maxResults=10&key=AIzaSyCQZg_RfycP2ZzF6yCPc5k7ugXqs1L6rmg";

    private static final int GOOGLE = 0;
    private static final int YOUTUBE = 1;

    /** Tag for the log messages */
    private static final String LOG_TAG = Utils.class.getSimpleName();

    // switch between different search engines
    private static int searchEngineType = YOUTUBE;

    // empty constructor
    private Utils() {
    }

    public static List<SearchResult> fetchSearchResultData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;



        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem closing input stream.", e);
        }

        // Extract relevant fields from the JSON response and create a {@link List<Earthquake>} object
        List<SearchResult> searchResultList = extractSearchResultsFromJson(jsonResponse);

        // Return the (@link List<Earthquake>}
        return searchResultList;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "ERROR with creating URL", e);
        }

        return url;
    }


    /**
     * Return a list of {@link SearchResult} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<SearchResult> extractSearchResultsFromJson(String searchResultJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(searchResultJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding search results to
        List<SearchResult> searchResultList = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(searchResultJSON);

            if (searchEngineType == GOOGLE) {
                // Extract the JSONArray associated with the key called "items",
                // which represents a list of search result items
                JSONArray searchResultArray = baseJsonResponse.getJSONArray("items");

                // For each search result in the searchResultArray, create an {@link SearchResult} object
                for (int i = 0; i < searchResultArray.length(); i++) {
                    // Get search result JSONObject at position i
                    JSONObject currentSearchResult = searchResultArray.getJSONObject(i);

                    // Get properties JSONObject
                    String title = currentSearchResult.getString("title");

                    // Extract the value for the key called "mag"
                    String url = currentSearchResult.getString("link");

                    // Extract "place" for the location
                    String snippet = currentSearchResult.getString("snippet");

                    // Create SearchResult java object from title, url, and snippet
                    SearchResult searchResult = new SearchResult(title, url, snippet);

                    // Add earthquake to list of earthquakes
                    searchResultList.add(searchResult);
                }

            } else if (searchEngineType == YOUTUBE) {
                // Extract the JSONArray associated with the key called "items",
                // which represents a list of search result items
                JSONArray searchResultArray = baseJsonResponse.getJSONArray("items");

                // For each search result in the searchResultArray, create an {@link SearchResult} object
                for (int i = 0; i < searchResultArray.length(); i++) {
                    // Get search result JSONObject at position i
                    JSONObject currentSearchResult = searchResultArray.getJSONObject(i);

                    // Extract the value for the key "videoId"
                    String videoId = currentSearchResult.getJSONObject("id").getString("videoId");

                    // this snippet is not the same as the description, but it does
                    // contain the search title and description
                    JSONObject searchSnippet = currentSearchResult.getJSONObject("snippet");

                    // Extract the value for the key called "title"
                    String title = searchSnippet.getString("title");

                    // Extract the value for the key called "description"
                    String description = searchSnippet.getString("description");

                    // Put together the youtube URL
                    String url = "https://www.youtube.com/watch?v=" + videoId;

                    // Create SearchResult java object from title, url, and snippet
                    SearchResult searchResult = new SearchResult(title, url, description);

                    // Add earthquake to list of earthquakes
                    searchResultList.add(searchResult);
                }
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the search JSON results", e);
        }

        // Return the list of earthquakes
        return searchResultList;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response
     */
    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse="";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /*milliseconds*/);
            urlConnection.setConnectTimeout(15000 /*milliseconds*/);
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
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

    public static String createSearchRequestUrl(List<String> searchTermList) {

        String searchResultUrl = "";
        if (searchEngineType == GOOGLE) {
            searchResultUrl = GOOGLE_BASE_SEARCH_REQUEST_URL + "&q=";

            String queryString = "";

            // get all the key words and concat them together..
            queryString = searchTermList.get(0);
            for (int m = 1; m < searchTermList.size(); m++) {
                queryString = queryString + " " + searchTermList.get(m);
            }

            searchResultUrl = searchResultUrl + "{" + queryString + "}&alt=json";
        }
        else if (searchEngineType == YOUTUBE) {
            searchResultUrl = YOUTUBE_BASE_SEARCH_REQUEST_URL +"&q=";

            String queryString = "";

            // get all the key words and concat them together..
            queryString = searchTermList.get(0);
            for (int m = 1; m < searchTermList.size(); m++) {
                queryString = queryString + "+" + searchTermList.get(m);
            }

            searchResultUrl = searchResultUrl + queryString;

        }

        return searchResultUrl;
    }


}
