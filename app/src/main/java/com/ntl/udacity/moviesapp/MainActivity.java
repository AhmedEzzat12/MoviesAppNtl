package com.ntl.udacity.moviesapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ntl.udacity.moviesapp.data.MovieContract;
import com.ntl.udacity.moviesapp.dataModels.Movie;
import com.ntl.udacity.moviesapp.dataModels.MoviesResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener, CustomAdapter.ListItemClickListener
        , LoaderManager.LoaderCallbacks<Cursor>
{

    //Api key is here
    public static final String API_KEY = "0ad818053218184a4f89716fa901d427";
    public static final String MOVIES_KEY = "movies";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MOVIE_LOADER_ID = 0;
    private List<Movie> movies = new ArrayList<>();
    private CustomAdapter customAdapter;
    private String sortby;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        customAdapter = new CustomAdapter(this);
        RecyclerView gridView = findViewById(R.id.gridView);
        gridView.setLayoutManager(new GridLayoutManager(this, 2));
        gridView.setHasFixedSize(true);
        gridView.setAdapter(customAdapter);

        if (savedInstanceState != null)
        {
            sortby = savedInstanceState.getString("setting");
            movies = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
            if (sortby.equals(getString(R.string.favourites)))
                customAdapter.setMovies(movies, true);
            else
                customAdapter.setMovies(movies, false);
        } else
        {
            sortby = sharedPreference.getString(getString(R.string.key_pref_movie_type), getString(R.string.popular));
            if (Utility.isNetworkAvailable(MainActivity.this))
            {
                if (sortby.equals(getString(R.string.favourites)))
                {
                    getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, MainActivity.this);
                } else
                    getDataFromApi(sortby);
            } else
            {
                Toast.makeText(MainActivity.this, "Please connect to internet to get the latest data", Toast.LENGTH_LONG)
                        .show();
            }

        }

        sharedPreference.registerOnSharedPreferenceChangeListener(this);
    }

    private void getDataFromApi(String choice)
    {
        MovieNetworkInterface networkInterface = MovieNetworkClient.getClient().create(MovieNetworkInterface.class);
        Call<MoviesResponse> moviesResponseCall = null;
        if (choice.equals(getString(R.string.toprated)))
        {
            moviesResponseCall = networkInterface.getTopRatedMovies(API_KEY);

        } else if (choice.equals(getString(R.string.popular)))
        {
            moviesResponseCall = networkInterface.getPopularMovies(API_KEY);

        }

        if (moviesResponseCall != null)
        {
            moviesResponseCall.enqueue(new Callback<MoviesResponse>()
            {
                @Override
                public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response)
                {
                    movies.clear();
                    movies = response.body().getResults();
                    customAdapter.setMovies(movies, false);
                    Log.d(TAG, "Number of movies received: " + movies.size());
                }

                @Override
                public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t)
                {
                    Log.e(TAG, t.toString());
                }
            });
        }

    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        String sortby = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString(getString(R.string.key_pref_movie_type), getString(R.string.popular));

        savedInstanceState.putString("setting", sortby);
        savedInstanceState.putParcelableArrayList(MOVIES_KEY, (ArrayList<? extends Parcelable>) movies);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        if (item.getItemId() == R.id.menu_item_settings)
        {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {

        if (key.equals(getString(R.string.key_pref_movie_type)))
        {
            movies.clear();
            sortby = sharedPreferences.getString(key, getString(R.string.popular));
            if (sortby.equals(getString(R.string.favourites)))
                getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, MainActivity.this);
            else
            {
                if (Utility.isNetworkAvailable(MainActivity.this))
                    getDataFromApi(sortby);
                else
                    Toast.makeText(MainActivity.this, "Please connect to internet to get the latest data", Toast.LENGTH_LONG)
                            .show();

            }

        }

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume()
    {
        if (sortby.equals(getString(R.string.favourites)))
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, MainActivity.this);
        super.onResume();
    }

    @Override
    public void onListItemClick(int clickedItemIndex)
    {
        Log.d(TAG, movies.get(clickedItemIndex).get_id());
        startActivity(new Intent(MainActivity.this, MovieDetail.class)
                .putExtra(MOVIES_KEY, movies.get(clickedItemIndex)));
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        return new AsyncTaskLoader<Cursor>(this)
        {
            Cursor mTaskData = null;

            @Override
            public Cursor loadInBackground()
            {
                try
                {
                    return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

                } catch (Exception e)
                {
                    Log.e(TAG, "Failed");
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Cursor data)
            {
                mTaskData = data;
                super.deliverResult(data);
            }

            @Override
            protected void onStartLoading()
            {
                if (mTaskData != null)
                {
                    deliverResult(mTaskData);
                } else
                {
                    forceLoad();
                }

            }

        };
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data)
    {
        movies.clear();
        movies = parseCursorData(data);
        customAdapter.setMovies(movies, true);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader)
    {
        movies.clear();

    }


    private List<Movie> parseCursorData(Cursor data)
    {
        List<Movie> movies_cursor = new ArrayList<>();
        int idIndex = data.getColumnIndex(MovieContract.MovieEntry._ID);
        int titleIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
        int releaseDateIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASEDATE);
        int plotIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_PLOTSYNOPSIS);
        int imageIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE);
        int rateIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATE);
        if (data.moveToFirst())
        {
            do
            {
                Movie movie = new Movie();
                movie.set_id(String.valueOf(data.getInt(idIndex)));
                movie.setTitle(data.getString(titleIndex));
                movie.setPlotSynopsis(data.getString(plotIndex));
                movie.setRate(data.getString(rateIndex));
                movie.setReleaseDate(data.getString(releaseDateIndex));
                movie.setImage(data.getBlob(imageIndex));
                movies_cursor.add(movie);

            } while (data.moveToNext());
        }
        return movies_cursor;
    }

}


