package com.example.android.popularmovies.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by bjoern on 10.04.17.
 *
 * @author <a href="mailto:mail@bjoern.cologne">Bjoern Gam</a>
 * @link <a href="http://bjoern.cologne">Webpage </a>
 * <p>
 * Description: This class contains the layout of our
 * database.
 */
public class DBContract {

    // With the default constructor set as private it's not possible
    // to create an object of the class
    private DBContract() {}

    // Here we are setting up the default content_authority uri
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";

    //Here we are build the complete uri int two steps
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCT = "PopularMovies";

    public static final class MoviesEntry implements BaseColumns {

        // the complete content_uri for getting access to the database
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCT);

        public static final String TABLE_NAME = "movies";                              // the name of the table inside the database

        // The table layout
        public static final String ID = BaseColumns._ID;                                // the unique id of the entry
        public static final String COLUMN_MOVIE_TITLE = "movieTitle";                   // the title of the movie
        public static final String COLUMN_MOVIE_RATING = "movieRating";                 // the rating of the movie
        public static final String COLUMN_MOVIE_CONTENT = "movieContent";               // the content of the movie
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movieReleaseDate";      // the release date of the movie
        public static final String COLUMN_MOVIE_REVIEW = "movieReview";                 // the review of the movie
        public static final String COLUMN_MOVIE_POSTER = "moviePoster";                 // the poster of the movie
        public static final String COLUMN_MOVIE_BACKPATH_POSTER = "movieBackpath";       // the second picture of the movie
        public static final String COLUMN_MOIVE_TRAILER = "movieTrailer";

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of ware.
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

        public static Uri buildMovieDetails(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }
    }
}
