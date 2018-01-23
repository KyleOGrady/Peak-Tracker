package kyle.peaktracker;

import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MyDBHandler extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "peaks.db";
    //private static final String DATABASE_PATH = "/app/src/main/assests/Database/";
    //private static final String DATABASE_PATH = "/data/data/kyle.peaktracker/databases/";
    //private static final String DATABASE_PATH = "\\app\\src\\main\\assets\\Database\\";
    public static String DATABASE_PATH = ""; //final?
    private static final String TABLE_ADK = "adk_peaks";
    private SQLiteDatabase myDataBase;
    private final Context myContext;


    //Columns
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "_name";
    private static final String COLUMN_HEIGHT = "_height";
    private static final String COLUMN_CLIMBED = "_climbed";
    private static final String COLUMN_DATE = "_date";
    private static final String COLUMN_LIST = "_list";

    public MyDBHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        //DATABASE_PATH = myContext.getApplicationInfo().dataDir + "/databases/";
        DATABASE_PATH = myContext.getDatabasePath(DATABASE_NAME).getPath();
        Log.v("DATABASE PATH: " + DATABASE_PATH, "");
    }

    public void createDatabase() throws IOException
    {
        boolean dbExist = checkDataBase();
        if(dbExist)
        {
            Log.v("DB Exists", "db exists");
            // By calling this method here onUpgrade will be called on a
            // writeable database, but only if the version number has been
            // bumped
            //onUpgrade(myDataBase, DATABASE_VERSION_old, DATABASE_VERSION);
        }
        boolean dbExist1 = checkDataBase();
        if(!dbExist1)
        {
            this.getReadableDatabase();
            try
            {
                this.close();
                Log.e("copyDatabase", "");
                copyDataBase();
            }
            catch (IOException e)
            {
                throw new Error("Error copying database");
            }
        }
    }

    //Check database already exist or not
    private boolean checkDataBase()
    {
        boolean checkDB = false;
        try
        {
            String myPath = DATABASE_PATH;
            File dbfile = new File(myPath);
            checkDB = dbfile.exists();
        }
        catch(SQLiteException e)
        {
        }
        return checkDB;
    }

    //Copies your database from your local assets-folder to the just created empty database in the system folder

    private void copyDataBase() throws IOException
    {
        String outFileName = DATABASE_PATH;
        OutputStream myOutput = new FileOutputStream(outFileName);
        InputStream myInput = myContext.getAssets().open(DATABASE_NAME);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0)
        {
            myOutput.write(buffer, 0, length);
        }
        myInput.close();
        myOutput.flush();
        myOutput.close();
    }

    public void db_delete()
    {
        File file = new File(DATABASE_PATH);
        if(file.exists())
        {
            file.delete();
            System.out.println("delete database file.");
        }
    }

    //Open database
    public void openDatabase() throws SQLException
    {
        String myPath = DATABASE_PATH;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void closeDataBase()throws SQLException
    {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }


    //Override onCreate and onUpgrade
    public void onCreate(SQLiteDatabase db){
        /*String query = "CREATE TABLE " + TABLE_ADK + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT " +
                COLUMN_NAME + " TEXT " +
                COLUMN_HEIGHT + " INTEGER " +
                COLUMN_CLIMBED + " INTEGER " +
                COLUMN_DATE + " TEXT " +
                COLUMN_LIST + " TEXT " +
                ");";

        db.execSQL(query); */
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADK);
        onCreate(db);
    }

    public String databaseToString(){
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_ADK + " WHERE 1";

        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();

        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("_name"))!= null){
                dbString += c.getString(c.getColumnIndex("_name"));
                dbString += "\n";
            }
        }
        c.close(); //Added
        db.close();
        return dbString;
    }
}
