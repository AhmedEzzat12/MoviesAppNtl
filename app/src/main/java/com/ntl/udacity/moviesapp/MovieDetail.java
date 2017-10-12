package com.ntl.udacity.moviesapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetail extends AppCompatActivity {
    private TextView title, rate, releaseDate, plot;
    private ImageView imageView;
    private String id;
    private String pic_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        title = (TextView) findViewById(R.id.title);
        rate = (TextView) findViewById(R.id.rate);
        releaseDate = (TextView) findViewById(R.id.date);
        plot = (TextView) findViewById(R.id.plot);
        imageView = (ImageView) findViewById(R.id.movie_pic);
        getData();

    }

    public void getData() {
        if (getIntent().getExtras().getBundle("bundle") != null) {
            Bundle intentBundle = getIntent().getExtras().getBundle("bundle");
            title.setText(intentBundle.getString("title"));
            rate.setText(intentBundle.getString("rate"));
            releaseDate.setText(intentBundle.getString("date"));
            plot.setText(intentBundle.getString("plot"));
            id = intentBundle.getString("id");
            pic_url = intentBundle.getString("url");
            Picasso.with(MovieDetail.this).load(intentBundle.getString("url")).fit().placeholder(R.drawable.holder).into(imageView);
        }
    }
}
