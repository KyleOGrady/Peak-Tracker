package kyle.peaktracker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseAccess {

    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;
    private static final String TABLE_ADK = "adk_peaks";

    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public String databaseToString(){
        String dbString = "";
        String query = "SELECT * FROM " + TABLE_ADK + " WHERE 1";

        Cursor c = database.rawQuery(query, null);

        c.moveToFirst();

        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("_name"))!= null){
                dbString += c.getString(c.getColumnIndex("_name"));
                dbString += "\n";
            }
        }
        c.close(); //Added
        //database.close();
        return dbString;
    }

}
