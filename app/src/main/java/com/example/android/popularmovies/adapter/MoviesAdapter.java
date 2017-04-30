package com.example.android.popularmovies.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movies;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by bjoern on 13.03.17.
 *
 * @author <a href="mailto:mail@bjoern.cologne">Bjoern Gam</a>
 * @link <a href="http://bjoern.cologne">Webpage </a>
 * <p>
 * Description: The adapter class for the popular movies project stage 2
 */
public class MoviesAdapter extends ArrayAdapter <Movies> {

    public MoviesAdapter (Context context, List<Movies> moviesList) {
        super(context, 0, moviesList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        Context context = getContext();

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.movie_item, parent, false);
            holder = new ViewHolder();
            holder.moviePoster = (ImageView) convertView.findViewById(R.id.iv_movieposter);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Movies currentMovie = getItem(position);
        // Suggestion by the Udacity review
        Picasso.with(context).load(currentMovie.getmPosterURL())
                .resize(220,300)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(holder.moviePoster);
        return convertView;
    }

    static class ViewHolder {
        //ViewHolder class
        ImageView moviePoster;
    }

}
