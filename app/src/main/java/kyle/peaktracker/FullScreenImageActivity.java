package kyle.peaktracker;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FullScreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ImageView image = (ImageView) findViewById(R.id.fullScreenImage);

//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(isImageFitToScreen) {
//                    isImageFitToScreen=false;
//                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                    imageView.setAdjustViewBounds(true);
//                }else{
//                    isImageFitToScreen=true;
//                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//                }
//            }
//        });


//        if(isImageFitToScreen){
//            isImageFitToScreen=false;
//            image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//            image.setAdjustViewBounds(true);
//            Toast.makeText(context, "BACK FROM FULLSCREEN", Toast.LENGTH_LONG).show();
//        } else {
//            isImageFitToScreen = true;
//            Toast.makeText(context, "FULLSCREEN!", Toast.LENGTH_LONG).show();
//            image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//            image.setScaleType(ImageView.ScaleType.FIT_XY);
//        }
//    }
//});



    }
}
