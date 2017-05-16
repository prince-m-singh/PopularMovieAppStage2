package com.kumar.prince.popularmovie;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kumar.prince.popularmovie.data.MovieDataContract;
import com.kumar.prince.popularmovie.databinding.ActivityMovieDetailsBinding;
import com.kumar.prince.popularmovie.network.MovieAPI;
import com.kumar.prince.popularmovie.network.NetworkAPI;
import com.kumar.prince.popularmovie.utilities.review.MovieReview;
import com.kumar.prince.popularmovie.utilities.review.MovieReviewResultHolder;
import com.kumar.prince.popularmovie.utilities.youtube.MovieYoutubeVideo;
import com.kumar.prince.popularmovie.utilities.youtube.MovieYoutubeVideoResultsHolder;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
    private final String MOVIE_ID = "id";
    private final String VOTECOUNT = "vote_count";
    private final String ORIGINAL_LANG = "original_language";
    private final String FAVOURITE_MOVIE = "favouritemovie";
    private TextView synopsisViewTv, averageVoteTv, movieReleaseDateTv, movieTitleTv, movieReviewTv, voteCountTv;
    private ImageView movieImageView, fab;
    private String title, release, poster, vote, plot, movieId, voteCount, lang, movieImage, posterURL;
    private boolean favMovie = false;
    private String shareYoutubeID;
    private LinearLayout linearLayout;
    private ActivityMovieDetailsBinding mDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  mDetailBinding= DataBindingUtil.setContentView(this,R.layout.activity_movie_details);
        setContentView(R.layout.activity_movie_details);
        synopsisViewTv = (TextView) findViewById(R.id.textViewPlotSynossis);
        averageVoteTv = (TextView) findViewById(R.id.textViewMovieRating);
        movieReleaseDateTv = (TextView) findViewById(R.id.textViewMoviDateOfRelease);
        movieTitleTv = (TextView) findViewById(R.id.textViewMovieTitle);
        movieImageView = (ImageView) findViewById(R.id.imageViewMovie);
        movieReviewTv = (TextView) findViewById(R.id.textViewMovieReview);
        linearLayout = (LinearLayout) findViewById(R.id.youtubelayout);
        voteCountTv = (TextView) findViewById(R.id.textViewVoteCount);
        fab = (ImageView) findViewById(R.id.fab);

        if (uiUpdate()) {
            Log.d("success", "success in updating UI");
        } else
            showErrorDialog();

        if (favMovie)
            fab.setImageResource(R.drawable.ic_star_on);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Fab add", Toast.LENGTH_SHORT).show();
                fab.setImageResource(R.drawable.ic_star_on);
                if (!favMovie){
                    favMovie=true;
                    insertFavMovieData();
                }

                //mBinding.fab.setImageResource(R.drawable.ic_star_on);
            }
        });
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
                movieId = intent.getStringExtra(MOVIE_ID);
                voteCount = intent.getStringExtra(VOTECOUNT);
                favMovie = intent.getBooleanExtra(FAVOURITE_MOVIE, false);
                lang = intent.getStringExtra(ORIGINAL_LANG);

                posterURL = "http://image.tmdb.org/t/p/w500/" + poster;


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
        voteCountTv.setText(voteCount);
        getTrailer(linearLayout);
        getMovieReview(movieReviewTv);
        Picasso.with(this)
                .load(posterURL)
                .into(movieImageView);
        return true;
    }

    /*Saving the instance when screen rotation happen*/

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if (title != null && release != null && plot != null && vote != null && posterURL != null) {
            outState.putString(MOVIE_TITLE, title);
            outState.putString(MOVIE_RELEASE_DATE, release);
            outState.putString(SYNOPSIS_OF_MOVIE, plot);
            outState.putString(AVERAGE_VOTE_MOVIE, vote);
            outState.putString(MOVIE_POSTER, posterURL);
        }
    }


    protected void getMovieReview(final View review) {
        MovieAPI mMovieAPI = NetworkAPI.createService(MovieAPI.class);
        mMovieAPI.fetchReview(getApplicationContext().getResources().getString(R.string.api_key), movieId, new Callback<MovieReview>() {

            @Override
            public void success(MovieReview movieReview, Response response) {
                MovieReviewResultHolder[] movieResult = movieReview.getResults();
                if (movieResult.length > 0) {
                    ((TextView) review).setText(movieResult[0].getContent());
                    Log.i("REVIEW", movieResult[0].getContent());
                } else
                    //movieReviewTv.setText("Sorry No Review is Available Till Now!");
                    ((TextView) review).setText("Sorry No Review is Available Till Now!");

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("error", error.toString());
                //movieReviewTv.setText("Sorry! Check Back Latter! Network Error!");
                ((TextView) review).setText("Sorry! Check Back Latter! Network Error!");
            }
        });
    }


    protected void getTrailer(final LinearLayout youtubeViewHolder) {
        MovieAPI mMovieAPI = NetworkAPI.createService(MovieAPI.class);
        mMovieAPI.fetchVideos(getApplicationContext().getResources().getString(R.string.api_key), movieId, new Callback<MovieYoutubeVideo>() {

            @Override
            public void success(MovieYoutubeVideo movieYoutubeVideo, Response response) {
                youtubeViewHolder.setPadding(5, 5, 5, 0);
                MovieYoutubeVideoResultsHolder[] trailer = movieYoutubeVideo.getResults();
                if (trailer.length > 0) {
                    shareYoutubeID = trailer[0].getKey();
                    for (final MovieYoutubeVideoResultsHolder obj : trailer) {
                        String url = generateYoutubeThumbnailURL(obj.getKey());
                        ImageView myImage = new ImageView(getApplicationContext());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                180,
                                200
                        );
                        params.leftMargin = 3;
                        params.rightMargin = 3;
                        params.topMargin = 0;
                        params.bottomMargin = 0;
                        myImage.setLayoutParams(params);
                        Picasso.with(getApplicationContext())
                                .load(url)
                                .into(myImage);
                        youtubeViewHolder.addView(myImage);
                        myImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                watchYoutubeVideo(obj.getKey());
                            }
                        });

                    }

                } else {
                    youtubeViewHolder.setPadding(50, 5, 50, 5);
                    TextView errmsg = new TextView(getApplicationContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            30
                    );
                    errmsg.setLayoutParams(params);
                    errmsg.setText("That's Bad Luck,No Trailers Found!Check later");
                    youtubeViewHolder.addView(errmsg);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                youtubeViewHolder.setPadding(50, 50, 50, 50);
                TextView errmsg = new TextView(getApplicationContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        30
                );
                errmsg.setLayoutParams(params);
                errmsg.setText("Network Error! You can't view Trailers Rite Now");
                youtubeViewHolder.addView(errmsg);

            }
        });
    }

    protected String generateYoutubeThumbnailURL(String id) {
        String url = "http://img.youtube.com/vi/" + id + "/mqdefault.jpg";
        return url;
    }


    public void watchYoutubeVideo(String id) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            startActivity(intent);
        }
    }


    private void insertFavMovieData() {
        ContentValues contentValues = new ContentValues();
        // Put the task description and selected mPriority into the ContentValues
        contentValues.put(MovieDataContract.TableFavorites._ID, movieId);
        contentValues.put(MovieDataContract.TableFavorites.COL_TITLE, title);
        contentValues.put(MovieDataContract.TableFavorites.COL_ORIGINAL_LANGUAGE, lang);
        contentValues.put(MovieDataContract.TableFavorites.COL_RELEASE_DATE, release);

        contentValues.put(MovieDataContract.TableFavorites.COL_POSTER_PATH, poster);
        contentValues.put(MovieDataContract.TableFavorites.COL_VOTE_AVERAGE, vote);
        // contentValues.put(MovieDataContract.TableFavorites.COL_THUMBNAIL, input);
        contentValues.put(MovieDataContract.TableFavorites.COL_PEOPLE, voteCount);
        contentValues.put(MovieDataContract.TableFavorites.COL_MOVIE_OVERVIEW, plot);

        Uri uri = getContentResolver().insert(MovieDataContract.CONTENT_URI, contentValues);

        if (uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }
     /*   title = intent.getStringExtra(MOVIE_TITLE);
        release = intent.getStringExtra(MOVIE_RELEASE_DATE);
        poster = intent.getStringExtra(MOVIE_POSTER);
        vote = intent.getStringExtra(AVERAGE_VOTE_MOVIE);
        plot = intent.getStringExtra(SYNOPSIS_OF_MOVIE);
        movieId = intent.getStringExtra(MOVIE_ID);
        voteCount = intent.getStringExtra(VOTECOUNT);
        poster = "http://image.tmdb.org/t/p/w500/" + poster;
*/
    }

    private boolean dataSearch() {

        return true;
    }


}
