package kyle.peaktracker;

import android.content.Context;
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

    public View.OnClickListener listener;

    public PeaksAdapter(Context context, int resource, List<String> items, View.OnClickListener listener){
        super(context, resource, items);
        this.resource = resource;
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    public void setButtonListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        PeakHolder mainPeakHolder = null;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(resource, parent, false);
            PeakHolder holder = new PeakHolder();
            holder.info = convertView.findViewById(R.id.peak_textView);
            holder.claimPeak = convertView.findViewById(R.id.peak_claimPeak);
            convertView.setTag(holder);
        }
        mainPeakHolder = (PeakHolder) convertView.getTag();
        mainPeakHolder.claimPeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "Button was clicked for list item " + position, Toast.LENGTH_SHORT).show();
            }
        });
        mainPeakHolder.info.setText(getItem(position));

        if (this.listener != null) {
            mainPeakHolder.claimPeak.setOnClickListener(this.listener);
        }

        return convertView;
    }

    public static class PeakHolder {
        Peak peak;
        TextView info;
        Button claimPeak;
    }
}
