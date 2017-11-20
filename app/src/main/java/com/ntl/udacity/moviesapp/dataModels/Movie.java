package com.ntl.udacity.moviesapp.dataModels;


import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Movie implements Parcelable
{

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>()
    {

        @Override
        public Movie createFromParcel(Parcel source)
        {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size)
        {
            return new Movie[size];
        }
    };
    @SerializedName("title")
    private String title;
    @SerializedName("vote_average")
    private String rate;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("overview")
    private String plotSynopsis;
    @SerializedName("poster_path")
    private String picUrl;
    @SerializedName("id")
    private String _id;
    private byte[] image;

    private Movie(Parcel in)
    {
        title = in.readString();
        rate = in.readString();
        releaseDate = in.readString();
        plotSynopsis = in.readString();
        picUrl = in.readString();
        _id = in.readString();
        this.image = new byte[in.readInt()];
        in.readByteArray(this.image);
    }

    public Movie()
    {

    }

    public byte[] getImage()
    {
        return image;
    }

    public void setImage(byte[] image)
    {
        this.image = image;
    }

    public String get_id()
    {
        return _id;
    }

    public void set_id(String _id)
    {
        this._id = _id;
    }

    public String getPicUrl()
    {

        return picUrl;
    }

    public void setPicUrl(String picUrl)
    {
        this.picUrl = picUrl;
    }

    public String buildPicUrl()
    {
        String picPath = this.picUrl;
        final String MOVIE_IMAGE_BASE_URL =
                "https://image.tmdb.org/t/p";
        final String SIZE_PARAM = "w500";
        final String PATH_PARAM = picPath.substring(1);
        return Uri.parse(MOVIE_IMAGE_BASE_URL)
                .buildUpon()
                .appendPath(SIZE_PARAM)
                .appendPath(PATH_PARAM)
                .build().toString();

    }

    public String getRate()
    {
        return rate;
    }

    public void setRate(String rate)
    {
        this.rate = rate;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getReleaseDate()
    {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate)
    {
        this.releaseDate = releaseDate;
    }

    public String getPlotSynopsis()
    {
        return plotSynopsis;
    }

    public void setPlotSynopsis(String plotSynopsis)
    {
        this.plotSynopsis = plotSynopsis;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(title);
        dest.writeString(rate);
        dest.writeString(releaseDate);
        dest.writeString(plotSynopsis);
        dest.writeString(picUrl);
        dest.writeString(_id);
        if (image != null)
        {
            dest.writeInt(image.length);
            dest.writeByteArray(image);
        }
    }
}
