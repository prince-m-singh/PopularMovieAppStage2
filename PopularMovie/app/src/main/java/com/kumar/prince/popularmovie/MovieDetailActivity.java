package com.kumar.prince.popularmovie;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by princ on 15-04-2017.
 */

/*This class used for Movie details Activity*/
public class MovieDetailActivity extends AppCompatActivity {
    private final String MOVIE_TITLE = "title";
    private final String MOVIE_RELEASE_DATE = "release_date";
    private final String MOVIE_POSTER = "poster_path";
    private final String AVERAGE_VOTE_MOVIE = "vote_average";
    private final String SYNOPSIS_OF_MOVIE = "overview";
    private TextView synopsisViewTv, averageVoteTv, movieReleaseDateTv, movieTitleTv;
    private ImageView movieImageView;
    private String title, release, poster, vote, plot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        synopsisViewTv = (TextView) findViewById(R.id.movieplot);
        averageVoteTv = (TextView) findViewById(R.id.voteAvgerage);
        movieReleaseDateTv = (TextView) findViewById(R.id.releaseDateMovie);
        movieTitleTv = (TextView) findViewById(R.id.titleofMovie);
        movieImageView = (ImageView) findViewById(R.id.posterimage);

        if (uiUpdate()) {
            Log.d("success", "success in updating UI");
        } else
            showErrorDialog();
    }

    /*It shoe*/
    private void showErrorDialog() {
        new AlertDialog.Builder(MovieDetailActivity.this)
                .setCancelable(true)
                .setMessage(R.string.error_on_load)
                .setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
    }


    /*Update Screen when the screen load with these information*/
    private boolean uiUpdate() {

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey(MOVIE_TITLE) && extras.containsKey(MOVIE_RELEASE_DATE) && extras.containsKey(MOVIE_POSTER) && extras.containsKey(AVERAGE_VOTE_MOVIE) && extras.containsKey(SYNOPSIS_OF_MOVIE)) {
                title = intent.getStringExtra(MOVIE_TITLE);
                release = intent.getStringExtra(MOVIE_RELEASE_DATE);
                poster = intent.getStringExtra(MOVIE_POSTER);
                vote = intent.getStringExtra(AVERAGE_VOTE_MOVIE);
                plot = intent.getStringExtra(SYNOPSIS_OF_MOVIE);
                poster = "http://image.tmdb.org/t/p/w500/" + poster;
            } else
                return false;
        } else
            return false;
        synopsisViewTv.setText(plot);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(release);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat df = new SimpleDateFormat("MMM dd, yyyy");
        movieReleaseDateTv.setText(df.format(date).toString());
        movieTitleTv.setText(title);
        averageVoteTv.setText(vote.toString());
        Picasso.with(this)
                .load(poster)
                .into(movieImageView);
        return true;
    }

    /*Saving the instance when screen rotation happen*/

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if (title != null && release != null && plot != null && vote != null && poster != null) {
            outState.putString(MOVIE_TITLE, title);
            outState.putString(MOVIE_RELEASE_DATE, release);
            outState.putString(SYNOPSIS_OF_MOVIE, plot);
            outState.putString(AVERAGE_VOTE_MOVIE, vote);
            outState.putString(MOVIE_POSTER, poster);
        }
    }


}
