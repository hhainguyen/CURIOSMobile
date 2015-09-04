package com.mapbox.mapboxsdk.android.testapp.DB;

/**
 * Created by hainguyen on 23/06/2014.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mapbox.mapboxsdk.android.testapp.Place;

import java.util.LinkedList;
import java.util.List;

public class PlaceDB extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    public static final String DATABASE_NAME = "CURIOSDB";
    public static final String TABLE_NAME = "Places";
    private static final String[] COLUMNS = {"id", "title", "desc", "lat", "lng","suggested"};
    public PlaceDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME +" ( " +
                "id INTEGER PRIMARY KEY , " +
                "title TEXT, "+
                "desc TEXT, " +
                "type TEXT, " +
                "lat REAL, " +
                "lng REAL, " +
                "suggested INT" +
                " )";

        // create books table
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME );

        // create fresh books table
        this.onCreate(db);
    }

    public void addPlace(Place place){

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("id", Integer.parseInt(place.getID()));
        values.put("title", place.getTitle());
        values.put("desc", place.getDesc());
        values.put("type", place.getType());
        values.put("lat",place.getLat());
        values.put("lng",place.getLng());
        values.put("suggested",place.isSuggested());

        // 3. insert
        db.insert(TABLE_NAME , // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public Place getPlace(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_NAME , // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        Place place = new Place(cursor.getString(0)
                ,cursor.getString(1)
                ,cursor.getString(2)
                ,cursor.getString(3)

                ,cursor.getDouble(4)
                ,cursor.getDouble(5)
                ,cursor.getInt(6)
        );

        return place;
    }

    public List<Place> getSuggestedPlaces(){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_NAME , // a. table
                        COLUMNS, // b. column names
                        " suggested > ?", // c. selections
                        new String[] { "0" }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        List<Place> places = new LinkedList<Place>();

        // 3. go over each row, build book and add it to list
        Place place = null;
        if (cursor.moveToFirst()) {
            do {
                place = new Place(cursor.getString(0)
                        ,cursor.getString(1)
                        ,cursor.getString(2)
                        ,cursor.getString(3)
                        ,cursor.getDouble(4)
                        ,cursor.getDouble(5)
                        ,cursor.getInt(6)
                );

                // Add book to books
                places.add(place);
            } while (cursor.moveToNext());
        }
        return places;
    }

    // Get All Books
    public List<Place> getAllPlaces() {
        List<Place> places = new LinkedList<Place>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_NAME;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Place place = null;
        if (cursor.moveToFirst()) {
            do {
                 place = new Place(cursor.getString(0)
                        ,cursor.getString(1)
                        ,cursor.getString(2)
                        ,cursor.getString(3)

                        ,cursor.getDouble(4)
                        ,cursor.getDouble(5)
                        ,cursor.getInt(6)
                );

                // Add book to books
                places.add(place);
            } while (cursor.moveToNext());
        }


        return places;
    }
    public int updatePlace(Place place) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("title", place.getTitle()); // get title
        values.put("desc", place.getTitle()); // get author

        // 3. updating row
        int i = db.update(TABLE_NAME, //table
                values, // column/value
                " id = ?", // selections
                new String[] { String.valueOf(place.getID()) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    public void deletePlace(Place place) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_NAME,
                " id = ?",
                new String[] { String.valueOf(place.getID()) });

        // 3. close
        db.close();


    }
}