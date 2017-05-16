package com.kumar.prince.popularmovie.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieDataContract {
    public static final String CONTENT_AUTHORITY = "com.kumar.prince.popularmovie";
    public static final String TABLE_FAVORITES = "favorites";
    public static final Uri CONTENT_URI = new Uri.Builder().scheme("content")
            .authority(CONTENT_AUTHORITY)
            .appendPath(TABLE_FAVORITES)
            .build();

    private MovieDataContract() {
    }

    public static final class TableFavorites implements BaseColumns {
        public static final String COL_ID = "_id";
        public static final String COL_TITLE = "title";
        public static final String COL_POSTER_PATH = "poster_path";
        public static final String COL_ADULT = "adult";
        public static final String COL_RELEASE_DATE = "release_date";
        public static final String COL_VOTE_AVERAGE = "vote_average";
        public static final String COL_ORIGINAL_LANGUAGE = "original_language";
        public static final String COL_THUMBNAIL = "mThumbnail";
        public static final String COL_PEOPLE = "mPeople";
        public static final String COL_MOVIE_OVERVIEW = "mOverview";
        public static final String COL_MOVIE_ID = "mReview";


    }
}