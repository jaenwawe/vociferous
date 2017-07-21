package com.example.admin.voiciferous;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    String searchRequestUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<String> searchWordList = new ArrayList<String>();

        // Here is a hard coded example of the parsed results received from Watson
        searchWordList.add("movie");
        searchWordList.add("family");
        searchWordList.add("G-rated");
        searchWordList.add("cartoon");

/*
        Intent searchIntent = new Intent(Intent.ACTION_WEB_SEARCH);
        String keyword = searchWordList.get(1);
        searchIntent.putExtra(SearchManager.QUERY, keyword);
        if (searchIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(searchIntent);
        }
        */

        //  to generate appropriate custom search URL(s)
        searchRequestUrl = Utils.createSearchRequestUrl(searchWordList);

        // Kick off an {@link AsyncTask} to perform the network request
        // TODO: call here for now but ideally would want to call it right after receiving the search terms from watson????????
        SearchAsyncTask searchAsyncTask = new SearchAsyncTask();
        searchAsyncTask.execute();

    }


    private class SearchAsyncTask extends AsyncTask<String, Void, List<SearchResult>> {

        @Override
        protected List<SearchResult> doInBackground(String... urls) {

            // Perform the network request to google search, parse the response, and extract a list of search results.
            List<SearchResult> searchResultList = Utils.fetchSearchResultData(searchRequestUrl);

            return searchResultList;
        }

        /**
         * Update the screen with the given earthquake (which was the result of the
         * {@link SearchAsyncTask}).
         */
        @Override
        protected void onPostExecute(List<SearchResult> searchResultList) {
            if (searchResultList == null) {
                return;
            }

            // TODO: DELETE (ie. for testing only)
            updateUi(searchResultList);
        }


        /**
         * FOR TESTING ONLY!!!
         * Update the screen to display information from the given {@link List<SearchResult>}.
         */
        private void updateUi(List<SearchResult> searchResultList) {
            // Display the search result title in the UI
            TextView titleTextView = (TextView) findViewById(R.id.title);
            // Display the search result url in the UI
            TextView urlTextView = (TextView) findViewById(R.id.url);
            // Display the search result snippet in the UI
            TextView snippetTextView = (TextView) findViewById(R.id.snippet);

            for (int m = 0; m < searchResultList.size(); m++) {
                titleTextView.append("#" + m + ": " + searchResultList.get(m).getTitle() + "\n");

                urlTextView.append("#" + m + ": " + searchResultList.get(m).getUrl() + "\n");

                snippetTextView.append("#" + m + ": " + searchResultList.get(m).getSnippet() + "\n");
            }

        }


    }


}

