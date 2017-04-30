package com.example.android.popularmovies.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.popularmovies.data.Movies;
import com.example.android.popularmovies.utils.MovieUtils;

import java.net.URL;
import java.util.List;

/**
 * Created by bjoern on 13.03.17.
 *
 * @author <a href="mailto:mail@bjoern.cologne">Bjoern Gam</a>
 * @link <a href="http://bjoern.cologne">Webpage </a>
 * <p>
 * Description: The AsynTask implementation of my popular movies project
 */
public class MovieLoader extends AsyncTaskLoader<List<Movies>> {

    /** Tag for log messages */
    private static final String LOG_TAG = MovieLoader.class.getName();

    private URL mURL;


    public MovieLoader(Context context, URL url){
        super(context);
        mURL = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movies> loadInBackground() {
        if (mURL == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of moview.
        List<Movies> movie = MovieUtils.fetchMovieData(mURL.toString());
        return movie;
    }
}
