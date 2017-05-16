package com.kumar.prince.popularmovie.network;


import com.kumar.prince.popularmovie.utilities.review.MovieReview;
import com.kumar.prince.popularmovie.utilities.youtube.MovieYoutubeVideo;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MovieAPI {

    @GET("/3/movie/{id}/reviews")
    void fetchReview(
            @Query("api_key") String mApiKey,
            @Path("id") String id,
            Callback<MovieReview> cb
    );

    @GET("/3/movie/{id}/videos")
    void fetchVideos(
            @Query("api_key") String mApiKey,
            @Path("id") String id,
            Callback<MovieYoutubeVideo> cb
    );

}
