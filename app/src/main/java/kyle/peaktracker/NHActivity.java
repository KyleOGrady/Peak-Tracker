package kyle.peaktracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class NHActivity extends AppCompatActivity {

    ListView peaksListView;
    List<Peak> peaksList = new ArrayList<>();
    List<String> peaksToLayout = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nh);

        DatabaseAccess access = DatabaseAccess.getInstance(this);

        access.open();
        peaksListView = (ListView)findViewById(R.id.nh_peak_list);
        peaksList = access.populatePeaks("nh_peaks");
        access.close();

        for(Peak peak: peaksList) {
            Log.d("PEAKS NAME", peak.get_name());
            peaksToLayout.add(peak.toString());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_peak_listview, R.id.peak_textView, peaksToLayout);
        peaksListView.setAdapter(arrayAdapter);
    }
}