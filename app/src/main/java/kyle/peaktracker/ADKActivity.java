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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
            peaksToLayout.add(peak.get_name() + " | Climbed: " + peak.get_climbed());
        }

        PeaksAdapter adapter = new PeaksAdapter(this, R.layout.activity_peak_listview, peaksToLayout, null);

        peaksListView.setAdapter(adapter);

    }

    //Action for the Claim peak button
    public void onClickClaimPeak(View view){

        LinearLayout vwParentRow = (LinearLayout)view.getParent();

        TextView child = (TextView)vwParentRow.getChildAt(0);
        Button btnChild = (Button)vwParentRow.getChildAt(1);

        Log.d("PEAK NAME CLICKED", child.getText().toString());

    }

    protected void onResume(){
        super.onResume();
    }
}
