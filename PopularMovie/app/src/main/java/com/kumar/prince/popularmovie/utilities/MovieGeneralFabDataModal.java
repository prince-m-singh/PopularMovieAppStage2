package com.kumar.prince.popularmovie.utilities;

import java.io.Serializable;

public class MovieGeneralFabDataModal implements Serializable {
    String mTitle;
    String mPoster;
    String mVote;
    String mId;
    String mPeople;
    String mReleaseDate;
    String mOverview;
    String mReview;

    public MovieGeneralFabDataModal(String mTitle, String mPoster,
                                    String mVote, String mId,
                                    String mPeople, String mReleaseDate,
                                    String mOverview) {
        this.mTitle = mTitle;
        this.mPoster = mPoster;
        this.mReleaseDate = mReleaseDate;
        this.mVote = mVote;
        this.mOverview = mOverview;
        this.mId = mId;
        this.mPeople = mPeople;


    }

    public String getmPoster() {
        return this.mPoster;
    }

    public String getmReview() {
        return this.mReview;
    }

    public void setmReview(String mReview) {
        this.mReview = mReview;
    }

    public String getmOverview() {
        return this.mOverview;
    }

    public String getmReleaseDate() {
        return this.mReleaseDate;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getThumbnail() {
        String url = "http://image.tmdb.org/t/p/w185/" + this.mPoster;
        return url;
    }

    public String getmId() {
        return this.mId;
    }

    public String getmPeople() {
        return this.mPeople;
    }

    public String getmVote() {
        return this.mVote;
    }
}
