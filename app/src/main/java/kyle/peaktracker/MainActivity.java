package kyle.peaktracker;

import android.annotation.TargetApi;
import android.content.Intent;;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.lang.annotation.Target;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Queue;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ImageView NE115;
    ImageView ADK46;
    ImageView NH48;
    TextView header;

    Bitmap originalPhoto;

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
        originalPhoto  = BitmapFactory.decodeResource(getResources(), R.drawable.progress_graphic);

        //Setting Id's
        header = findViewById(R.id.main_header);
        NE115 = findViewById(R.id.ne115_display_button);
        ADK46 = findViewById(R.id.adk_display_button);
        NH48 = findViewById(R.id.nh_display_button);

        final Typeface cabin_semiBold = Typeface.createFromAsset(getAssets(),"fonts/Cabin-SemiBold.ttf");
        final Typeface cabin_regular = Typeface.createFromAsset(getAssets(),"fonts/Cabin-Regular.ttf");
        //Typeface noir = Typeface.createFromAsset(getAssets(), "fonts/NoirStd-Regular.ttf");

        header.setTypeface(cabin_semiBold);
       // NE115.setTypeface(cabin_regular);
       // ADK46.setTypeface(cabin_regular);
       // NH48.setTypeface(cabin_regular);

        access.open();
        ne_num_completed = access.calculate_completion("ne_peaks");
        adk_num_completed = access.calculate_completion("adk_peaks");
        nh_num_completed = access.calculate_completion("nh_peaks");
        access.close();

        ne_perc_completed = calculate_perc_completed(ne_num_completed, "ne_peaks");
        adk_perc_completed = calculate_perc_completed(adk_num_completed, "adk_peaks");
        nh_perc_completed = calculate_perc_completed(nh_num_completed, "nh_peaks");

        //Set the shading on the images according to how much has been completed
        setMonoChrome(NE115, originalPhoto, ne_perc_completed);
        setMonoChrome(ADK46, originalPhoto, adk_perc_completed);
        setMonoChrome(NH48, originalPhoto, nh_perc_completed);


        //Setting text for the buttons
     //   NE115.setText("NE115: " + ne_completed + "/115");
//        ADK46.setText("ADK46: " + adk_completed + "/46");
//        NH48.setText("NH48: " + nh_completed + "/48");

        //Button Actions
        NE115.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NEActivity.class));
            }
        });

        ADK46.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ADKActivity.class));
            }
        });

        NH48.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NHActivity.class));
            }
        });

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

    //Take an imageview and image, and a percentage, and fill the image with color for the specified percentage,
    //and then assign the image to the specified imageview
    public void setMonoChrome(ImageView image, Bitmap originalBitmap, double percentageCompleted) {

        int height = originalBitmap.getHeight();
        int percentHeight = (int) Math.floor(height *  (1-percentageCompleted));

        //create a bitmap of the top 40% of image height that we will make black and white
        Bitmap croppedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth() , percentHeight );
        //make it monochrome
        Bitmap blackAndWhiteBitmap = monoChrome(croppedBitmap);
        //copy the monochrome bmp (blackAndWhiteBitmap) to the original bmp (originalBitmap)
        originalBitmap = overlay(originalBitmap, blackAndWhiteBitmap);
        //set imageview to new bitmap
        image.setImageBitmap(originalBitmap );
    }

    public Bitmap monoChrome(Bitmap bitmap) {
        Bitmap bmpMonochrome = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmpMonochrome);
        ColorMatrix ma = new ColorMatrix();
        ma.setSaturation(0);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(ma));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bmpMonochrome;
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
