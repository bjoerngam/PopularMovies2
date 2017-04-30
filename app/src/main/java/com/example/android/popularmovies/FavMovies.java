package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.popularmovies.data.DBContract;
import com.example.android.popularmovies.loader.FavLoader;

/**
 * Created by bjoern on 28.04.17.
 *
 * @author <a href="mailto:mail@bjoern.cologne">Bjoern Gam</a>
 * @link <a href="http://bjoern.cologne">Webpage </a>
 * <p>
 * Description: Within this class we will display the content of
 * local database / our fav. movies.
 */
public class FavMovies extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private FavLoader mFavLoader;
    private ListView mListView;

    private static final String MY_PREFS_NAME = "SharedPreferences";

    private static final int FAVORITE_MOVIES_LOADER_ID = 13;

    final String[] projection = new String[]{
            DBContract.MoviesEntry._ID,
            DBContract.MoviesEntry.COLUMN_MOVIE_TITLE,
            DBContract.MoviesEntry.COLUMN_MOVIE_RATING,
            DBContract.MoviesEntry.COLUMN_MOVIE_CONTENT,
            DBContract.MoviesEntry.COLUMN_MOVIE_RELEASE_DATE,
            DBContract.MoviesEntry.COLUMN_MOVIE_REVIEW,
            DBContract.MoviesEntry.COLUMN_MOVIE_POSTER,
            DBContract.MoviesEntry.COLUMN_MOVIE_BACKPATH_POSTER,
            DBContract.MoviesEntry.COLUMN_MOIVE_TRAILER
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_movies);

        mListView = (ListView) findViewById(R.id.lv_fav_list);

        mFavLoader = new FavLoader(this, null);

        mListView.setAdapter(mFavLoader);

        getSupportLoaderManager().initLoader(FAVORITE_MOVIES_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new android.support.v4.content.CursorLoader(this,
                DBContract.MoviesEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mFavLoader.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFavLoader.swapCursor(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuID = item.getItemId();
        switch (menuID){

            case R.id.action_favorites:
                deleteEveryFavMovie();
                break;
            case R.id.action_main_menu:
                setPrefValue();
                // After changing the Shared Prefs we start the main screen with
                // a default intent.
                Intent intent = new Intent(FavMovies.this, MainActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fav_menu, menu);
        return true;
    }

    /**
     * With this funcion the user can remove all of the stored movies.
     */
    public void deleteEveryFavMovie ()
    {
        int rows = getContentResolver().delete(DBContract.MoviesEntry.CONTENT_URI, null, null);

        if ( rows == 0){
            Toast.makeText(this,R.string.delete_all_items_error,Toast.LENGTH_LONG).show();
        }Toast.makeText(this, R.string.delete_all_items, Toast.LENGTH_LONG).show();
    }

    /**
     * We need this function for the case: If the favourite movies selection
     * was stored in our SharedPrefs. We will reset the sharedPreferences to 2
     * so its possible to see the default main screen.
     */
    public void setPrefValue(){
        final SharedPreferences.Editor editor =
                getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.clear();
        editor.putInt("selectedElement", 2);
        editor.commit();
    }
}
