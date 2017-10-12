package com.ntl.udacity.moviesapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;


class CustomAdapter extends BaseAdapter {


    private Context context;
    private List<Movie> movies;

    public CustomAdapter(Context c, List<Movie> m) {
        this.movies = m;
        this.context = c;
    }

    public void setMovies(List<Movie> m) {
        this.movies = m;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int i) {
        return movies.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View grid;

        if (view == null) {
            grid = inflater.inflate(R.layout.grid_item, null);
        } else {
            grid = view;
        }

        ImageView imageView = grid.findViewById(R.id.imageItem);
        Picasso.with(context).load(movies.get(i).getPicUrl()).placeholder(R.drawable.holder).fit().into(imageView);
        Log.d("adapter", movies.get(i).getTitle() + " " + movies.get(i).getPicUrl());
        return grid;
    }

}