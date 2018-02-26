package kyle.peaktracker;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ADKActivity extends AppCompatActivity {

    ListView peaksListView;
    public List<Peak> peaksList = new ArrayList<>();
    public List<String> sortBy = new ArrayList<>();
   //List<String> peaksToLayout = new ArrayList<>();
    PeaksAdapter adapter;
    final DatabaseAccess access = DatabaseAccess.getInstance(this);
    Button claimPeak;
    TextView header;
    Spinner sortBySpinner;
    TextView sortByTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adk);
       // final DatabaseAccess access = DatabaseAccess.getInstance(this);

        header = (TextView)findViewById(R.id.adkHeader);
        Typeface noir = Typeface.createFromAsset(getAssets(), "fonts/NoirStd-Regular.ttf");
        header.setTypeface(noir);

        //Setting information for sort by spinner
        sortByTextview = (TextView)findViewById(R.id.sortByTextview);
        sortByTextview.setTypeface(noir);
        sortBySpinner = (Spinner) findViewById(R.id.sortBySpinner);
        sortBy.add("Name");
        sortBy.add("Height");
        sortBy.add("Climbed/Not Climbed");
        sortBy.add("Date Climbed");
        sortByTextview.setText("Sort By Name");

        SortAdapter dataAdapter = new SortAdapter(this, R.layout.custom_spinner_item, sortBy);

        sortBySpinner.setAdapter(dataAdapter);

        access.open();
        peaksListView = (ListView)findViewById(R.id.adk_peak_list);
       //peaksList = access.populatePeaks("adk_peaks", "_name");
        access.close();

        adapter = new PeaksAdapter(this, R.layout.activity_peak_listview, peaksList);

        peaksListView.setAdapter(adapter);

        sortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String sortBy = null;
                Log.d("SELECTED ITEM", sortBySpinner.getSelectedItem().toString());

                if(sortBySpinner.getSelectedItem().toString().equals("Height")){
                    sortBy = "_height";

                    sortByTextview.setText("Sort by Height");
                    Log.d("WIDTH", Integer.toString(sortByTextview.getMeasuredWidth()));
                }else if(sortBySpinner.getSelectedItem().toString().equals("Name")){
                    sortBy = "_name";

                    sortByTextview.setText("Sort by Name");
                    Log.d("WIDTH", Integer.toString(sortByTextview.getMeasuredWidth()));
                } else if(sortBySpinner.getSelectedItem().toString().equals("Climbed/Not Climbed")){
                    sortBy = "_climbed";

                    sortByTextview.setText("Sort by Climbed");
//                    DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
//                    float dp = sortByTextview.getMeasuredWidth() / (metrics.densityDpi / 160f);
//                    dp += 105; //Add dp for wider textview
//
//                    Log.d("WIDTH STANDARD", Integer.toString(sortByTextview.getMeasuredWidth()));
//                    Log.d("WIDTH IN DP", Float.toString(dp));
//                    float px = dp * (metrics.densityDpi / 160f);
//                    Log.d("WIDTH IN PX", Float.toString(px));

//                    ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) sortByTextview.getLayoutParams();
//                    params.height = Math.round(px);
//                    sortByTextview.setLayoutParams(params);

                    Log.d("WIDTH AFTER CHANGE", Integer.toString(sortByTextview.getMeasuredWidth()));
                } else if(sortBySpinner.getSelectedItem().toString().equals("Date Climbed")){
                    sortBy = "_date";

                    sortByTextview.setText("Sort by Date");
                    Log.d("WIDTH", Integer.toString(sortByTextview.getMeasuredWidth()));
                }

                sort(sortBy);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }

        });


    }

    protected void onResume() {
        super.onResume();

        adapter.clear();
        peaksList.clear();

        access.open();
        peaksList.addAll(access.populatePeaks("adk_peaks", "_name"));
        access.close();

        adapter.notifyDataSetChanged();
    }

    protected void sort(String sortBy){
        super.onResume();
        adapter.clear();
        peaksList.clear();

        access.open();
        peaksList.addAll(access.populatePeaks("adk_peaks", sortBy));
        access.close();

        adapter.notifyDataSetChanged();
    }

}
