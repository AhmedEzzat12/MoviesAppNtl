package com.ntl.udacity.moviesapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ntl.udacity.moviesapp.dataModels.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;


class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MovieViewHolder>
{

    private static final String TAG = CustomAdapter.class.getSimpleName();
    final private ListItemClickListener mOnClickListener;
    private List<Movie> movies;
    private Context context;
    private boolean offline;

    public CustomAdapter(ListItemClickListener mOnClickListener)
    {
        this.mOnClickListener = mOnClickListener;
    }

    public void setMovies(List<Movie> m, boolean offline)
    {
        this.movies = m;
        this.offline = offline;
        notifyDataSetChanged();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.grid_item, null);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position)
    {
        holder.bind(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount()
    {
        if (movies != null)
            return movies.size();
        return 0;
    }

    public interface ListItemClickListener
    {
        void onListItemClick(int clickedItemIndex);
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        ImageView imageView;

        public MovieViewHolder(View itemView)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageItem);
            itemView.setOnClickListener(this);

        }

        void bind(int listIndex)
        {
            if (offline)
            {
                Bitmap bitmap = Utility.getImage(movies.get(listIndex).getImage());
                imageView.setImageBitmap(bitmap);
            } else
            {
                String imageUrl = movies.get(listIndex).buildPicUrl();
                Picasso.with(context).load(imageUrl).placeholder(R.drawable.holder).fit().into(imageView);
                Log.d(TAG, movies.get(listIndex).getTitle() + " " + imageUrl);

            }
        }

        @Override
        public void onClick(View v)
        {
            mOnClickListener.onListItemClick(getAdapterPosition());

        }
    }
}