package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.popularmovies.adapter.TrailerAdapter;
import com.example.android.popularmovies.data.DBContract;
import com.example.android.popularmovies.data.Movies;
import com.example.android.popularmovies.databinding.ActivityMovieDetailsBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.popularmovies.BuildConfig.API_KEY;
import static com.example.android.popularmovies.utils.NetworkUtils.makeHttpRequest;

public class MovieDetails extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Movies>>{

    //Size of the main movie details image
    private static final int IMAGE_WIDTH = 400;
    private static final int IMAGE_HEIGHT = 200;

    private static final int COMPRESS_FACTOR = 100;

    public String mMovieTitle;
    public String mMovieOverview;
    public String mMovieMainTrailer;

    // We need this for the whole YouTube issue.
    public MovieTrailer mTrailer;
    public ArrayList<String> mTrailerPreviewImages;
    public ArrayList<String> mTrailerURL;

    //With this boolean we are checking if a movie is already saved.
    private boolean mMovieSaved;

    private ActivityMovieDetailsBinding mMovieDetailBinding;

    // Two string variables for the sharing dialog
    private String mMovieDetailsSharing;
    private static final String MOVIE_DETAIL_SHARE_HASHTAG = " #MovieApp";

    /** Content URI for the existing movie (null if it's a new movie) */
    private Uri mCurrentMovieUri;

    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Using the fancy Android data binding
        mMovieDetailBinding = DataBindingUtil
                .setContentView(this, R.layout.activity_movie_details);

        // getting the current object and its content via parcel
        Intent i = getIntent();
        mCurrentMovieUri = i.getData();
        final Movies currentMovie = i.getExtras().getParcelable("currentObject");

        mMovieTitle = currentMovie.getmOrignalTitle();
        mMovieOverview = currentMovie.getmOverview();

        // Getting the main movie poster
        Picasso.with(this).load(currentMovie.getmPosterURL())
                .resize(IMAGE_WIDTH, IMAGE_HEIGHT)
                .centerCrop()
                .into(mMovieDetailBinding.ivMovieDetailsPoster);

        // Getting the object content of our current object.
        Picasso.with(this).load(currentMovie.getmBackdropPathURL()).
                resize(IMAGE_WIDTH, IMAGE_HEIGHT).
                centerCrop().
                into(mMovieDetailBinding.ivMovieDetailsImage);
        mMovieDetailBinding.tvMovieDetailsOverview.setText(currentMovie.getmOverview());
        mMovieDetailBinding.tvMovieReview.setText(currentMovie.getmMovieReview());
        mMovieDetailBinding.tvMovieDetailsAverageRatings.
                setText(getString(R.string.movie_details_average_votes) + currentMovie.getmVoteAverage());
        mMovieDetailBinding.tvMovieDetailsReleaseDate.
                setText(getString(R.string.movie_details_release_date) + currentMovie.getmReleaseDate());
        mMovieDetailBinding.tvMovieReview.
                setText(currentMovie.getmMovieReview());
        mMovieDetailBinding.tvMovieTitle.setText(currentMovie.getmOrignalTitle());
        setTitle(currentMovie.getmOrignalTitle());
        mTrailer = new MovieTrailer(currentMovie.getmMovieID());
        mTrailer.execute();

        // Check if the movie is already saved in our database.
        isAlreadySaved();

        /*
        * Getting some basic movie information for the sharing function.
        * And set the whole information into the mMovieDetailsSharing string variable.
         */

        mMovieDetailsSharing = mMovieDetailBinding.tvMovieTitle.getText().toString() + " " +
                mMovieDetailBinding.tvMovieDetailsReleaseDate.getText().toString() + " "+
                mMovieDetailBinding.tvMovieDetailsAverageRatings.getText().toString() + " "+
                mMovieDetailBinding.tvMovieDetailsOverview.getText().toString();

        mMovieDetailBinding.faAddMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If the user clicked the fabbutton we will call the saveMovie function.
                if (!mMovieSaved){
                    saveMovie();
                }else {
                    Toast.makeText
                            (getBaseContext(), R.string.movie_details_already_saved,
                                    Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public Loader<List<Movies>> onCreateLoader(int i, Bundle bundle) {
        return null;
    }


    @Override
    public void onLoadFinished(Loader<List<Movies>> loader, List<Movies> moviesList) {

    }

    @Override
    public void onLoaderReset(Loader<List<Movies>> loader) {

    }

    private Intent createShareMovieDetailsIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mMovieDetailsSharing + MOVIE_DETAIL_SHARE_HASHTAG)
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuID = item.getItemId();
        switch (menuID){

            case R.id.menu_item_share:
                Intent shareIntent = createShareMovieDetailsIntent();
                startActivity(shareIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Here the whole movie data will be saved into our database
     */

    private void saveMovie (){

        byte[] imageByteArraySecondPoster;
        byte[] imageByteArrayMainPoster;

        ContentValues values = new ContentValues();

        values.put(
                DBContract.MoviesEntry.COLUMN_MOVIE_TITLE,
                mMovieDetailBinding.tvMovieTitle.getText().toString() );
        values.put(
                DBContract.MoviesEntry.COLUMN_MOVIE_RELEASE_DATE,
                mMovieDetailBinding.tvMovieDetailsReleaseDate.getText().toString());
        values.put(
                DBContract.MoviesEntry.COLUMN_MOVIE_REVIEW,
                mMovieDetailBinding.tvMovieReview.getText().toString());
        values.put(
                DBContract.MoviesEntry.COLUMN_MOVIE_CONTENT,
                mMovieDetailBinding.tvMovieDetailsOverview.getText().toString());
        values.put(
                DBContract.MoviesEntry.COLUMN_MOIVE_TRAILER, mMovieMainTrailer);

        // Saving the second image of the movie
        Bitmap imageBitMapMainPoster =
                ((BitmapDrawable) mMovieDetailBinding.ivMovieDetailsPoster
                        .getDrawable())
                        .getBitmap();
        ByteArrayOutputStream outputStreamMainPoster = new ByteArrayOutputStream();
        imageBitMapMainPoster.compress(Bitmap.CompressFormat.PNG,
                COMPRESS_FACTOR, outputStreamMainPoster);
        imageByteArrayMainPoster = outputStreamMainPoster.toByteArray();
        values.put(
                DBContract.MoviesEntry.COLUMN_MOVIE_POSTER, imageByteArrayMainPoster);

        // Saving the second image of the movie
         Bitmap imageBitMapSecondPoster =
                ((BitmapDrawable) mMovieDetailBinding.ivMovieDetailsImage
                        .getDrawable())
                        .getBitmap();
        ByteArrayOutputStream outputStreamSecondImage = new ByteArrayOutputStream();
        imageBitMapSecondPoster.compress(Bitmap.CompressFormat.PNG,
                COMPRESS_FACTOR, outputStreamSecondImage);
        imageByteArraySecondPoster = outputStreamSecondImage.toByteArray();
        values.put(
                DBContract.MoviesEntry.COLUMN_MOVIE_BACKPATH_POSTER, imageByteArraySecondPoster);
        values.put(
                DBContract.MoviesEntry.COLUMN_MOVIE_RATING,
                mMovieDetailBinding.tvMovieDetailsAverageRatings.getText().toString());

        if (mCurrentMovieUri == null) {

            Uri newUri = getContentResolver().insert(DBContract.MoviesEntry.CONTENT_URI, values);
            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, R.string.save_movie_detail_failed,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, R.string.save_movie_detail_sucess,
                        Toast.LENGTH_SHORT).show();
                mMovieDetailBinding.faAddMovie.setImageResource(R.drawable.ic_star_black_24dp);
                addNotification();
            }
        }
    }

    /**
     * Check if the movie is already saved in our database.
     * @return true if not and false if so.
     */

    public void isAlreadySaved() {
        String selection = DBContract.MoviesEntry.COLUMN_MOVIE_TITLE + " = ?";
        String[] projection = {
                DBContract.MoviesEntry._ID,
                DBContract.MoviesEntry.COLUMN_MOVIE_TITLE};
        String[] selectionArgs = {mMovieTitle};

        mCursor = getContentResolver().query(DBContract.MoviesEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);

        if (mCursor != null && mCursor.getCount() != 0) {
            // If the return value of the cursor is zero the movie is already saved.
            mMovieDetailBinding.faAddMovie.setImageResource(R.drawable.ic_star_black_24dp);
            mMovieSaved = true;
            mCursor.close();
        } else {
            mMovieDetailBinding.faAddMovie.setImageResource(R.drawable.ic_star_border_black_24dp);
            mMovieSaved = false;
            mCursor.close();
        }
    }

    /**
     * Added a small Android notification
     * I used the example from:
     * https://www.tutorialspoint.com/android/android_notifications.htm
     */

    public void addNotification(){
        NotificationCompat.Builder mNotification = new NotificationCompat.Builder(this);
        mNotification
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0))
                .setSmallIcon(R.drawable.ic_star_black_24dp)
                .setSubText(mMovieOverview)
                .setContentText((getString(R.string.movie_details_added_notification) +mMovieTitle))
                .setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
         // notificationID allows you to update the notification later on.
        mNotificationManager.notify(0, mNotification.build());
    }


    /**
     * The class for getting the trailer of the selected movie
     */

    public class MovieTrailer extends AsyncTask<String, Void, String>
    {
        public String mMovieID;
        private URL mMovieTrailerURL;
        private final static int mImageSizingFactor = 400;

        /**BASE URL**/
        private static final String BASE_URL = "https://api.themoviedb.org";

        /**Building the path **/
        private static final String MOVIES_STAGE ="3";

        private static final String MOVIES_FOLDER ="movie";

        private static final String VIDEO_FOLDER = "videos";

        private static final String MOVIE_JSON_RESULTS = "results";
        private static final String MOVIE_JSON_KEY = "key";

        public MovieTrailer (String movieID){
            mMovieID = movieID;
        }

        /**
         * Creating a proper URL for every movie trailer
         * @param mMovieID
         */
        private void createMovieTrailerUrl (String mMovieID) {
            Uri buildURI = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(MOVIES_STAGE)
                    .appendPath(MOVIES_FOLDER)
                    .appendPath(mMovieID)
                    .appendPath(VIDEO_FOLDER)
                    .appendQueryParameter("api_key", API_KEY)
                    .build();
            URL url = null;
            try {
                url = new URL(buildURI.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            mMovieTrailerURL = url;
        }

        @Override
        protected String doInBackground(String... strings) {
            String requestString = null;            // for our HTTP request
            createMovieTrailerUrl(mMovieID);
            try
            {
                requestString = makeHttpRequest(mMovieTrailerURL);
            }catch (IOException ex) {
                Log.i("MovieTrail ", "Error creating the video URL ", ex);
            }

            try{
                // Create a JSONObject from the JSON response string
                JSONObject baseJsonResponse = new JSONObject(requestString);
                JSONArray movieJSONReview = baseJsonResponse.getJSONArray(MOVIE_JSON_RESULTS);

                //In both of the ArrayLists we will store the content of the YouTube JSON
                //object.
                mTrailerPreviewImages = new ArrayList<String>();
                mTrailerURL = new ArrayList<String>();

                for (int i = 0; i != movieJSONReview.length(); i++){
                    JSONObject currentObject = movieJSONReview.getJSONObject(i);
                    mTrailerPreviewImages.add
                            (getmMovieTrailerPreviewImage(currentObject.getString(MOVIE_JSON_KEY)));
                    mTrailerURL.add
                            (getmMovieTrailerURL(currentObject.getString(MOVIE_JSON_KEY)));
                }

            } catch (JSONException e) {
                // If an error is thrown when executing any of the above statements in the "try" block,
                // catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                Log.e("MovieUtils", "Problem parsing the the movie video JSON results", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            // We creating a new trailerAdpaer with the URLs to the Youtube trailer images
            // and the the YouTube trailer URL
            final TrailerAdapter mTrailerAdapter =
                    new TrailerAdapter(getBaseContext(), mTrailerPreviewImages, mTrailerURL);

            // Here we expand the LinearLayout if we are getting ome more trailer.
            mMovieDetailBinding.llTrailer.setLayoutParams
                    (new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            mTrailerPreviewImages.size() * mImageSizingFactor));

            mMovieDetailBinding.gvTrailer.setAdapter(mTrailerAdapter);
            mMovieMainTrailer = mTrailerAdapter.getTrailerURL(0);

            //By clicking the image we will launch a browser intent.
            mMovieDetailBinding.gvTrailer.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mTrailerAdapter.getTrailerURL(i))));
                }
            });

            super.onPostExecute(s);
        }

        /**
         * Getting an image for the trailer preview.
         * @return the path to the image as a string.
         */
        public String getmMovieTrailerPreviewImage(String movieID)
        {
            final String TRAILER_BASE_URL = "https://i.ytimg.com/vi/";
            final String PATH = "/default.jpg";
            return TRAILER_BASE_URL + movieID + PATH;
        }

        /**
         * By using the current movies ID we are generating the proper Youtube URL
         * @param mMovieID
         * @return the URL for the Youtube clip as a String.
         */
        public String getmMovieTrailerURL(String mMovieID) {
            final String TRAILER_BASE_URL = "https://www.youtube.com/watch?v=";
            return TRAILER_BASE_URL + mMovieID;
        }
    }
}
