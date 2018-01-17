package kyle.peaktracker;

import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView testText;
    MyDBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testText = (TextView) findViewById(R.id.test_text);
        //dbHandler = new MyDBHandler(this, null, null, 1);
        dbHandler = new MyDBHandler(this);
        try {
            dbHandler.createDatabase();
        } catch (IOException ioe){
            throw new Error("Unable to create database");
        }

        try {

            dbHandler.openDatabase();

        }catch(SQLException sqle){

            throw sqle;

        }

        printDatabase();
    }

    public void printDatabase(){
        String dbString = dbHandler.databaseToString();
        testText.setText(dbString);
    }
}
