package com.example.android.popularmovies;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.popularmovies.adapter.MoviesAdapter;
import com.example.android.popularmovies.data.DBContract;
import com.example.android.popularmovies.data.Movies;
import com.example.android.popularmovies.loader.MovieLoader;
import com.example.android.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.popularmovies.R.id.gridView;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Movies>>{

    private MoviesAdapter mAdapter;

    private SharedPreferences prefs;

    private static final String[] MOVIE_DETAIL_PROJECTION = {
            DBContract.MoviesEntry.COLUMN_MOVIE_TITLE,
            DBContract.MoviesEntry.COLUMN_MOVIE_RELEASE_DATE,
            DBContract.MoviesEntry.COLUMN_MOVIE_REVIEW,
            DBContract.MoviesEntry.COLUMN_MOVIE_CONTENT,
            DBContract.MoviesEntry.COLUMN_MOVIE_RATING,
            DBContract.MoviesEntry.COLUMN_MOVIE_POSTER,
            DBContract.MoviesEntry.COLUMN_MOVIE_BACKPATH_POSTER
    };

    private GridView mGridview;
    private ProgressBar mProgressBar;

    private static final int MOVIE_LOADER_ID = 2;

    private static final int SORT_ORDER_HIGHEST_RATED = 1;
    private static final int SORT_ORDER_POPULAR = 2;
    private static final int SORT_ORDER_FAVORITES = 3;

    public URL COMPLETE_URL;

    private static final String MY_PREFS_NAME = "SharedPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridview = (GridView) findViewById(gridView);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_loader);

        mAdapter = new MoviesAdapter(this, new ArrayList<Movies>());

        mGridview.setEmptyView(findViewById(R.id.empty_view));

        //As default we will display the most popular movies
        switch (getSelectedPreference()) {
            case SORT_ORDER_HIGHEST_RATED:
                COMPLETE_URL = NetworkUtils.movieURL(SORT_ORDER_HIGHEST_RATED);
                break;
            case SORT_ORDER_POPULAR:
                COMPLETE_URL = NetworkUtils.movieURL(SORT_ORDER_POPULAR);
                break;
            case SORT_ORDER_FAVORITES:
                startFavMovies();
                break;
            default:
                COMPLETE_URL = NetworkUtils.movieURL(SORT_ORDER_POPULAR);
        }

        mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(MainActivity.this, MovieDetails.class);
                Movies currentMovie = mAdapter.getItem(position);
                intent.putExtra("currentObject",currentMovie);
                startActivity(intent);
            }
        });

        mGridview.setAdapter(mAdapter);
        if (isNetworkConnected()) {
            getLoaderManager().initLoader(MOVIE_LOADER_ID, null, MainActivity.this);
        }else Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
    }

    private boolean isNetworkConnected() {
        // The helper function for checking if the network connection is working.
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public Loader<List<Movies>> onCreateLoader(int i, Bundle bundle) {
        return new MovieLoader(this, COMPLETE_URL);
    }

    @Override
    public void onLoaderReset(Loader<List<Movies>> loader) {
        mAdapter.clear();
    }

    @Override
    public void onLoadFinished(Loader<List<Movies>> loader, List<Movies> moviesList) {
        // If there is a valid list of {@link moviesList}, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        mProgressBar.setVisibility(View.GONE);
        mAdapter.clear();

        if (moviesList != null && !moviesList.isEmpty()) {
            mAdapter.addAll(moviesList);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "prefence" option
            case R.id.action_preference:
                perferenceDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Creating the sharedPreferenceDialog
     */
    private void perferenceDialog(){
        final SharedPreferences.Editor editor =
                getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        AlertDialog.Builder perferenceBuilder = new AlertDialog.Builder(this);
        perferenceBuilder.setTitle(getString(R.string.menu_header));
        String[] types = {getString(R.string.highest_rated_movies_item)
                ,getString(R.string.popular_movies_item)
                , getString(R.string.favourite_movies_item)};

        perferenceBuilder.setItems(types, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface perferenceDialog, int selectedItem) {

                perferenceDialog.dismiss();
                switch(selectedItem){
                    case 0:
                        //Highest rated movies
                        editor.clear();
                        editor.putInt("selectedElement", 1);
                        editor.commit();
                        COMPLETE_URL = NetworkUtils.movieURL(SORT_ORDER_HIGHEST_RATED);
                        getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, MainActivity.this);
                        break;
                    case 1:
                        // Popular movies
                        editor.clear();
                        editor.putInt("selectedElement", 2);
                        editor.commit();
                        COMPLETE_URL = NetworkUtils.movieURL(SORT_ORDER_POPULAR);
                        getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, MainActivity.this);
                        break;
                    case 2:
                        //Local database
                        editor.clear();
                        editor.putInt("selectedElement", 3);
                        editor.commit();
                        startFavMovies();
                        break;
                }
            }

        });
        perferenceBuilder.show();
    }

    /**
     * Here we will check which preference was set.
     * @return the selected sharedPreference, the default value is 0
     */
    private int getSelectedPreference(){
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        int selectedPreference = prefs.getInt("selectedElement" ,0);
        return selectedPreference;
    }

    private void startFavMovies(){
        Intent intent = new Intent(MainActivity.this, FavMovies.class);
        startActivity(intent);
    }
}
