package kyle.peaktracker;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.lang.System.in;

public class PeaksAdapter extends ArrayAdapter<Peak>{

    private List <Peak> items;
    private int resource;
    private Context context;
    String printPeakInfo = "";
    String printDate = "";
    DatabaseAccess access = DatabaseAccess.getInstance(context);;

    public PeaksAdapter(Context context, int resource, List<Peak> items){
        super(context, resource, items);
        this.resource = resource;
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        PeakHolder mainPeakHolder = null;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(resource, parent, false);
        PeakHolder holder = new PeakHolder();

        Typeface noir = Typeface.createFromAsset(context.getAssets(),"fonts/NoirStd-Regular.ttf");
        holder.info = convertView.findViewById(R.id.peak_textView);
        holder.info.setTypeface(noir);

        holder.dateClimbed = convertView.findViewById(R.id.climbed_date);
        holder.dateClimbed.setVisibility(GONE);

        holder.claimPeak = convertView.findViewById(R.id.peak_claimPeak);
        holder.claimPeak.setVisibility(GONE);

        if(getItem(position).get_climbed().equals("N")){
            convertView.setBackgroundResource(R.color.peakBrown);
            holder.claimPeak.setVisibility(VISIBLE);
        }else{
            holder.dateClimbed.setVisibility(VISIBLE);
        }

        convertView.setTag(holder);

        mainPeakHolder = (PeakHolder) convertView.getTag();

        printPeakInfo = getItem(position).get_id() + ". " + getItem(position).get_name() + " " +
                        getItem(position).get_height() + "' | " + getItem(position).get_climbed();

        printDate = "Climbed on " + "\n" + getItem(position).get_date();
        mainPeakHolder.info.setText(printPeakInfo);
        mainPeakHolder.dateClimbed.setText(printDate);


        holder.claimPeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                access.open();

                String peakName = getItem(position).get_name();
                String list = getItem(position).get_list();
                String tableName = "";

                if(list.equals("ADK")){
                    tableName = "adk_peaks";
                } else if(list.equals("NH")){
                    tableName = "nh_peaks";
                } else {
                    tableName = null;
                }
                // NEED TO ADD OTHER TABLE NAMES LATER

                access.claimPeak(peakName, tableName);
                Log.d("CLAIMING PEAK", peakName);
                access.close();

                Toast.makeText(context, "Peak Claimed", Toast.LENGTH_SHORT).show();

            }
        });

        return convertView;
    }

    public static class PeakHolder {
        TextView info;
        TextView dateClimbed;
        Button claimPeak;
    }

    public void refreshList(List<Peak> peaksList){
        peaksList.clear();
        peaksList.addAll(peaksList);
        notifyDataSetChanged();
    }
}
