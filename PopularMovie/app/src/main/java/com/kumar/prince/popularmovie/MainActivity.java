package com.kumar.prince.popularmovie;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kumar.prince.popularmovie.adapter.MovieCursorAdapter;
import com.kumar.prince.popularmovie.adapter.MovieAdapter;
import com.kumar.prince.popularmovie.data.MovieDataContract;
import com.kumar.prince.popularmovie.network.NetworkUtils;
import com.kumar.prince.popularmovie.utilities.MovieGeneralFabDataModal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler
        , LoaderManager.LoaderCallbacks<Cursor>, MovieCursorAdapter.MovieAdapterOnClickHandler {
    private final String TAG = getClass().getName();
    private String[] imgUrl;
    private GridView mGridView;
    private static final int TASK_LOADER_ID = 0;

    // Member variables for the adapter and RecyclerView

    /*Menu option where user can change from Toprated movie to popular movie and vise versa */
    private Menu mMenu;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;
    private MovieAdapter movieAdapter;
    private MovieCursorAdapter movieCursorAdapter;
    static int urlType = 0;
    private JSONArray movieDetails;
    Context context;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    String jsonWeatherResponse;
    private static Bundle mBundleRecyclerViewState;
    private final String TITLE = "title";
    private final String RELEASE_DATE = "release_date";
    private final String MOVIE_POSTER = "poster_path";
    private final String VOTE_AVERAGE = "vote_average";
    private final String PLOT_SYNOPSIS = "overview";
    private final String MOVIE_ID = "id";
    private final String VOTE_COUNT = "vote_count";
    private final String ORIGINAL_LANG = "original_language";
    private final String FAVOURITE_MOVIE = "favouritemovie";
    private RecyclerView.LayoutManager layoutManager;
    private List<MovieGeneralFabDataModal> movieGeneralFabDataModals;
    int mScrollPosition;
    boolean fabMovie = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_moviecast);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        layoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        movieCursorAdapter = new MovieCursorAdapter(this);
        movieAdapter = new MovieAdapter(this);
        //mRecyclerView.setAdapter(movieAdapter);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        if (mBundleRecyclerViewState != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
        loadMovieData(mRecyclerView);

    }

    @Override
    protected void onPause() {
        super.onPause();
     /*   mBundleRecyclerViewState = new Bundle();
        Parcelable listState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);*/
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable("key");
            mRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("key", mRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (fabMovie)
            getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);

    }

    private void loadMovieData(RecyclerView mRecyclerView) {
        mRecyclerView.setAdapter(movieAdapter);
        showMovieDataView();
        new FetchMovieDataTask().execute(urlType);
        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);


    }

    private void loadFavMoviedata(RecyclerView mRecyclerView) {
        mRecyclerView.setAdapter(movieCursorAdapter);
        showMovieDataView();
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    /*Create Menu for selecting for popular movie or High Rating movie*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_most_popular) {
            urlType = 0;
            movieAdapter.setMovierURLData(null);
            loadMovieData(mRecyclerView);
            return true;
        } else if (itemThatWasClickedId == R.id.action_top_rated) {
            urlType = 1;
            movieAdapter.setMovierURLData(null);
            loadMovieData(mRecyclerView);
            return true;
        } else if (itemThatWasClickedId == R.id.action_favorite_movie) {
            fabMovie = true;
            movieAdapter.setMovierURLData(null);
            movieCursorAdapter.setMovierURLData(null);
            loadFavMoviedata(mRecyclerView);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method will make the View for the Movie data visible and
     * hide the error message.
     */
    private void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the weather
     * View.
     */
    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(JSONObject movieData) {
        try {
            Log.e("Data", movieData.toString());
            String title = movieData.getString(TITLE);
            String poster = "" + movieData.getString(MOVIE_POSTER);
            String release_date = movieData.getString(RELEASE_DATE);
            String vote = movieData.getString(VOTE_AVERAGE);
            String plot = movieData.getString(PLOT_SYNOPSIS);
            String id = movieData.getString(MOVIE_ID);
            String voteCount = movieData.getString(VOTE_COUNT);
            String lang = movieData.getString(ORIGINAL_LANG);
            boolean favMovie = false;
            if (containsFabMovie(movieGeneralFabDataModals, id)) {
                favMovie = true;
            } else {
                favMovie = false;
            }

            Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class);
            intent.putExtra(TITLE, title);
            intent.putExtra(MOVIE_POSTER, poster);
            intent.putExtra(RELEASE_DATE, release_date);
            intent.putExtra(VOTE_AVERAGE, vote);
            intent.putExtra(PLOT_SYNOPSIS, plot);
            intent.putExtra(MOVIE_ID, id);
            intent.putExtra(VOTE_COUNT, voteCount);
            intent.putExtra(ORIGINAL_LANG, lang);
            intent.putExtra(FAVOURITE_MOVIE, favMovie);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
            showErrorMessage();
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mMovieData = null;

            @Override
            protected void onStartLoading() {
                if (mMovieData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mMovieData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                // Will implement to load data

                // Query and load all task data in the background; sort by priority
                // [Hint] use a try/catch block to catch any errors in loading data

                try {
                    return getContentResolver().query(MovieDataContract.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        movieGeneralFabDataModals = getAllMovies(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

           // notifyDataSetChanged();


    }

    @Override
    public void onClick(MovieGeneralFabDataModal movieData) {


        String title = movieData.getTitle();
        String poster = "" + movieData.getmPoster();
        String release_date = movieData.getmReleaseDate();
        Log.i(getClass().getName(), release_date);
        String vote = movieData.getmVote();
        String plot = movieData.getmOverview();
        String id = movieData.getmId();
        String voteCount = movieData.getmPeople();
        //String lang=movieData.getString(ORIGINAL_LANG);
        Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class);
        intent.putExtra(TITLE, title);
        intent.putExtra(MOVIE_POSTER, poster);
        intent.putExtra(RELEASE_DATE, release_date);
        intent.putExtra(VOTE_AVERAGE, vote);
        intent.putExtra(PLOT_SYNOPSIS, plot);
        intent.putExtra(MOVIE_ID, id);
        intent.putExtra(VOTE_COUNT, voteCount);
        intent.putExtra(FAVOURITE_MOVIE, true);
        //intent.putExtra(ORIGINAL_LANG,lang);
        startActivity(intent);

    }


    /*Feteching Data*/
    public class FetchMovieDataTask extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... integers) {
            if (integers.length == 0) {
                return null;
            }
            URL movieRequestURL = NetworkUtils.buildUrl(getApplicationContext(), integers[0]);
            try {
                jsonWeatherResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestURL);
                if (jsonWeatherResponse != null) {
                    JSONObject movie = new JSONObject(jsonWeatherResponse);
                    movieDetails = movie.getJSONArray("results");
                    imgUrl = new String[movieDetails.length()];
                    for (int i = 0; i < movieDetails.length(); i++) {
                        JSONObject temp_mov = movieDetails.getJSONObject(i);
                        imgUrl[i] = context.getResources().getString(R.string.poster_url) + temp_mov.getString("poster_path");
                    }
                } else
                    return jsonWeatherResponse;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return jsonWeatherResponse;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String strings) {
            super.onPostExecute(strings);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (strings != null) {
                JSONObject movie = null;
                try {
                    movie = new JSONObject(strings);
                    movieDetails = movie.getJSONArray("results");
                    imgUrl = new String[movieDetails.length()];
                    for (int i = 0; i < movieDetails.length(); i++) {
                        JSONObject temp_mov = movieDetails.getJSONObject(i);
                        imgUrl[i] = context.getResources().getString(R.string.poster_url) + temp_mov.getString("poster_path");
                        Log.e("Movie", "   " + context.getResources().getString(R.string.poster_url) + temp_mov.getString("poster_path"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                movieAdapter.setMovierURLData(imgUrl);
                movieAdapter.setMovieDataJSONArray(movieDetails);

            } else {
                showErrorMessage();
            }
        }
    }

    public List<MovieGeneralFabDataModal> getAllMovies(Cursor cursor) {
        imgUrl = new String[cursor.getCount()];
        List<MovieGeneralFabDataModal> movieList = new ArrayList<>();

        if (cursor == null) {
            return null;
        }
        int totalData = cursor.getCount();
        int movieIdIndex = cursor.getColumnIndex(MovieDataContract.TableFavorites.COL_ID);
        int titleIndex = cursor.getColumnIndex(MovieDataContract.TableFavorites.COL_TITLE);
        int posterIndex = cursor.getColumnIndex(MovieDataContract.TableFavorites.COL_POSTER_PATH);
        int releaseDateIndex = cursor.getColumnIndex(MovieDataContract.TableFavorites.COL_RELEASE_DATE);
        int voteIndex = cursor.getColumnIndex(MovieDataContract.TableFavorites.COL_VOTE_AVERAGE);
        int originalLangIndex = cursor.getColumnIndex(MovieDataContract.TableFavorites.COL_ORIGINAL_LANGUAGE);
        int peopleIndex = cursor.getColumnIndex(MovieDataContract.TableFavorites.COL_PEOPLE);
        int movieOverViewIndex = cursor.getColumnIndex(MovieDataContract.TableFavorites.COL_MOVIE_OVERVIEW);
        int i = 0;
        Log.e("Index", "totalData" + totalData + "movieIdIndex "
                + movieIdIndex + "posterIndex " + posterIndex + "releaseDateIndex " + releaseDateIndex + "movieOverViewIndex " + movieOverViewIndex);
        if (cursor.moveToFirst()) {
            do {
                MovieGeneralFabDataModal movie = new MovieGeneralFabDataModal(cursor.getString(titleIndex), cursor.getString(posterIndex),
                        cursor.getString(voteIndex), cursor.getString(movieIdIndex), cursor.getString(peopleIndex),
                        cursor.getString(releaseDateIndex), cursor.getString(movieOverViewIndex));

                Log.e("Movie", context.getResources().getString(R.string.poster_url) + cursor.getString(posterIndex));
                movieList.add(movie);
                imgUrl[i] = context.getResources().getString(R.string.poster_url) + cursor.getString(posterIndex);
                i++;
            } while (cursor.moveToNext());
        }
        if (movieList != null) {

            movieCursorAdapter.setMovierURLData(imgUrl);
            movieCursorAdapter.setMovieGeneralModals(movieList);
        } else {
            showErrorMessage();
        }


        return movieList;
    }


    private static boolean containsFabMovie(final List<MovieGeneralFabDataModal> movie, final String search) {
        for (final MovieGeneralFabDataModal moviedata : movie) {
            if (moviedata.getmId().equals(search)) {
                return true;
            }
        }

        return false;
    }
}
