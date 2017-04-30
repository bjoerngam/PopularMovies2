package com.example.android.popularmovies.utils;

import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import static android.content.ContentValues.TAG;

/**
 * Created by bjoern on 20.04.17.
 *
 * @author <a href="mailto:mail@bjoern.cologne">Bjoern Gam</a>
 * @link <a href="http://bjoern.cologne">Webpage </a>
 * <p>
 * Description:
 */
public class NetworkUtils {

    /** Tag for the log messages */
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    /**BASE URL**/
    private static final String BASE_URL = "https://api.themoviedb.org";

    /**Building the path **/
    private static final String MOVIES_STAGE ="3";

    private static final String MOVIES_FOLDER ="movie";

    //The query command for getting the popular movies
    private static final int MOVIE_SORT_POPULAR = 2;

    //The query command for getting the highest ratted movies
    private static final int MOVIE_SORT_HIGHEST_RATED = 1;

    /** API Key **/
    private static final String API_KEY = BuildConfig.API_KEY;

    /** HTTP **/
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;
    private static final int HTTP_SUCCESS = 200;


    public static URL createUrl(String stringUrl) {
        /**
         * Returns new URL object from the given string URL.
         */

        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    public static String makeHttpRequest(URL url) throws IOException {
        String mResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return mResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT /* milliseconds */);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse HTTP_SUCCESS response.
            if (urlConnection.getResponseCode() == HTTP_SUCCESS) {
                inputStream = urlConnection.getInputStream();
                mResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem getting the the movies results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return mResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    public static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader =
                    new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Build the proper URL for the movie datebase
     * @param sortKind
     * When you using the const int: MOVIE_SORT_POPULAR the popular movies will be
     * displayed. When you using the const int: MOVIE_SORT_TOP_RATED the highest
     * rated movies will be displayed.
     */

    public static URL movieURL (int sortKind){

        Uri buildURI;

        switch (sortKind) {

            case MOVIE_SORT_POPULAR:

                buildURI = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(MOVIES_STAGE)
                        .appendPath(MOVIES_FOLDER)
                        .appendPath("popular")
                        .appendQueryParameter("api_key", API_KEY)
                        .build();
                break;

            case MOVIE_SORT_HIGHEST_RATED:
                 buildURI = Uri.parse(BASE_URL).buildUpon()
                         .appendPath(MOVIES_STAGE)
                         .appendPath(MOVIES_FOLDER)
                         .appendPath("top_rated")
                         .appendQueryParameter("api_key", API_KEY)
                         .build();
                break;

            default:
                buildURI = null;
                Log.v(TAG, "Unknown sort order.");
                break;
        }
        URL url = null;
        try {
            url = new URL(buildURI.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

}
