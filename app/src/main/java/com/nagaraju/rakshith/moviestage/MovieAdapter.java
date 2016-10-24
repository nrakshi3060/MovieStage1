package com.nagaraju.rakshith.moviestage;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Rakshith on 10/3/16.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    private Context mContext;
    private LayoutInflater mInflater;

 public MovieAdapter(Activity context, List<Movie> movies){
      super(context,0,movies);
    }



    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //mInflater = LayoutInflater.from(mContext);
        if(view == null){
            view = LayoutInflater.from(getContext()).inflate(
                    R.layout.grid_item, viewGroup, false);
        }

        Movie m = getItem(i);
        ImageView imageView = (ImageView) view.findViewById(R.id.picture);
        //imageView.setImageResource(m.getImage());
        //System.out.println(m.getPosterUrl());
        Picasso.with(getContext()).load(m.getPosterUrl()).into(imageView);

//        TextView textView = (TextView) view.findViewById(R.id.movieName_text);
//        textView.setText(m.getMovieName());
        return view;
    }


}
