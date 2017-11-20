package com.ntl.udacity.moviesapp.dataModels;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MoviesResponse
{

    @SerializedName("page")
    private int pageNum;
    @SerializedName("total_pages")
    private int totalPages;
    @SerializedName("results")
    private List<Movie> results;
    @SerializedName("total_results")
    private int totalResults;

    public int getPageNum()
    {
        return pageNum;
    }

    public void setPageNum(int pageNum)
    {
        this.pageNum = pageNum;
    }

    public int getTotalPages()
    {
        return totalPages;
    }

    public void setTotalPages(int totalPages)
    {
        this.totalPages = totalPages;
    }

    public List<Movie> getResults()
    {
        return results;
    }

    public void setResults(List<Movie> results)
    {
        this.results = results;
    }

    public int getTotalResults()
    {
        return totalResults;
    }

    public void setTotalResults(int totalResults)
    {
        this.totalResults = totalResults;
    }
}
