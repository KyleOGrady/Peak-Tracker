package kyle.peaktracker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ClaimPeakActivity extends AppCompatActivity {

    DatabaseAccess access = DatabaseAccess.getInstance(this);;
    ImageView testView;
    ShareButton fbShareButton;
    Bitmap selectedImage;
    SharePhoto sharePhoto;
    CallbackManager callbackManager;
    ShareDialog shareDialog = new ShareDialog(this);
    byte[] imageSaved;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_claim_peak);

        callbackManager = CallbackManager.Factory.create();

        Bundle bundle = getIntent().getExtras();
        final String peakName = bundle.getString("PEAK NAME");
        final String tableName = bundle.getString("TABLE NAME");

        //Items on dialog
        TextView claimPeak = (TextView)findViewById(R.id.claimPeakHeader);
        ImageButton submitClaim = (ImageButton)findViewById(R.id.submitClaim);
        ImageButton uploadImage = (ImageButton)findViewById(R.id.upload_image);
        final EditText selectDate = (EditText)findViewById(R.id.selectDate);
        final EditText enterComments = (EditText)findViewById(R.id.enterComments);
        testView = (ImageView)findViewById(R.id.test_view);

        int c = enterComments.getCurrentHintTextColor();
        Log.d("HINT COLOR", String.format("%X", c));

        //Date Picking logic
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date;

        fbShareButton = (ShareButton) findViewById(R.id.share_btn);
        //Setting typefaces for each element in the layout
        Typeface cabin_semiBold = Typeface.createFromAsset(getAssets(),"fonts/Cabin-SemiBold.ttf");
        Typeface cabin_regular = Typeface.createFromAsset(getAssets(),"fonts/Cabin-Regular.ttf");
        claimPeak.setTypeface(cabin_semiBold);
        selectDate.setTypeface(cabin_regular);
        enterComments.setTypeface(cabin_regular);

        fbShareButton.setEnabled(false);

        fbShareButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

               sharePhoto = new SharePhoto.Builder()
                        .setBitmap(selectedImage).build();

                ShareContent shareContent = new ShareMediaContent.Builder()
                        .addMedium(sharePhoto).build();
                fbShareButton.setShareContent(shareContent);

            }
        });

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                selectDate.setText(sdf.format(myCalendar.getTime()));
            }

        };

        selectDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(ClaimPeakActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        //ON UPLOAD IMAGE CLICK
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickPhoto.setType("image/*");
                pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(pickPhoto, "Select Picture"), 2);
            }
        });

        //ON SUBMIT OF CLAIM
        submitClaim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selectDate.getText().toString().equals("") || enterComments.getText().toString().equals("")){
                    Toast.makeText(getBaseContext(), "Enter some comments and select and date.", Toast.LENGTH_LONG).show();
                } else{
                    String date = selectDate.getText().toString();
                    String comments = enterComments.getText().toString();

                    access.open();
                    access.claimPeak(peakName, date, comments, imageSaved, tableName);
                    access.close();

                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if(reqCode == 2){
            if(resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    selectedImage = BitmapFactory.decodeStream(imageStream);
                    int width = selectedImage.getWidth();
                    int height = selectedImage.getHeight();
                    Bitmap selectedImageScaled = getResizedBitmap(selectedImage, height / 5, width / 5);
                    //Bitmap selectedImageScaled = Bitmap.createScaledBitmap(selectedImage, width/10, height/10, true);
                    Log.d("SELECTED SIZE", Integer.toString(selectedImage.getByteCount()));
                    Log.d("SELECTED SCALED SIZE", Integer.toString(selectedImageScaled.getByteCount()));

                    fbShareButton.setEnabled(true);

                    imageSaved = getBitmapAsByteArray(selectedImageScaled);

                    testView.setVisibility(View.VISIBLE);
                    testView.setImageBitmap(selectedImageScaled);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                }

            } else {
                Log.d("RESULT CODE", Integer.toString(resultCode));
                Toast.makeText(getBaseContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d("RESULT CODE", Integer.toString(resultCode));
        }
        callbackManager.onActivityResult(reqCode, resultCode, data);
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth)
    {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }


}
