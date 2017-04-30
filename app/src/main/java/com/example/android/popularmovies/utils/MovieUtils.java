package com.example.android.popularmovies.utils;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.data.Movies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.popularmovies.utils.NetworkUtils.createUrl;
import static com.example.android.popularmovies.utils.NetworkUtils.makeHttpRequest;

/**
 * Created by bjoern on 13.03.17.
 *
 * @author <a href="mailto:mail@bjoern.cologne">Bjoern Gam</a>
 * @link <a href="http://bjoern.cologne">Webpage </a>
 * <p>
 * Description: Reused code from the 'newsapp' app project.
 */

public class MovieUtils {

    /** JSON object parameters **/

    private static final String MOVIE_POSTER = "poster_path";
    private static final String MOVIE_RELEASE_DATE = "release_date";
    private static final String MOVIE_OVERVIEW = "overview";
    private static final String MOVIE_TITLE = "original_title";
    private static final String MOVIE_BACKDROP_PATH = "backdrop_path";
    private static final String MOVIE_AVERAGE_VOTE = "vote_average";
    private static final String MOVIE_ID = "id";

    /** API Key **/
    private static final String API_KEY = BuildConfig.API_KEY;

    public MovieUtils() { /*no default constructor*/ }

    /**
     * Query the Movie and return a list of {@link Movies} objects.
     */
    public static List<Movies> fetchMovieData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("Error:", "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Movies}
        List<Movies> movies = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link movies}
        return movies;
    }

    /**
     * Return a list of {@link Movies} objects that has been built up from
     * parsing the given JSON response.
     */
    @Nullable
    private static List<Movies> extractFeatureFromJson(String moviesJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(moviesJSON)) { return null; }
        // Create an empty ArrayList that we can start adding movies to
        List<Movies> movieList = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(moviesJSON);

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of features.
            JSONArray MoviesArray = baseJsonResponse.getJSONArray("results");
            // For each book in the NewsArray, create an {@link News} object
            for (int i = 0; i < MoviesArray.length(); i++) {
                JSONObject currentMovie = MoviesArray.getJSONObject(i);
                //getting the movies ID
                String ID = currentMovie.getString(MOVIE_ID);
                //getting the path of the poster image
                String poster = currentMovie.getString(MOVIE_POSTER);
                //getting the synopsis of the movie
                String overview = currentMovie.getString(MOVIE_OVERVIEW);
                //getting the release date
                String release_date = currentMovie.getString(MOVIE_RELEASE_DATE);
                //getting the movie title
                String original_title = currentMovie.getString(MOVIE_TITLE);
                //getting the second image of the movie
                String backdrop_path = currentMovie.getString(MOVIE_BACKDROP_PATH);
                //getting the average vote of the movie
                String vote_average = currentMovie.getString(MOVIE_AVERAGE_VOTE);
                // getting the correct movie review
                String movie_review = getMovieReview(currentMovie.getString(MOVIE_ID));
                //creating a new object
                Movies movie = new Movies(original_title, overview, vote_average,
                        release_date, poster, backdrop_path, ID, movie_review);
                //add the object to the movielist.
                movieList.add(movie);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("MovieUtils", "Problem parsing the the movie JSON results", e);
        }
        // Return the list of movies
        return movieList;
    }

    /**
     * Getting the movies review by the movies id
     * The basic URL for every review / content is:
     * http://api.themoviedb.org/3/movie/"+ moviesID +"/reviews?api_key=API_KEY
     * The return value of the method is the review of the movie
     */
    private static String getMovieReview (String movieID){
        String requestString = null;            // for our HTTP request

        // Building the proper URL for the review:
        final String BASE_REVIEWS_URL = "http://api.themoviedb.org/3/movie/";
        final String PATH_REVIEWS_URL = "/reviews?api_key=";
        String reviewsURL= BASE_REVIEWS_URL + movieID + PATH_REVIEWS_URL + API_KEY;

        URL url = NetworkUtils.createUrl(reviewsURL);
        try
        {
            requestString = makeHttpRequest(url);
        }catch (IOException ex) {
            Log.e("MovieUtils", "Error creating the reviews URL ", ex);
        }

        try{
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(requestString);
            JSONArray movieJSONReview = baseJsonResponse.getJSONArray("results");
            if (movieJSONReview.length() != 0) {
                JSONObject currentObject = movieJSONReview.getJSONObject(0);
                return (currentObject.getString("content"));
            }else if (movieJSONReview.length() == 0 ) {return "Currently no review";}
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("MovieUtils", "Problem parsing the the movie reviews JSON results", e);
        }
        return null;
    }
}
