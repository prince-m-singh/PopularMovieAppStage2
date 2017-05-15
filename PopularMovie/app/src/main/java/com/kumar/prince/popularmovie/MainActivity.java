package com.kumar.prince.popularmovie;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
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

import com.kumar.prince.popularmovie.adapter.MovieAdapter;
import com.kumar.prince.popularmovie.network.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {
    private final String TAG = getClass().getName();
    private String[] imgUrl;
    private GridView mGridView;
    /*Menu option where user can change from Toprated movie to popular movie and vise versa */
    private Menu mMenu;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;
    private MovieAdapter movieAdapter;
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
    private final String MOVIE_ID="id";
    private final String VOTE_COUNT="vote_count";
    private RecyclerView.LayoutManager layoutManager;
    int mScrollPosition;

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
        movieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(movieAdapter);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        if (mBundleRecyclerViewState != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
        loadMovieData();

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

    }

    private void loadMovieData() {
        showMovieDataView();
        new FetchMovieDataTask().execute(urlType);


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
            loadMovieData();
            return true;
        } else if (itemThatWasClickedId == R.id.action_top_rated) {
            urlType = 1;
            movieAdapter.setMovierURLData(null);
            loadMovieData();
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
            Log.e("Data",movieData.toString());
            String title = movieData.getString(TITLE);
            String poster = "" + movieData.getString(MOVIE_POSTER);
            String release_date = movieData.getString(RELEASE_DATE);
            String vote = movieData.getString(VOTE_AVERAGE);
            String plot = movieData.getString(PLOT_SYNOPSIS);
            String id=movieData.getString(MOVIE_ID);
            String voteCount=movieData.getString(VOTE_COUNT);
            Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class);
            intent.putExtra(TITLE, title);
            intent.putExtra(MOVIE_POSTER, poster);
            intent.putExtra(RELEASE_DATE, release_date);
            intent.putExtra(VOTE_AVERAGE, vote);
            intent.putExtra(PLOT_SYNOPSIS, plot);
            intent.putExtra(MOVIE_ID,id);
            intent.putExtra(VOTE_COUNT,voteCount);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
            showErrorMessage();
        }

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
}
