package kyle.peaktracker;

import android.content.Intent;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.text.DecimalFormat;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button NE115;
    Button ADK46;
    Button NH48;

    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseAccess access = DatabaseAccess.getInstance(this);
        //Setting Id's
        NE115 = (Button) findViewById(R.id.ne115_display_button);
        ADK46 = (Button) findViewById(R.id.adk_display_button);
        NH48 = (Button) findViewById(R.id.nh_display_button);

        //NE115 Setting Display
        double perc_completed = 0.0; //initial value does not matter
        access.open();
        perc_completed = access.calculate_completion("adk_peaks");

        ADK46.setText("ADK46: " + df.format(perc_completed) + "%");
        access.close();

        //Button Actions
        ADK46.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ADKActivity.class));
            }
        });
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
