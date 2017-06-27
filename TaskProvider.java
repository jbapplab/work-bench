package com.jbapplab.tasks.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by JohnB on 26/06/2017.
 */

public class TaskProvider extends ContentProvider { //ABSTRACT because otherwise we would need to implement 3 more methods of the content provider
    //Database columns
    public static final String COLUMN_TASKID = "_id";
    public static final String COLUMN_DATE_TIME = "task_date_time";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_TITLE = "title";

    //Database related Constants
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "data";
    public static final String DATABASE_TABLE = "tasks";

    //The database itself
    SQLiteDatabase db;

    @Override
    public boolean onCreate(){
        //Grab connection to our database
        db = new DatabaseHelper(getContext()).getWritableDatabase();
        return true;
    }

    //A helper class which nows how to create and update our database
    protected static class DatabaseHelper extends SQLiteOpenHelper{
        static final String DATABASE_CREATE =
                "create table " + DATABASE_TABLE + " (" + COLUMN_TASKID + " integer primary key autoincrement, " +
                        COLUMN_TITLE + " text not null, " +
                        COLUMN_NOTES + " text not null, " +
                        COLUMN_DATE_TIME + " integer not null);";

        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            throw new UnsupportedOperationException();
        }
    }

    //Content Provider URI and Authority
    public static final String AUTHORITY = "com.jbapplab.tasks.provider.TaskProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/task");

    //MIME types used for listing tasks or looking up a single task
    private static final String TASKS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.com.jbapplabs.tasks.tasks";
    private static final String TASK_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd.com.jbapplabs.tasks.task";

    //UriMatcher stuff
    private static final int LIST_TASK = 0;
    private static final int ITEM_TASK = 1;
    private static final UriMatcher URI_MATCHER = buildUriMatcher();

    //builds up a UriMatcher for search suggestion and shortcut refresh queries
    private static UriMatcher buildUriMatcher(){
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, "task", LIST_TASK);
        matcher.addURI(AUTHORITY, "task/#", ITEM_TASK);
        return matcher;
    }

    //method required in order to query the supported types
    @Override
    public String getType(Uri uri){
        switch (URI_MATCHER.match(uri)){
            case LIST_TASK:
                return TASKS_MIME_TYPE;
            case ITEM_TASK:
                return TASK_MIME_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri:" + uri);
        }
    }

    //CREATE method
    @Override
    public Uri insert(Uri uri, ContentValues values){ //identifies which table to insert into CONTENT_URI - valuse is a hashmap with task title and notes
        //you can't choose your own task id
        if(values.containsKey(COLUMN_TASKID))
            throw new UnsupportedOperationException();

        long id = db.insertOrThrow(DATABASE_TABLE, null, values); //insert or throw typically thows an exception when phone storage is full.
        getContext().getContentResolver().notifyChange(uri, null); //notifyChange is for editing the task list
        return ContentUris.withAppendedId(uri, id);
    }

    //UPDATE method for contentprovider
    @Override
    public int update(Uri uri, ContentValues values, String ignored1, String[] ignored2){
        //can't change task id
        if(values.containsKey(COLUMN_TASKID))
            throw new UnsupportedOperationException();

        int count = db.update(
                DATABASE_TABLE,
                values,
                COLUMN_TASKID+"=?",
                // ^ specifies the WHERE clause to the SQL query. In this case the WHERE clause will be "_id=?",
                // indicating that you want to update the row that has an _id of ?. The ? will be replaced by the value of line 16.
                new String[]{Long.toString(ContentUris.parseId(uri))}
                // ^ each ? in the where clause will be replaced by the respective entry from the String array,
                // so there should always be exactly as many question marks in the
                //where clause as there are items in the String array.
        );

        if(count>0)
            getContext().getContentResolver().notifyChange(uri, null); //if anything changes notify any listeners
        return count; //returns count of items update. It should only be 0 or 1.
    }

    //DELETE method - delete something from the content provider
    @Override
    public int delete(Uri uri, String ignored1, String[] ignored2){
        int count = db.delete(
                DATABASE_TABLE,
                COLUMN_TASKID+"=?",
                new String[]{Long.toString(ContentUris.parseId(uri))}
        );

        if(count>0)
            getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    //READ method - implementing queries - reading something from the content provider. We ask the dp for info and we return it in a Cursor
    @Override
    public Cursor query(Uri uri, String[] ignored1, String selection, String[] selectionArgs, String sortOrder){ //query takes a uri that represents the content to be queried. The selection parameter
        // specifies an optional where clause (title=?), and the selectionArgs parameter is an array of strings that fill any question marks in that election parameter. sortOrder indicates how results should be sorted.
        String [] projection = new String[]{
                COLUMN_TASKID,
                COLUMN_TITLE,
                COLUMN_NOTES,
                COLUMN_DATE_TIME};

        Cursor c;
        switch (URI_MATCHER.match(uri)){
            case LIST_TASK:
                c = db.query(DATABASE_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case ITEM_TASK:
                c = db.query(DATABASE_TABLE, projection, COLUMN_TASKID+ "=?", new String[]{Long.toString(ContentUris.parseId(uri))}, null, null, null, null);

                if(c.getCount()>0){
                    c.moveToFirst();
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: "+uri);
        }
        c.setNotificationUri(getContext().getContentResolver(), uri); //sets thenotification URI for this cursor. This URI must agree with the URIs we used in insert, update and delete. The loader
        //uses this URI to watch for any changes to the data; and if the data changes, the loader automatically refreshes the UI
        return c;
    }
}
