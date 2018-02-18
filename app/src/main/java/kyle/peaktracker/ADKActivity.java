package kyle.peaktracker;

import android.content.Intent;
import android.graphics.Typeface;
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
    public List<Peak> peaksList = new ArrayList<>();
    List<String> peaksToLayout = new ArrayList<>();
    PeaksAdapter adapter;
    final DatabaseAccess access = DatabaseAccess.getInstance(this);
    Button claimPeak;
    TextView header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adk);
       // final DatabaseAccess access = DatabaseAccess.getInstance(this);

        header = (TextView)findViewById(R.id.adkHeader);
        Typeface noir = Typeface.createFromAsset(getAssets(), "fonts/NoirStd-Regular.ttf");
        header.setTypeface(noir);

        access.open();
        peaksListView = (ListView)findViewById(R.id.adk_peak_list);
        peaksList = access.populatePeaks("adk_peaks");
        access.close();

        adapter = new PeaksAdapter(this, R.layout.activity_peak_listview, peaksList);

        peaksListView.setAdapter(adapter);

    }

    protected void onResume() {
        super.onResume();

        adapter.clear();
        peaksList.clear();

        access.open();
        peaksList.addAll(access.populatePeaks("adk_peaks"));
        access.close();

        adapter.notifyDataSetChanged();

    }
}
