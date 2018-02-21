package kyle.peaktracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import static android.provider.BlockedNumberContract.BlockedNumbers.COLUMN_ID;

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
        Log.d("DATABASE ACCESS", "opened database");
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
            Log.d("DATABASE ACCESS", "closed database");
        }
    }

    public String databaseToString(){
        String dbString = "";
        String query = "SELECT * FROM " + TABLE_ADK + " WHERE 1";

        Cursor c = database.rawQuery(query, null);

        c.moveToFirst();

        dbString += c.getString(c.getColumnIndex("_name"));

        c.close(); //Added
        return dbString;
    }

    public double calculate_completion(String tableName){

        String query = "SELECT _climbed FROM " + tableName;

        Cursor c = database.rawQuery(query, null);
        String temp_compare = ""; //placeholder for N or Y
        double num_hiked = 0.0;
        double total;
        double perc;
        if(tableName.equals("ne_peaks")){
            total = 115.0;
        }else if(tableName.equals("adk_peaks")){
            total = 46.0;
        }else if(tableName.equals("nh_peaks")){
            total = 48.0;
        }else{
            total = 0.0;
        }

        c.moveToFirst();
        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("_climbed"))!= null){
                temp_compare = c.getString(c.getColumnIndex("_climbed"));
                Log.d("TEMP COMPARE", temp_compare);
                if(temp_compare.equals("Y")){
                    num_hiked++;
                    Log.d("NUM HIKED", Double.toString(num_hiked));
                }
                c.moveToNext();
            }
        }

        perc = (num_hiked/total)*100;
        Log.d("PERCENTAGE", Double.toString(perc));
        c.close();
        return perc;
    }

    public List<Peak> populatePeaks(String tableName, String orderBy){
        List<Peak> peaksList = new ArrayList<>();
        String query = "SELECT * FROM " + tableName + " ORDER BY " + orderBy + ";";

        Cursor c = database.rawQuery(query, null);

        while(c.moveToNext()) {
            Peak temp = new Peak();
            for (int i = 0; i < c.getColumnCount()+1; i++) {
                switch(i){
                    case 0: //ID
                        temp.set_id(c.getInt(i));
                        Log.d("SET OBJECT", "Setting ID to " + c.getInt(i));
                        break;
                    case 1: // NAME
                        temp.set_name(c.getString(i));
                        Log.d("SET OBJECT", "Setting name to " + c.getString(i));
                        break;
                    case 2: //HEIGHT
                        temp.set_height(c.getInt(i));
                        Log.d("SET OBJECT", "Setting height to " + c.getInt(i));
                        break;
                    case 3: //CLIMBED
                        temp.set_climbed(c.getString(i));
                        Log.d("SET OBJECT", "Setting climbed to " + c.getString(i));
                        break;
                    case 4: //DATE
                        temp.set_date(c.getString(i));
                        Log.d("SET OBJECT", "Setting date to " + c.getString(i));
                        break;
                    case 5: //LIST
                        temp.set_list(c.getString(i));
                        Log.d("SET OBJECT", "Setting list to " + c.getString(i));
                        break;
                    case 6: //COMMENTS
                        temp.set_comments(c.getString(i));
                        Log.d("SET OBJECT", "Setting comments to " + c.getString(i));
                        break;
                    case 7: //IMAGE
                        byte[] blob = c.getBlob(i);
                        if(blob != null) {
                            Bitmap image = BitmapFactory.decodeByteArray(blob, 0, blob.length);
                            temp.set_image(image);
                            Log.d("SET OBJECT", "Setting image.");
                        } else {
                            Log.d("SET OBJECT", "No image to set.");
                        }
                        break;
                    case 8:
                        peaksList.add(temp);
                        Log.d("ADDING OBJECT", "Adding " + temp.get_name() + " to peaksList.");
                        break;
                }
            }
        }

        c.close();
        return peaksList;
    }

    public void claimPeak(String peakName, String date, String comments, byte[] image, String tableName){

        insert_image(tableName, peakName, image);

        String query = "UPDATE " + tableName + " SET _climbed = 'Y', _date = '" + date +
                       "', _comments = '" + comments + "' WHERE _name = '" + peakName + "';";

        database.execSQL(query);
    }

    public void insert_image(String tableName, String peakName, byte[] image){
        ContentValues cv = new ContentValues();
        cv.put("_image", image);
        String[] whereArgs = new String[] {String.valueOf(peakName)};
        database.update(tableName, cv, "_name=?", whereArgs);
    }

    public Bitmap query_image(String tableName, String peakName){
        String query = "SELECT _image FROM " + tableName + " WHERE _name='" + peakName + "';";

        Cursor c = database.rawQuery(query, null);
        c.moveToFirst();

        byte[] byteArray = c.getBlob(c.getColumnIndex("_image"));

        Bitmap image = BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length);

        return image;
    }


}
