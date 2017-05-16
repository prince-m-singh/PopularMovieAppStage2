
package com.kumar.prince.popularmovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Favorites.db";
    private static final int DB_VERSION = 1;
    private static final String TAG = MovieDbHelper.class.getSimpleName();

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_TABLE_FAVORITES = "CREATE TABLE " +
            MovieDataContract.TABLE_FAVORITES + " (" +
            MovieDataContract.TableFavorites.COL_ID + TEXT_TYPE + PRIMARY_KEY + COMMA_SEP +
            MovieDataContract.TableFavorites.COL_TITLE + TEXT_TYPE + COMMA_SEP +
            MovieDataContract.TableFavorites.COL_ADULT + INTEGER_TYPE + COMMA_SEP +
            MovieDataContract.TableFavorites.COL_ORIGINAL_LANGUAGE + TEXT_TYPE + COMMA_SEP +
            MovieDataContract.TableFavorites.COL_RELEASE_DATE + TEXT_TYPE + COMMA_SEP +
            MovieDataContract.TableFavorites.COL_POSTER_PATH + TEXT_TYPE + COMMA_SEP +
            MovieDataContract.TableFavorites.COL_VOTE_AVERAGE + TEXT_TYPE + COMMA_SEP +
            MovieDataContract.TableFavorites.COL_THUMBNAIL + TEXT_TYPE + COMMA_SEP +
            MovieDataContract.TableFavorites.COL_PEOPLE + INTEGER_TYPE + COMMA_SEP +
            MovieDataContract.TableFavorites.COL_MOVIE_OVERVIEW + TEXT_TYPE +
            " )";

    public MovieDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_FAVORITES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieDataContract.TABLE_FAVORITES);
        onCreate(db);
    }
}