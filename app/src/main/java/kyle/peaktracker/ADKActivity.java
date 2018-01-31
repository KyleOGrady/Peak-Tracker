package kyle.peaktracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ADKActivity extends AppCompatActivity {

    ListView peaksListView;
    List<Peak> peaksList = new ArrayList<>();
    List<String> peaksToLayout = new ArrayList<>();

    Button claimPeak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adk);
        final DatabaseAccess access = DatabaseAccess.getInstance(this);

        //claimPeak = (Button) findViewById(R.id.claimPeak);

        access.open();
        peaksListView = (ListView)findViewById(R.id.adk_peak_list);
        peaksList = access.populatePeaks("adk_peaks");
        access.close();

        for(Peak peak: peaksList) {
            //Log.d("PEAKS NAME", peak.get_name());
            peaksToLayout.add(peak.toString());
        }

        PeaksAdapter adapter = new PeaksAdapter(this, R.layout.activity_peak_listview, peaksToLayout, null);
        adapter.setButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < peaksListView.getChildCount(); i++) {

                    if (view == peaksListView.getChildAt(i).findViewById(R.id.peak_claimPeak)) {
                        Log.d("TEST BUTTON", "CLAIM " + "" + " BUTTON PRESSED.");
                    }
                }

             }
            }
        );

        peaksListView.setAdapter(adapter);

        /*peaksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ADKActivity.this, "List item was clicked at " + position, Toast.LENGTH_SHORT).show();
            }
        });*/

    }
    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adk);
        final DatabaseAccess access = DatabaseAccess.getInstance(this);

        //claimPeak = (Button) findViewById(R.id.claimPeak);

        access.open();
        peaksListView = (ListView)findViewById(R.id.adk_peak_list);
        peaksList = access.populatePeaks("adk_peaks");
        access.close();

        for(Peak peak: peaksList) {
            //Log.d("PEAKS NAME", peak.get_name());
            peaksToLayout.add(peak.toString());
        }

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_peak_listview, R.id.peak_textView, peaksToLayout);
        peaksListView.setAdapter(arrayAdapter);

       *//* claimPeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                access.open();
                access.claimPeak("Skylight", "adk_peaks");
                peaksList.clear();
                peaksList = access.populatePeaks("adk_peaks");

                for(Peak peak: peaksList){
                    Log.d("PEAKS NAME: ", peak.get_name() + " CLIMBED: " + peak.get_climbed());
                    peaksToLayout.add(peak.toString());
                }
                access.close();

            }
        });*//*
    }*/

    protected void onResume(){
        super.onResume();
    }
}
