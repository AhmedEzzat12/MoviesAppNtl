package com.ntl.udacity.moviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //Api key is here
    private static final String API_KEY = "";
    private List<Movie> movies = new ArrayList<>();
    private CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String sortby;
        if (savedInstanceState != null) {
            sortby = savedInstanceState.getString("setting");
        } else {
            sortby = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .getString(getString(R.string.key_pref_movie_type), getString(R.string.popular));
        }

        customAdapter = new CustomAdapter(getApplicationContext(), movies);


        if (Utility.isNetworkAvailable(MainActivity.this)) {


            GridView gridView = (GridView) findViewById(R.id.gridView);


            if (sortby.equals(getString(R.string.popular))) {
                getDataFromApiIfPopular();
            } else if (sortby.equals(getString(R.string.toprated))) {
                getDataFromApiIfTopRated();
            }
            gridView.setAdapter(customAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Bundle bundle = new Bundle();
                    bundle.putString("title", movies.get(i).getTitle());
                    bundle.putString("rate", movies.get(i).getRate());
                    bundle.putString("date", movies.get(i).getReleaseDate());
                    bundle.putString("plot", movies.get(i).getPlotSynopsis());
                    bundle.putString("url", movies.get(i).getPicUrl());
                    bundle.putString("id", movies.get(i).get_id());
                    Log.d("mainID", movies.get(i).get_id());
                    startActivity(new Intent(MainActivity.this, MovieDetail.class).putExtra("bundle", bundle));
                }
            });
        } else
            Toast.makeText(MainActivity.this, "please connect to the internet to get data", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onResume() {

        String sortBy = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString(getString(R.string.key_pref_movie_type), getString(R.string.popular));

        if (sortBy.equals(getString(R.string.popular))) {
            getDataFromApiIfPopular();
        } else if (sortBy.equals(getString(R.string.toprated))) {
            getDataFromApiIfTopRated();
        }
        super.onResume();
    }

    private void getDataFromApiIfTopRated() {
        String url = "http://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY;
        // Get a RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                movies.clear();
                movies = parseJson(response.toString());
                customAdapter.setMovies(movies);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Main", error.toString());
            }
        });

        queue.add(jsonObjReq);

    }

    private void getDataFromApiIfPopular() {
        String url = "http://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY;
        // Get a RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                movies.clear();
                movies = parseJson(response.toString());
                customAdapter.setMovies(movies);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Main", error.toString());
            }
        });

        queue.add(jsonObjReq);

    }

    private List<Movie> parseJson(String jsonStr) {
        List<Movie> listOfMovies = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray results = jsonObject.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                Movie movie = new Movie();
                JSONObject movieObj = results.getJSONObject(i);
                String title = movieObj.getString("original_title");
                String rate = movieObj.getString("vote_average");
                String releaseDate = movieObj.getString("release_date");
                String baseURL = "https://image.tmdb.org/t/p/w500/";
                String AlbumPicUrl = baseURL + movieObj.getString("poster_path");
                String plot = movieObj.getString("overview");
                String _id = movieObj.getString("id");
                movie.setTitle(title);
                movie.setRate(rate);
                movie.setReleaseDate(releaseDate);
                movie.setPicUrl(AlbumPicUrl);
                movie.setPlotSynopsis(plot);
                movie.set_id(_id);
                listOfMovies.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listOfMovies;
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        String sortby = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString(getString(R.string.key_pref_movie_type), getString(R.string.popular));

        savedInstanceState.putString("setting", sortby);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_item_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}


