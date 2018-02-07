package kyle.peaktracker;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class PeaksAdapter extends ArrayAdapter<String>{

    private List <String> items;
    private int resource;
    private Context context;

    public PeaksAdapter(Context context, int resource, List<String> items){
        super(context, resource, items);
        this.resource = resource;
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        PeakHolder mainPeakHolder = null;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(resource, parent, false);
            PeakHolder holder = new PeakHolder();
            Typeface noir = Typeface.createFromAsset(context.getAssets(),"fonts/NoirStd-Regular.ttf");
            holder.info = convertView.findViewById(R.id.peak_textView);
            holder.info.setTypeface(noir);
            holder.claimPeak = convertView.findViewById(R.id.peak_claimPeak);
            convertView.setTag(holder);
        }
        mainPeakHolder = (PeakHolder) convertView.getTag();

        mainPeakHolder.info.setText(getItem(position).toString());

        return convertView;
    }

    public static class PeakHolder {
        TextView info;
        Button claimPeak;
    }
}
