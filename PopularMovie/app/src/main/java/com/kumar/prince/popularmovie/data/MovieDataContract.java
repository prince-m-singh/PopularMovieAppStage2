/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kumar.prince.popularmovie.data;

import android.net.Uri;
import android.provider.BaseColumns;



/**
 * Defines table and column names for the  favorites movie database.
 */
public class MovieDataContract {
    public static final String CONTENT_AUTHORITY = "com.kumar.prince.popularmovie";
    // Database schema information
    public static final String TABLE_FAVORITES = "favorites";

    // Base content Uri for accessing the provider
    public static final Uri CONTENT_URI = new Uri.Builder().scheme("content")
            .authority(CONTENT_AUTHORITY)
            .appendPath(TABLE_FAVORITES)
            .build();

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
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