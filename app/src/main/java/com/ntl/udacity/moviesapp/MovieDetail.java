package com.ntl.udacity.moviesapp;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ntl.udacity.moviesapp.data.MovieContract;
import com.ntl.udacity.moviesapp.dataModels.Movie;
import com.ntl.udacity.moviesapp.dataModels.Review;
import com.ntl.udacity.moviesapp.dataModels.ReviewResponse;
import com.ntl.udacity.moviesapp.dataModels.Trailer;
import com.ntl.udacity.moviesapp.dataModels.TrailerResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetail extends AppCompatActivity
{
    private TextView title, rate, releaseDate, plot;
    private ImageView imageView;
    private String id;
    private String pic_url;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        title = findViewById(R.id.title);
        rate = findViewById(R.id.rate);
        releaseDate = findViewById(R.id.date);
        plot = findViewById(R.id.plot);
        imageView = findViewById(R.id.movie_pic);
        getData();
        if (Utility.isNetworkAvailable(this))
            getTrailersAndReviews();
        else
            Toast.makeText(MovieDetail.this, "Please connect to internet to get the latest data", Toast.LENGTH_LONG)
                    .show();


    }

    public void getData()
    {

        if (getIntent().hasExtra(MainActivity.MOVIES_KEY))
        {
            Movie movie = getIntent().getExtras().getParcelable(MainActivity.MOVIES_KEY);
            if (movie != null)
            {
                title.setText(movie.getTitle());
                rate.setText(movie.getRate());
                releaseDate.setText(movie.getReleaseDate());
                plot.setText(movie.getPlotSynopsis());
                id = movie.get_id();
                try
                {
                    //online
                    pic_url = movie.buildPicUrl();
                    Picasso.with(MovieDetail.this).load(pic_url).fit().placeholder(R.drawable.holder).into(imageView);
                } catch (Exception e)
                {
                    //offline
                    Bitmap bitmap = Utility.getImage(movie.getImage());
                    imageView.setImageBitmap(bitmap);
                }
            }

        }
    }

    public void getTrailersAndReviews()
    {
        MovieNetworkInterface networkInterface = MovieNetworkClient.getClient().create(MovieNetworkInterface.class);
        Call<TrailerResponse> trailerResponseCall = networkInterface.getMovieTrailers(id, MainActivity.API_KEY);
        trailerResponseCall.enqueue(new Callback<TrailerResponse>()
        {
            @Override
            public void onResponse(@NonNull Call<TrailerResponse> call, @NonNull Response<TrailerResponse> response)
            {
                addTrailersToView(response.body().getResults());
            }

            @Override
            public void onFailure(@NonNull Call<TrailerResponse> call, @NonNull Throwable t)
            {

            }
        });

        Call<ReviewResponse> reviewResponseCall = networkInterface.getMovieReviews(id, MainActivity.API_KEY);
        reviewResponseCall.enqueue(new Callback<ReviewResponse>()
        {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response)
            {
                addReviewsToView(response.body().getReviews());
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t)
            {

            }
        });
    }

    private void addReviewsToView(List<Review> response)
    {
        LinearLayout linearLayout = findViewById(R.id.reviews_list);
        int listSize = response.size();
        for (int i = 0; i < listSize; ++i)
        {
            View view = LayoutInflater.from(this).inflate(R.layout.review_item, null);
            TextView authorName = view.findViewById(R.id.author_tv);
            TextView content = view.findViewById(R.id.content_tv);
            final Review currItem = response.get(i);
            authorName.setText(currItem.getAuthor());
            content.setText(currItem.getContent());
            linearLayout.addView(view);
            view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(currItem.getUrl()));
                    startActivity(intent);
                }
            });
        }

    }

    private void addTrailersToView(List<Trailer> response)
    {

        LinearLayout linearLayout = findViewById(R.id.trailersList);
        int listSize = response.size();
        for (int i = 0; i < listSize; ++i)
        {
            View view = LayoutInflater.from(this).inflate(R.layout.trailer_item, null);
            TextView trailerTitle = view.findViewById(R.id.trailer_name);
            final Trailer currItem = response.get(i);
            trailerTitle.setText(currItem.getName());
            linearLayout.addView(view);
            view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(currItem.buildTrailerURL()));
                    startActivity(intent);
                }
            });
        }
    }

    public void addToFavourite(View view)
    {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry._ID, id);
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title.getText().toString());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RATE, rate.getText().toString());
        contentValues.put(MovieContract.MovieEntry.COLUMN_PLOTSYNOPSIS, plot.getText().toString());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASEDATE, releaseDate.getText().toString());
        contentValues.put(MovieContract.MovieEntry.COLUMN_IMAGE, Utility.getImageBytes(bitmap));
        Uri uri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(id).build();
        try
        {
            Uri resUri = getContentResolver().insert(uri, contentValues);
            if (resUri != null)
            {
                Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "you've inserted this movie before", Toast.LENGTH_LONG).show();
        }
    }

    public void removeFromFavourite(View view)
    {
        Uri uri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(id).build();
        try
        {
            int deleted = getContentResolver().delete(uri, null, null);
            if (deleted > 0)
                Toast.makeText(getBaseContext(), "movie has been successfully deleted", Toast.LENGTH_LONG).show();

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
