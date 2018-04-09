package kyle.peaktracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //Northeast 115
    ImageView NE115_mtn;
    TextView NE115_title;
    TextView NE115_number;

    //ADK 46
    ImageView ADK46_mtn;
    TextView ADK46_title;
    TextView ADK46_number;

    //NH 48
    ImageView NH48_mtn;
    TextView NH48_title;
    TextView NH48_number;

    //Main header
    TextView header;

    Bitmap originalPhoto;
    Bitmap overlay;

    DatabaseAccess access;
    //Setting Display
    double adk_perc_completed;
    double nh_perc_completed;
    double ne_perc_completed;

    int ne_num_completed;
    int adk_num_completed;
    int nh_num_completed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        access = DatabaseAccess.getInstance(this);

        originalPhoto  = BitmapFactory.decodeResource(getResources(), R.drawable.progress_graphic_1);
        overlay  = BitmapFactory.decodeResource(getResources(), R.drawable.progress_graphic_1_overlay);

        final Typeface cabin_semiBold = Typeface.createFromAsset(getAssets(),"fonts/Cabin-SemiBold.ttf");
        final Typeface cabin_regular = Typeface.createFromAsset(getAssets(),"fonts/Cabin-Regular.ttf");
        //Typeface noir = Typeface.createFromAsset(getAssets(), "fonts/NoirStd-Regular.ttf");

        //Setting Id's and typeface for Northeast 115
        NE115_mtn = findViewById(R.id.ne_mtn);
        NE115_title = findViewById(R.id.ne_title);
        NE115_title.setTypeface(cabin_semiBold);
        NE115_number = findViewById(R.id.ne_number);
        NE115_number.setTypeface(cabin_regular);

        //Setting Id's and typeface for ADK 46
        ADK46_mtn = findViewById(R.id.adk_mtn);
        ADK46_title = findViewById(R.id.adk_title);
        ADK46_title.setTypeface(cabin_semiBold);
        ADK46_number = findViewById(R.id.adk_number);
        ADK46_number.setTypeface(cabin_regular);

        //Setting Id's and typeface for NH 48
        NH48_mtn = findViewById(R.id.nh_mtn);
        NH48_title = findViewById(R.id.nh_title);
        NH48_title.setTypeface(cabin_semiBold);
        NH48_number = findViewById(R.id.nh_number);
        NH48_number.setTypeface(cabin_regular);

        //Setting id for header
        header = findViewById(R.id.main_header);
        header.setTypeface(cabin_semiBold);

        access.open();
        ne_num_completed = access.calculate_completion("ne_peaks");
        adk_num_completed = access.calculate_completion("adk_peaks");
        nh_num_completed = access.calculate_completion("nh_peaks");
        access.close();

        ne_perc_completed = calculate_perc_completed(ne_num_completed, "ne_peaks");
        adk_perc_completed = calculate_perc_completed(adk_num_completed, "adk_peaks");
        nh_perc_completed = calculate_perc_completed(nh_num_completed, "nh_peaks");

        //Set the shading on the images according to how much has been completed
        setOverlay(NE115_mtn, originalPhoto, ne_perc_completed);
        setOverlay(ADK46_mtn, originalPhoto, adk_perc_completed);
        setOverlay(NH48_mtn, originalPhoto, nh_perc_completed);

        //Setting text for number displays
        NE115_number.setText(ne_num_completed + "/115");
        ADK46_number.setText(adk_num_completed + "/46");
        NH48_number.setText(nh_num_completed + "/48");

        //Button Actions
//        NE115.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, NEActivity.class));
//            }
//        });
//
//        ADK46.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, ADKActivity.class));
//            }
//        });
//
//        NH48.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, NHActivity.class));
//            }
//        });

    }

    protected void onResume(){
        super.onResume();

        access.open();
        ne_num_completed = access.calculate_completion("ne_peaks");
        adk_num_completed = access.calculate_completion("adk_peaks");
        nh_num_completed = access.calculate_completion("nh_peaks");
        access.close();

        //NE115.setText("NE115: " + ne_completed + "/115");
        //ADK46.setText("ADK46: " + adk_completed + "/46");
        //NH48.setText("NH48: " + nh_completed + "/48");
    }

    //Take an imageview, image and percentage, and fill the image with color for the specified percentage,
    //and then assign the image to the specified imageview
    public void setOverlay(ImageView image, Bitmap originalBitmap, double percentageCompleted) {

        int height = originalBitmap.getHeight();
        int percentHeight;
        if(percentageCompleted == 0){
            percentHeight = (int) Math.floor(height);
        } else {
            percentHeight = (int) Math.floor(height *  (percentageCompleted));
        }

        Bitmap cropped = Bitmap.createBitmap(overlay, 0, 0, overlay.getWidth() , percentHeight );
        originalBitmap = overlay(originalBitmap, cropped);
        //set imageview to new bitmap
        image.setImageBitmap(originalBitmap );
    }

    public Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmp3 = bmp1.copy(Bitmap.Config.ARGB_8888,true);//mutable copy
        Canvas canvas = new Canvas(bmp3 );
        canvas.drawBitmap(bmp2, new Matrix(), null);
        return bmp3 ;
    }

    //Take the number of peaks hiked, and calculate a percentage based off of each list
    public double calculate_perc_completed(int num_hiked, String tableName){

        double total;
        double perc;
        if(tableName.equals("ne_peaks")){
            total = 115.0;
        }else if(tableName.equals("adk_peaks")){
            total = 46.0;
        }else if(tableName.equals("nh_peaks")){
            total = 48.0;
        }else{
            total = 0.0;
        }

        perc = (num_hiked/total);

        return perc;
    }

}
