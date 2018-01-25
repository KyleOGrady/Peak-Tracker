package kyle.peaktracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ADKActivity extends AppCompatActivity {

    ListView peaksListView;
    List<Peak> peaksList = new ArrayList<>();
    List<String> testNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adk);
        DatabaseAccess access = DatabaseAccess.getInstance(this);

        access.open();
        peaksListView = (ListView)findViewById(R.id.adk_peak_list);
        peaksList = access.populatePeaks("adk_peaks");
        access.close();

        for(Peak peak: peaksList) {
            Log.d("PEAKS NAME", peak.get_name());
            testNames.add(peak.get_name());
        }


//        for(int i = 0; i < peaksList.size(); i++){
//            Log.d("PEAKS NAME" , peaksList.get(i).get_name());
//            testNames.add(peaksList.get(i).get_name());
//        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_peak_listview, R.id.peak_textView, testNames);
        peaksListView.setAdapter(arrayAdapter);

    }
}
