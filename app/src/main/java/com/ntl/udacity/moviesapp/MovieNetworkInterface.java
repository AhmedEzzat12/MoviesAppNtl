package com.ntl.udacity.moviesapp;


import com.ntl.udacity.moviesapp.dataModels.MoviesResponse;
import com.ntl.udacity.moviesapp.dataModels.ReviewResponse;
import com.ntl.udacity.moviesapp.dataModels.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieNetworkInterface
{

    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/popular")
    Call<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<TrailerResponse> getMovieTrailers(@Path("id") String id, @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<ReviewResponse> getMovieReviews(@Path("id") String id, @Query("api_key") String apiKey);

}
