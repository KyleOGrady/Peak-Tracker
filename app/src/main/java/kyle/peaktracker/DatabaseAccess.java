package kyle.peaktracker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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
        Log.d("DATABASE ACCES", "opened database");
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
            Log.d("DATABASE ACCES", "closed database");
        }
    }

    public String databaseToString(){
        String dbString = "";
        String query = "SELECT * FROM " + TABLE_ADK + " WHERE 1";

        Cursor c = database.rawQuery(query, null);

        c.moveToFirst();

        dbString += c.getString(c.getColumnIndex("_name"));

        /*while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("_name"))!= null){
                dbString += c.getString(c.getColumnIndex("_name"));
                dbString += "\n";
                Log.e("COLUMN: ", dbString);
                c.moveToNext();
            }
        }*/
        c.close(); //Added
        return dbString;
    }

    public double calculate_completion(String tableName){

        String query = "SELECT _climbed FROM " + tableName + " WHERE 1";

        Cursor c = database.rawQuery(query, null);
        String temp_compare = ""; //placeholder for N or Y
        double num_hiked = 0.0;
        double total;
        double perc;

        if(tableName.equals("adk_peaks")){
            total = 46.0;
        }else if(tableName.equals("ne_peaks")){
            total = 115.0;
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

        perc = num_hiked/total;
        Log.d("PERCENTAGE", Double.toString(perc));

        return perc;
    }

    public List<Peak> populatePeaks(String tableName){
        List<Peak> peaksList = new ArrayList<>();
        String query = "SELECT * FROM " + tableName;

        Cursor c = database.rawQuery(query, null);
        c.moveToFirst();
        Peak tempPeak = new Peak();
        Log.d("COLUMN COUNT", Integer.toString(c.getColumnCount()));
        String temp = "";

        while(!c.isAfterLast()){
            for(int i=0; i<c.getColumnCount(); i++){
                temp = c.getString(i);

                switch(i){
                    case 0:
                        Log.d("SET OBJECT", "Setting ID to " + temp);
                        tempPeak.set_id(Integer.parseInt(temp));
                        break;
                    case 1:
                        Log.d("SET OBJECT", "Setting name to " + temp);
                        tempPeak.set_name(temp);
                        break;
                    case 2:
                        Log.d("SET OBJECT", "Setting height to " + temp);
                        tempPeak.set_height(Integer.parseInt(temp));
                        break;
                    case 3:
                        Log.d("SET OBJECT", "Setting climbed to " + temp);
                        tempPeak.set_climbed(temp);
                        break;
                    case 4:
                        Log.d("SET OBJECT", "Setting date to " + temp);
                        tempPeak.set_date(temp);
                        break;
                    case 5:
                        Log.d("SET OBJECT", "Setting list to " + temp);
                        tempPeak.set_list(temp);
                        break;
                }
            }

            //peaksList.add(tempPeak);
            Log.d("ADD PEAK", "Adding " + tempPeak.get_name() + "to database.");
            peaksList.add(tempPeak);
            //temp = "";
            c.moveToNext();

        }

        return peaksList;
    }

}
