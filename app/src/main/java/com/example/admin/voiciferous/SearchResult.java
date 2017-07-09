package com.example.admin.voiciferous;

/**
 * {@Link SearchResult} represents a single search result
 */

public class SearchResult {

    /** Title of the search result */
    private String mTitle;

    /** URL of the search result (formattedUrl)*/
    private String mUrl;

    /** Snippet of the search result (snippet)*/
    private String mSnippet;

    /**
     * Create a new SearchResult object.
     *
     * @param title
     * @param url
     * @param snippet
     */
    public SearchResult(String title, String url, String snippet) {
        mTitle = title;
        mUrl = url;
        mSnippet = snippet;
    }

    /**
     * Get the title of the search result
     * @return the title of the search result
     */
    public String getTitle() { return mTitle; }

    /**
     * Get the URL of the search result
     * @return the URL of the search result
     */
    public String getUrl() { return mUrl; }

    /**
     * Get the snippet of the search result
     * @return the snippet of the search result
     */
    public String getSnippet() { return mSnippet; }


    /**
     * Returns the string representation of the {@link SearchResult} object.
     */
    @Override
    public String toString() {
        return "SearchResult{" +
                "mTitle=" + mTitle +
                ", mUrl='" + mUrl + '\'' +
                ", mSnippet='" + mSnippet + '\'' +
                '}';
    }



}
