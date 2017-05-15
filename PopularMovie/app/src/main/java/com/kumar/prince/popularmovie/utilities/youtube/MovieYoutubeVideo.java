package com.kumar.prince.popularmovie.utilities.youtube;
/**
 * Created by princ on 14-05-2017.
 */
public class MovieYoutubeVideo {

    private String id;

    private MovieYoutubeVideoResultsHolder[] results;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MovieYoutubeVideoResultsHolder[] getResults() {
        return results;
    }

    public void setResults(MovieYoutubeVideoResultsHolder[] result) {
        results = result;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = " + id + ", results = " + results + "]";
    }
}
