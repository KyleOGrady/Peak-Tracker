package kyle.peaktracker;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;

public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "peaks.db";
    private static final String TABLE_ADK = "adk_peaks";
    //Columns
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "_name";
    private static final String COLUMN_HEIGHT = "_height";
    private static final String COLUMN_CLIMBED = "_climbed";
    private static final String COLUMN_DATE = "_date";
    private static final String COLUMN_LIST = "_list";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        String query = "CREATE TABLE " + TABLE_ADK + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT " +
                COLUMN_NAME + " TEXT " +
                COLUMN_HEIGHT + " INTEGER " +
                COLUMN_CLIMBED + " INTEGER " +
                COLUMN_DATE + " TEXT " +
                COLUMN_LIST + " TEXT " +
                ");";

        db.execSQL(query);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADK);
        onCreate(db);
    }
}
