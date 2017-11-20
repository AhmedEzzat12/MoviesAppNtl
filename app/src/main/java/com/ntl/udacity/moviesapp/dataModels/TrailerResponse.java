package com.ntl.udacity.moviesapp.dataModels;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class TrailerResponse
{

    @SerializedName("id")
    private int movieId;
    @SerializedName("results")
    private List<Trailer> results;

    public int getMovieId()
    {
        return movieId;
    }

    public void setMovieId(int movieId)
    {
        this.movieId = movieId;
    }

    public List<Trailer> getResults()
    {
        return results;
    }

    public void setResults(List<Trailer> results)
    {
        this.results = results;
    }
}
