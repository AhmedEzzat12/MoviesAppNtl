package com.ntl.udacity.moviesapp.dataModels;


import android.net.Uri;

public class Trailer
{
    private String name;
    private String key;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String buildTrailerURL()
    {
        String baseUrl = "https://www.youtube.com/watch";
        return Uri.parse(baseUrl).buildUpon().appendQueryParameter("v", key).build().toString();

    }
}
