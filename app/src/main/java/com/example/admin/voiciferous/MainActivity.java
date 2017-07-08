package com.example.admin.voiciferous;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<String> searchWordList = new ArrayList<String>();

        // Here is a hard coded example of the parsed results received from Watson
        searchWordList.add("movie");
        searchWordList.add("1 hour");
        searchWordList.add("family");
        searchWordList.add("G-rated");
        searchWordList.add("dogs");


        Intent searchIntent = new Intent(Intent.ACTION_WEB_SEARCH);
        String keyword= searchWordList.get(1);
        searchIntent.putExtra(SearchManager.QUERY, keyword);
        if (searchIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(searchIntent);
        }



    }

}

