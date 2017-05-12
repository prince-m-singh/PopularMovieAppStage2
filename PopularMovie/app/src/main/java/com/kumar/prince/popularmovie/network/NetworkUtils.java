package com.kumar.prince.popularmovie.network;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.kumar.prince.popularmovie.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by princ on 14-04-2017.
 */

public class NetworkUtils {

    private static final String TAG = "NetworkUtils";

    /*These two URL used for requesting Movie according to popularity  */
    private static final String POPULAR_MOVIE_URL = "https://api.themoviedb.org/3/movie/popular";

    private static final String TOP_RATED_MOVIE_URL = "https://api.themoviedb.org/3/movie/top_rated";


    /* The format we want our API to return */
    private static final String format = "json";
    /* The number of page we want our API to return */


    /*In which Language Movie Popular or Top Rated  */
    private static final String languageCode = "en-US";
    private static int page = 1;

    final static String API = "api_key";
    final static String LANGUAGE = "language";
    final static String LON_PARAM = "lon";
    final static String PAGE = "page";
    final static String UNITS_PARAM = "units";
    final static String DAYS_PARAM = "cnt";


    public static String urlForQuery(int urlQueryType) {
        String queryurl = "";
        if (urlQueryType == 0) {
            queryurl = POPULAR_MOVIE_URL;
        } else if (urlQueryType == 1) {
            queryurl = TOP_RATED_MOVIE_URL;
        } else {
            queryurl = TOP_RATED_MOVIE_URL;
        }
        return queryurl;
    }

    /**
     * Build Url for popular movie and toprated url
     *
     * @param context context for application.
     * @param urlType which url type we are sending rewuest
     * @return The URL to use to query the weather server.
     */
    public static URL buildUrl(Context context, int urlType) {
        Log.v(TAG, urlType + "Built URI " + urlForQuery(urlType));
        String apikey = context.getResources().getString(R.string.api_key);
        Uri builtUri = Uri.parse(urlForQuery(urlType)).buildUpon()
                .appendQueryParameter(API, apikey)
                .appendQueryParameter(LANGUAGE, languageCode)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Builds the URL used to talk to the weather server using latitude and longitude of a
     * location.
     *
     * @param lat The latitude of the location
     * @param lon The longitude of the location
     * @return The Url to use to query the weather server.
     */
    public static URL buildUrl(Double lat, Double lon) {
        /** This will be implemented in a future lesson **/
        return null;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(5000);
        urlConnection.setReadTimeout(10000);

        try {


            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }


}
