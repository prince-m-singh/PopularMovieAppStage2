package com.kumar.prince.popularmovie.utilities.review;


public class MovieReview {

    private String id;

    private MovieReviewResultHolder[] results;

    private String page;

    private String total_pages;

    private String total_results;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MovieReviewResultHolder[] getResults() {
        return results;
    }
}
