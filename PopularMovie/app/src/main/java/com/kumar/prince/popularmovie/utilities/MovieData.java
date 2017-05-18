package com.kumar.prince.popularmovie.utilities;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.List;

/**
 * Created by princ on 18-05-2017.
 */

public class MovieData implements Serializable {
    private String[] posterURL;

    public String[] getPosterURL() {
        return posterURL;
    }

    public void setPosterURL(String[] posterURL) {
        this.posterURL = posterURL;
    }

    public JSONArray getMovieData() {
        return movieData;
    }

    public void setMovieData(JSONArray movieData) {
        this.movieData = movieData;
    }

    private transient JSONArray movieData;
    private int menuOption;

    public int getMenuOption() {
        return menuOption;
    }

    public void setMenuOption(int menuOption) {
        this.menuOption = menuOption;
    }

    public List<MovieGeneralFabDataModal> getMovieGeneralModals() {
        return movieGeneralModals;
    }

    public void setMovieGeneralModals(List<MovieGeneralFabDataModal> movieGeneralModals) {
        this.movieGeneralModals = movieGeneralModals;
    }

    private List<MovieGeneralFabDataModal> movieGeneralModals;
}
