package kyle.peaktracker;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetBehavior;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.lang.System.in;

public class PeaksAdapter extends ArrayAdapter<Peak>{

    private List <Peak> items;
    private int resource;
    private Context context;
    String printPeakInfo = "";
    String printDate = "";
    PeaksAdapter adapter;

    public PeaksAdapter(Context context, int resource, List<Peak> items){
        super(context, resource, items);
        this.resource = resource;
        this.context = context;
        this.items = items;
        this.adapter = this;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        PeakHolder mainPeakHolder = null;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(resource, parent, false);
        PeakHolder holder = new PeakHolder();

        final Typeface noir = Typeface.createFromAsset(context.getAssets(),"fonts/NoirStd-Regular.ttf");
        holder.info = convertView.findViewById(R.id.peak_textView);
        Log.d("HOLDER INFO TEXT", holder.info.getText().toString());
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

        printPeakInfo = getItem(position).get_name() + " " + getItem(position).get_height() + "'";

        printDate = "Climbed on " + "\n" + getItem(position).get_date();
        mainPeakHolder.info.setText(printPeakInfo);
        mainPeakHolder.dateClimbed.setText(printDate);

        //Bring up bottom dialog
        holder.info.setOnLongClickListener(new View.OnLongClickListener() { //list is my listView

            @Override
            public boolean onLongClick(View v) {

                Log.d("TEST HOLD", "HELD");
                BottomSheetDialog bottomDialog = new BottomSheetDialog(context);
                bottomDialog.setContentView(R.layout.bottom_sheet_layout);
                //Setting items on screen
                TextView name = bottomDialog.findViewById(R.id.name);
                TextView height_climbed = bottomDialog.findViewById(R.id.height_climbed);
                TextView comments = bottomDialog.findViewById(R.id.comments);

                Typeface cabin_reg = Typeface.createFromAsset(context.getAssets(),"fonts/Cabin-Regular.ttf");
                Typeface cabin_italic = Typeface.createFromAsset(context.getAssets(),"fonts/Cabin-Italic.ttf");
                Typeface cabin_semiBold = Typeface.createFromAsset(context.getAssets(),"fonts/Cabin-SemiBold.ttf");

                name.setTypeface(cabin_semiBold);
                height_climbed.setTypeface(cabin_reg);
                comments.setTypeface(cabin_italic);
                ImageView image = bottomDialog.findViewById(R.id.uploaded_image);

                //Setting text for textfields
                name.setText(getItem(position).get_name());
                String height_climbed_text = getItem(position).get_height() + "' | Climbed on " + getItem(position).get_date();
                height_climbed.setText(height_climbed_text);
                comments.setText(getItem(position).get_comments());

                image.setImageBitmap(getItem(position).get_image());

                bottomDialog.show();
                return true;
            }
        });

        holder.claimPeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, ClaimPeakActivity.class);
                String peakName = getItem(position).get_name();
                String tableName = getItem(position).get_list();
                Bitmap peakImage = getItem(position).get_image();
                Bundle bundle = new Bundle();
                bundle.putString("PEAK NAME", peakName);
                bundle.putString("TABLE NAME", tableName);
                i.putExtras(bundle);

                context.startActivity(i);

            }
        });

        return convertView;
    }

    public static class PeakHolder {
        TextView info;
        TextView dateClimbed;
        Button claimPeak;
    }

}
