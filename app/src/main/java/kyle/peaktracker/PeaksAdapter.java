package kyle.peaktracker;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.graphics.Matrix;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.lang.Thread.sleep;

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

        //Climbed image
        holder.climbedImage = convertView.findViewById(R.id.climbed_image);
       // holder.climbedImage.setVisibility(GONE);

        //Claim Button
        holder.claimPeak = convertView.findViewById(R.id.peak_claimPeak);
        holder.claimPeak.setVisibility(GONE);

        if(getItem(position).get_climbed().equals("N")){  //Has not been climbed, set background to brown, display claim button
            convertView.setBackgroundResource(R.color.peakBrown);
            holder.claimPeak.setVisibility(VISIBLE);
        } else {  //Has been climbed, display claimed information
            holder.climbedImage.setVisibility(VISIBLE);
            Bitmap set = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.yes6);
            int width = set.getWidth();
            int height = set.getHeight();
            Bitmap resized = getResizedBitmap(set, height/10, width/10);
            holder.climbedImage.setImageBitmap(resized);
        }

        convertView.setTag(holder);

        mainPeakHolder = (PeakHolder) convertView.getTag();

        printPeakInfo = getItem(position).get_name() + " " + getItem(position).get_height() + "'";

        printDate = "Climbed on " + getItem(position).get_date();
        Log.d("CLIMBED INFO", printDate);
        mainPeakHolder.info.setText(printPeakInfo);
//        mainPeakHolder.dateClimbed.setText(printDate);

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
                final ImageView image = bottomDialog.findViewById(R.id.uploaded_image);

                //Testing to see if there is an image to display
                if(getItem(position).get_image() == null){
                    //do nothing
                    Log.d("BOTTOM SHEET IMAGE", "Image is null");
                } else{
                    image.setVisibility(VISIBLE);
                    Log.d("BOTTOM SHEET IMAGE", "Image exists");
                }

                //Setting text for textfields
                name.setText(getItem(position).get_name());
                String height_climbed_text;

                if(getItem(position).get_date() == null){ //Hasn't been climbed yet
                    height_climbed_text = getItem(position).get_height() + "' | Not Yet Climbed";
                } else { //Has been climbed
                    height_climbed_text = getItem(position).get_height() + "' | Climbed on " + getItem(position).get_date();
                }

                height_climbed.setText(height_climbed_text);
                comments.setText(getItem(position).get_comments());

                image.setImageBitmap(getItem(position).get_image());

                //Fullscreen image on click
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final Dialog fullScreenDialog = new Dialog(context, R.style.FullScreenDialogTheme);
                        fullScreenDialog.setContentView(R.layout.activity_full_screen_image);

                        ImageView fullScreenImage = (ImageView)fullScreenDialog.findViewById(R.id.fullScreenImage);
                        fullScreenImage.setImageBitmap(getItem(position).get_image());

                        //Close the image on click
                        fullScreenImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fullScreenDialog.dismiss();
                            }
                        });

                        fullScreenDialog.show();
                    }
                });
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
        ImageView climbedImage;
        //TextView dateClimbed;
        Button claimPeak;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix m = new Matrix();
        // resize the bit map
        m.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, m, false);
        return resizedBitmap;
    }



}
