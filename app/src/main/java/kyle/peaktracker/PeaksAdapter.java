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
    DatabaseAccess access = DatabaseAccess.getInstance(context);
    PeaksAdapter adapter;
    Uri selectedimg = null;
    private static final int SELECT_PICTURE = 1;

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
                Bundle bundle = new Bundle();
                bundle.putString("PEAK NAME", peakName);
                i.putExtras(bundle);

                context.startActivity(i);

//                final Dialog dialog = new Dialog(context);
//
//                dialog.setContentView(R.layout.activity_claim_peak);
//                dialog.setTitle("TEST");
//
//                dialog.getWindow().setSoftInputMode(
//                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//                //Items on dialog
//                Button submitClaim = dialog.findViewById(R.id.submitClaim);
//                Button uploadImage = dialog.findViewById(R.id.upload_image);
//                final EditText selectDate = (EditText)dialog.findViewById(R.id.selectDate);
//                final EditText enterComments = (EditText)dialog.findViewById(R.id.enterComments);
//                final ImageView testView = dialog.findViewById(R.id.test_view);
//
//                //Date Picking logic
//                final Calendar myCalendar = Calendar.getInstance();
//                final DatePickerDialog.OnDateSetListener date;
//
//                date = new DatePickerDialog.OnDateSetListener() {
//
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int monthOfYear,
//                                          int dayOfMonth) {
//
//                        myCalendar.set(Calendar.YEAR, year);
//                        myCalendar.set(Calendar.MONTH, monthOfYear);
//                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                        String myFormat = "MM/dd/yy"; //In which you need put here
//                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//
//                        selectDate.setText(sdf.format(myCalendar.getTime()));
//                    }
//
//                };
//
//                selectDate.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        new DatePickerDialog(context, date, myCalendar
//                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//                    }
//                });
//
//
//                //ON UPLOAD IMAGE CLICK
//                uploadImage.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
//                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                        pickPhoto.setType("image/*");
//                        pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
//                        ((Activity)context).startActivityForResult(Intent.createChooser(pickPhoto, "Select Picture"), SELECT_PICTURE);
//
//                        Uri contentURI = pickPhoto.getData();
//                        try {
//                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), contentURI);
//
//                            Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show();
//                            testView.setImageBitmap(bitmap);
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                });
//
//                //ON SUBMIT OF CLAIM
//                submitClaim.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        String name = getItem(position).get_name();
//                        String date = selectDate.getText().toString();
//                        String comments = enterComments.getText().toString();
//
//                        access.open();
//                        access.claimPeak(name, date, comments, "adk_peaks");
//                        access.close();
//
//                        dialog.dismiss();
//                    }
//                });
//
//                dialog.show();
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
