package kyle.peaktracker;

import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

        DatabaseAccess access = DatabaseAccess.getInstance(this);
        access.open();
        Log.d("TEST", "opened database");
        access.close();
        Log.d("TEST", "closed database");
    }

    public void printDatabase(){
        String dbString = dbHandler.databaseToString();
        testText.setText(dbString);
    }
}
