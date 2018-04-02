package kyle.peaktracker;

import android.content.Intent;;
import android.graphics.Typeface;
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
    TextView header;

    DatabaseAccess access;
    //Setting Display
    int adk_completed = 0; //initial value does not matter
    int nh_completed = 0;
    int ne_completed = 0;

    DecimalFormat df = new DecimalFormat("###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        access = DatabaseAccess.getInstance(this);

        //Setting Id's
        header = findViewById(R.id.main_header);
        NE115 = findViewById(R.id.ne115_display_button);
        ADK46 = findViewById(R.id.adk_display_button);
        NH48 = findViewById(R.id.nh_display_button);

        final Typeface cabin_semiBold = Typeface.createFromAsset(getAssets(),"fonts/Cabin-SemiBold.ttf");
        final Typeface cabin_regular = Typeface.createFromAsset(getAssets(),"fonts/Cabin-Regular.ttf");
        //Typeface noir = Typeface.createFromAsset(getAssets(), "fonts/NoirStd-Regular.ttf");

        header.setTypeface(cabin_semiBold);
        NE115.setTypeface(cabin_regular);
        ADK46.setTypeface(cabin_regular);
        NH48.setTypeface(cabin_regular);

        access.open();
        adk_completed = access.calculate_completion("adk_peaks");
        nh_completed = access.calculate_completion("nh_peaks");
        ne_completed = access.calculate_completion("ne_peaks");
        access.close();

        //Setting text for the buttons
        NE115.setText("NE115: " + ne_completed + "/115");
        ADK46.setText("ADK46: " + adk_completed + "/46");
        NH48.setText("NH48: " + nh_completed + "/48");

        //Button Actions
        NE115.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NEActivity.class));
            }
        });

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
        ne_completed = access.calculate_completion("ne_peaks");
        adk_completed = access.calculate_completion("adk_peaks");
        nh_completed = access.calculate_completion("nh_peaks");
        access.close();

        NE115.setText("NE115: " + ne_completed + "/115");
        ADK46.setText("ADK46: " + adk_completed + "/46");
        NH48.setText("NH48: " + nh_completed + "/48");
    }

}
