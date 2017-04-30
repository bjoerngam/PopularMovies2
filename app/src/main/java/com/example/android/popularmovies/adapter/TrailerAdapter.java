package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by bjoern on 30.04.17.
 *
 * @author <a href="mailto:mail@bjoern.cologne">Bjoern Gam</a>
 * @link <a href="http://bjoern.cologne">Webpage </a>
 * <p>
 * Description: With this adapter we assigning the value to the
 * correct UI elements.
 */
public class TrailerAdapter extends ArrayAdapter <String>
{
    private ArrayList <String> mTrailerImages;
    private ArrayList <String> mtrailerURL;

    public TrailerAdapter(Context context, ArrayList <String> trailerImages, ArrayList <String> trailerURL){
        super(context, 0, trailerImages);
        mTrailerImages = trailerImages;
        mtrailerURL = trailerURL;
    }

    public String getTrailerURL (int mMovie){
        return mtrailerURL.get(mMovie);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        Context context = getContext();

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.trailer_item, parent, false);
            holder = new ViewHolder();
            holder.movieTrailerPreviewImage =
                    (ImageView) convertView.findViewById(R.id.iv_movie_trailer);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
            Picasso.with(this.getContext())
                    .load(mTrailerImages.get(position).toString())
                    .resize(400,300)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .into(holder.movieTrailerPreviewImage);
        return convertView;
    }

    static class ViewHolder {
        //ViewHolder class
        ImageView movieTrailerPreviewImage;
    }
}
