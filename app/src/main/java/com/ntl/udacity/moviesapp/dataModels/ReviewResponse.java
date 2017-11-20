package com.ntl.udacity.moviesapp.dataModels;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewResponse
{
    @SerializedName("id")
    private String id;
    @SerializedName("results")
    private List<Review> reviews;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public List<Review> getReviews()
    {
        return reviews;
    }

    public void setReviews(List<Review> reviews)
    {
        this.reviews = reviews;
    }
}
