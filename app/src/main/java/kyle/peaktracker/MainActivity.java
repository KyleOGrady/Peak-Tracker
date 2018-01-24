package kyle.peaktracker;

import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button NE115;
    Button ADK46;
    Button NH48;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseAccess access = DatabaseAccess.getInstance(this);
        //Setting Id's
        NE115 = (Button) findViewById(R.id.ne115_display);
        ADK46 = (Button) findViewById(R.id.adk_display);
        NH48 = (Button) findViewById(R.id.nh_display);

        //NE115 Setting Display
        double perc_completed = 0.0; //initial value does not matter
        access.open();
        access.calculate_completion("adk_peaks");

        NE115.setText("NE115: " + perc_completed);
        access.close();
    }


    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button NE115 = (Button) findViewById(R.id.ne115_display);
        access.open();
        Log.d("TEST", "opened database");

        String dbString = access.databaseToString();
        Log.d("DATA OUTPUT: ", dbString);
        NE115.setText(dbString);

        access.close();

    }*/

}
