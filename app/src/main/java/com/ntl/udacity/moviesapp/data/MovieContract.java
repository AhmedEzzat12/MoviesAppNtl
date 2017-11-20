package com.ntl.udacity.moviesapp.data;


import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract
{

    public static final String AUTHORITY = "com.ntl.udacity.moviesapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();


        // Task table and column names
        public static final String TABLE_NAME = "movies";


        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RATE = "rate";
        public static final String COLUMN_RELEASEDATE = "releasedate";
        public static final String COLUMN_PLOTSYNOPSIS = "plotsyn";
        public static final String COLUMN_IMAGE = "imagecol";


    }

}
