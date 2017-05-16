/*
 * Copyright (C) 2015 The Android Open Source Project
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

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MovieDataProvider extends ContentProvider {

    private static final String TAG = MovieDataProvider.class.getSimpleName();

    private static final int FAVORITES = 100; // can be used later if we are providing all favorites
    private static final int FAVORITES_WITH_ID = 101;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(MovieDataContract.CONTENT_AUTHORITY,
                MovieDataContract.TABLE_FAVORITES,
                FAVORITES);

        uriMatcher.addURI(MovieDataContract.CONTENT_AUTHORITY,
                MovieDataContract.TABLE_FAVORITES + "/#",
                FAVORITES_WITH_ID);
    }

    private MovieDbHelper mFavoritesDBHelper;

    @Override
    public boolean onCreate() {
        mFavoritesDBHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // get readable database access
        final SQLiteDatabase db = mFavoritesDBHelper.getReadableDatabase();

        // Match URI
        int match = uriMatcher.match(uri);

        // query

        Cursor rtCursor;

        switch (match) {
            case FAVORITES:
                rtCursor = db.query(MovieDataContract.TABLE_FAVORITES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
                break;
            case FAVORITES_WITH_ID:

                // selection and args
                // uri

                final int INDEX_OF_ID_TOKEN = 1;
                String id = uri.getPathSegments().get(INDEX_OF_ID_TOKEN);
                String _selection = "_id=?";
                String[] _selectionArgs = new String[]{id};

                rtCursor = db.query(MovieDataContract.TABLE_FAVORITES,
                        projection,
                        _selection,
                        _selectionArgs,
                        null,
                        null,
                        null);

                break;
            default:
                throw new UnsupportedOperationException("Unknown URI : " + uri);
        }

        // notification
        ContentResolver resolver = getContext().getContentResolver();
        if (rtCursor != null && resolver != null) {
            rtCursor.setNotificationUri(resolver, uri);
        }

        return rtCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // get database access
        final SQLiteDatabase db = mFavoritesDBHelper.getWritableDatabase();

        // Match URI
        int match = uriMatcher.match(uri);

        Uri returnUri;
        switch (match) {
            case FAVORITES:
                long id = db.insert(MovieDataContract.TABLE_FAVORITES, null, values);
                if (id > 0) {
                    // insert successful
                    returnUri = ContentUris.withAppendedId(MovieDataContract.CONTENT_URI, id);
                } else {
                    // failed
                    throw new SQLException("Failed to insert into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown URI : " + uri);
        }

        // notify change
        ContentResolver resolver = getContext().getContentResolver();
        if (resolver != null) {
            resolver.notifyChange(uri, null);
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // get database access
        final SQLiteDatabase db = mFavoritesDBHelper.getWritableDatabase();

        // Match URI
        int match = uriMatcher.match(uri);

        int noOfDeletedRows;
        switch (match) {
            case FAVORITES:
                noOfDeletedRows = db.delete(MovieDataContract.TABLE_FAVORITES, selection, selectionArgs);
                if (noOfDeletedRows == 0) {
                    throw new SQLException("Failed to delete " + uri);
                }
                break;
            case FAVORITES_WITH_ID:
                final int INDEX_OF_ID_TOKEN = 1;
                String id = uri.getPathSegments().get(INDEX_OF_ID_TOKEN);
                String _selection = "_id=?";
                String[] _selectionArgs = new String[]{id};

                noOfDeletedRows = db.delete(MovieDataContract.TABLE_FAVORITES, _selection, _selectionArgs);
                if (noOfDeletedRows == 0) {
                    throw new SQLException("Failed to delete " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown URI : " + uri);
        }

        // notify change on id
        ContentResolver resolver = getContext().getContentResolver();
        if (resolver != null) {
            resolver.notifyChange(uri, null);
//            if (match == FAVORITES_WITH_ID) {
//                // in case of uri with id, notifying the main uri too
//                resolver.notifyChange(FavoritesPersistenceContract.CONTENT_URI, null);
//            }
        }
        return noOfDeletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("This provider does not support update");
    }
}