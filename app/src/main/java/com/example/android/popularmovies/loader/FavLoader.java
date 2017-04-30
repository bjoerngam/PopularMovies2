package com.example.android.popularmovies.loader;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.DBContract;
import com.example.android.popularmovies.databinding.FavItemBinding;

/**
 * Created by bjoern on 28.04.17.
 *
 * @author <a href="mailto:mail@bjoern.cologne">Bjoern Gam</a>
 * @link <a href="http://bjoern.cologne">Webpage </a>
 * <p>
 * Description: We use this loader for getting all of the data for our favorites.
 */
public class FavLoader extends CursorAdapter {

    private FavItemBinding mFavItemBinding;

    /***
     * The default constructor
     * @param context
     * @param c
     */
    public FavLoader(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.fav_item,
                viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        mFavItemBinding = DataBindingUtil.bind(view);

        int moviePoster = cursor.getColumnIndex
                (DBContract.MoviesEntry.COLUMN_MOVIE_BACKPATH_POSTER);

        int mainMoviePoster = cursor.getColumnIndex(
                DBContract.MoviesEntry.COLUMN_MOVIE_POSTER);

        mFavItemBinding.tvFavMovieTitle.setText(cursor.getString
                (cursor.getColumnIndex(DBContract.MoviesEntry.COLUMN_MOVIE_TITLE)));

        mFavItemBinding.tvFavMovieAverageRating.setText(cursor.getString
                (cursor.getColumnIndex(DBContract.MoviesEntry.COLUMN_MOVIE_RATING)));

        mFavItemBinding.tvFavMovieOverview.setText(cursor.getString
                (cursor.getColumnIndex(DBContract.MoviesEntry.COLUMN_MOVIE_CONTENT)));

        mFavItemBinding.tvYoutubeUrl.setText(cursor.getString
                (cursor.getColumnIndex(DBContract.MoviesEntry.COLUMN_MOIVE_TRAILER)));

        //Getting the first image of the movie out of the database.
        byte[] photoByte = cursor.getBlob(mainMoviePoster);
        Bitmap photoBitmap = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);
        mFavItemBinding.ivFavMovieMainPoster.setImageBitmap(photoBitmap);

        //Getting the second image of the movie out of the database.
        byte[] photoByte2 = cursor.getBlob(moviePoster);
        Bitmap photoBitmap2 = BitmapFactory.decodeByteArray(photoByte2, 0, photoByte2.length);
        mFavItemBinding.ivFavMoviePoster.setImageBitmap(photoBitmap2);
    }
}
