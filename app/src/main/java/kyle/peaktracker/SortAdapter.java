package kyle.peaktracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SortAdapter extends ArrayAdapter<String> {

    private int resource;
    private Context context;
    private List <String> items;

    public SortAdapter(Context context, int resource, List<String> items){
        super(context, resource, items);
        this.resource = resource;
        this.context = context;
        this.items = items;
    }

    @Override
    public View getDropDownView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(resource, parent, false);
        SortHolder holder = new SortHolder();
        holder.item = convertView.findViewById(R.id.sortItem);
        Typeface noir = Typeface.createFromAsset(context.getAssets(), "fonts/NoirStd-Regular.ttf");
        holder.item.setTypeface(noir);

        String text = getItem(position);
        holder.item.setText(text);

        convertView.setTag(holder);

        return convertView;
    }

    public static class SortHolder {
        TextView item;
    }

}
