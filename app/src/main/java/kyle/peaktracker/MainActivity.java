package kyle.peaktracker;

import android.content.Intent;;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.text.DecimalFormat;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button NE115;
    Button ADK46;
    Button NH48;

    DatabaseAccess access;
    //Setting Display
    double adk_perc_completed = 0.0; //initial value does not matter
    double nh_perc_completed = 0.0;

    DecimalFormat df = new DecimalFormat("###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        access = DatabaseAccess.getInstance(this);

        //Setting Id's
        NE115 = (Button) findViewById(R.id.ne115_display_button);
        ADK46 = (Button) findViewById(R.id.adk_display_button);
        NH48 = (Button) findViewById(R.id.nh_display_button);

        access.open();
        adk_perc_completed = access.calculate_completion("adk_peaks");
        nh_perc_completed = access.calculate_completion("nh_peaks");
        access.close();

        //Setting text for the buttons
        ADK46.setText("ADK46: " + df.format(adk_perc_completed) + "%");
        NH48.setText("NH48: " + df.format(nh_perc_completed) + "%");

        //Button Actions
        ADK46.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ADKActivity.class));
            }
        });

        NH48.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NHActivity.class));
            }
        });
    }

    protected void onResume(){
        super.onResume();

        access.open();
        adk_perc_completed = access.calculate_completion("adk_peaks");
        nh_perc_completed = access.calculate_completion("nh_peaks");
        access.close();

        ADK46.setText("ADK46: " + df.format(adk_perc_completed) + "%");
        NH48.setText("NH48: " + df.format(nh_perc_completed) + "%");
    }

}
