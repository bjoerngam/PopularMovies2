package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by bjoern on 10.04.17.
 *
 * @author <a href="mailto:mail@bjoern.cologne">Bjoern Gam</a>
 * @link <a href="http://bjoern.cologne">Webpage </a>
 * <p>
 * Description: Here we will create the database or updating
 * the database.
 */
public class DBHelper extends SQLiteOpenHelper{

    //Name of the database file
    private static final String DATABASE_NAME = "pmovies.db";

    //Database version. If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 5;

    //Constructor
    public DBHelper(Context context){

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + DBContract.MoviesEntry.TABLE_NAME +
                " (" + DBContract.MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DBContract.MoviesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                DBContract.MoviesEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, "+
                DBContract.MoviesEntry.COLUMN_MOVIE_REVIEW + " TEXT NOT NULL, " +
                DBContract.MoviesEntry.COLUMN_MOVIE_CONTENT + " TEXT NOT NULL, "+
                DBContract.MoviesEntry.COLUMN_MOIVE_TRAILER + " TEXT NOT NULL, "+
                DBContract.MoviesEntry.COLUMN_MOVIE_POSTER + " BLOB, " +
                DBContract.MoviesEntry.COLUMN_MOVIE_BACKPATH_POSTER + " BLOB, "+
                DBContract.MoviesEntry.COLUMN_MOVIE_RATING + " TEXT NOT NULL)";

        // Creating the database
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldDatabase, int newDatabase) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBContract.MoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
