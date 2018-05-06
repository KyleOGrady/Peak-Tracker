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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.graphics.Matrix;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import static android.view.View.VISIBLE;

public class PeaksAdapter extends ArrayAdapter<Peak>{

    private List <Peak> items = new ArrayList<Peak>();
    private int resource;
    private Context context;
    String printPeakInfo = "";
    String printDate = "";
    PeaksAdapter adapter;
    DatabaseAccess access;

    private static final int CLIMBED = 0;
    private static final int NOT_CLIMBED = 1;

    public PeaksAdapter(Context context, int resource, List<Peak> items){
        super(context, resource, items);
        this.resource = resource;
        this.context = context;
        this.items = items;
        this.adapter = this;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position).get_climbed().equals("N")) {
            return NOT_CLIMBED;
        } else {
            return CLIMBED;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        PeakHolder holder;
        int listViewItemType = getItemViewType(position);
        if(convertView == null) {

            if(listViewItemType == CLIMBED){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.type_climbed, null);
            } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.type_unclimbed, null);
            }

            holder = new PeakHolder();

            final Typeface noir = Typeface.createFromAsset(context.getAssets(), "fonts/NoirStd-Regular.ttf");
            holder.info = convertView.findViewById(R.id.peak_textView);
            Log.d("HOLDER INFO TEXT", holder.info.getText().toString());
            holder.info.setTypeface(noir);

            if(listViewItemType == CLIMBED){
                holder.climbedImage = convertView.findViewById(R.id.climbed_image);
                Bitmap set = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.yes_5);
                int width = set.getWidth();
                int height = set.getHeight();
                Bitmap resized = getResizedBitmap(set, height/10, width/10);
                holder.climbedImage.setImageBitmap(resized);
            } else {
                holder.claimPeak = convertView.findViewById(R.id.peak_claimPeak);
            }

            convertView.setTag(holder);

        } /*end if convertView==null*/ else {

            holder = (PeakHolder) convertView.getTag();

        }

        printPeakInfo = getItem(position).get_name() + " " + getItem(position).get_height() + "'";

        printDate = "Climbed on " + getItem(position).get_date();
        Log.d("CLIMBED INFO", printDate);
        holder.info.setText(printPeakInfo);

        holder.info.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                Log.d("TEST HOLD", "HELD");

                final BottomSheetDialog bottomDialog = new BottomSheetDialog(context);
                bottomDialog.setContentView(R.layout.view_claim_info_layout);
                //Setting items on screen
                TextView name = bottomDialog.findViewById(R.id.name);
                final ImageButton edit_button = bottomDialog.findViewById(R.id.edit_button);
                final TextView height_climbed = bottomDialog.findViewById(R.id.height_climbed);
                final TextView comments = bottomDialog.findViewById(R.id.comments);

                Typeface cabin_reg = Typeface.createFromAsset(context.getAssets(),"fonts/Cabin-Regular.ttf");
                Typeface cabin_italic = Typeface.createFromAsset(context.getAssets(),"fonts/Cabin-Italic.ttf");
                Typeface cabin_semiBold = Typeface.createFromAsset(context.getAssets(),"fonts/Cabin-SemiBold.ttf");

                name.setTypeface(cabin_semiBold);
                height_climbed.setTypeface(cabin_reg);
                comments.setTypeface(cabin_italic);
                final ImageView image = bottomDialog.findViewById(R.id.uploaded_image);

                //Testing to see if there are comments to display
                if(getItem(position).get_comments() != null){
                    comments.setVisibility(VISIBLE);
                }

                //Testing to see if there is an image to display
                if(getItem(position).get_image() != null){
                    image.setVisibility(VISIBLE);
                }

                //Setting text for textfields
                name.setText(getItem(position).get_name());
                String height_climbed_text;

                if(getItem(position).get_date() == null){ //Hasn't been climbed yet
                    height_climbed_text = getItem(position).get_height() + "' | Not Yet Climbed";
                } else { //Has been climbed
                    height_climbed_text = getItem(position).get_height() + "' | Climbed on " + getItem(position).get_date();
                    edit_button.setVisibility(VISIBLE);
                }

                height_climbed.setText(height_climbed_text);
                comments.setText(getItem(position).get_comments());

                image.setImageBitmap(getItem(position).get_image());

                //Fullscreen image on click
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final Dialog fullScreenDialog = new Dialog(context, R.style.FullScreenDialogTheme);
                        fullScreenDialog.setContentView(R.layout.full_screen_image_layout);

                        ImageView fullScreenImage = fullScreenDialog.findViewById(R.id.fullScreenImage);
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

                //Bring up edit info dialog
                edit_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(context, EditClaimActivity.class);
                        String peakName = getItem(position).get_name();
                        String tableName = getItem(position).get_list();
                        String newComments = getItem(position).get_comments();
                        String newDate = getItem(position).get_date();

                        Bundle bundle = new Bundle();
                        bundle.putString("PEAK NAME", peakName);
                        bundle.putString("TABLE NAME", tableName);
                        bundle.putString("COMMENTS", newComments);
                        bundle.putString("DATE", newDate);
                        i.putExtras(bundle);

                        context.startActivity(i);
                        bottomDialog.dismiss();

                    }
                });

                bottomDialog.show();
                return true;
            }
        });

        if(listViewItemType == NOT_CLIMBED){
            holder.claimPeak.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent i = new Intent(context, ClaimPeakActivity.class);
                    String peakName = getItem(position).get_name();
                    String tableName = getItem(position).get_list();
                    Bundle bundle = new Bundle();
                    bundle.putString("PEAK NAME", peakName);
                    bundle.putString("TABLE NAME", tableName);
                    i.putExtras(bundle);

                    context.startActivity(i);
                }
            });

        } else {

            holder.climbedImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    String peakName = getItem(position).get_name();
                    String dateClimbed = getItem(position).get_date();

                    Toast.makeText(context, peakName + " climbed on " + dateClimbed, Toast.LENGTH_LONG).show();
                }
            });

        }

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
