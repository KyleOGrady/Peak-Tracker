package kyle.peaktracker;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static kyle.peaktracker.ClaimPeakActivity.getBitmapAsByteArray;
import static kyle.peaktracker.ClaimPeakActivity.getPath;

public class EditClaimActivity extends AppCompatActivity {

    DatabaseAccess access = DatabaseAccess.getInstance(this);
    Bitmap selectedImage;
    Bitmap selectedImageScaled;
    Bitmap shareImage;
    byte[] imageSaved;
    ImageView picView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.activity_edit_claim);

        //Get information from PeaksAdapter
        Bundle bundle = getIntent().getExtras();
        final String peakName = bundle.getString("PEAK NAME");
        final String tableName = bundle.getString("TABLE NAME");
        final String oldComments = bundle.getString("COMMENTS");
        final String oldDate = bundle.getString("DATE");

        final TextView header = findViewById(R.id.editInfoHeader);
        final EditText editComments = findViewById(R.id.editComments);
        final EditText editDate = findViewById(R.id.editDate);
        ImageButton newImage = findViewById(R.id.new_image);
        Button saveChanges = findViewById(R.id.saveChanges);
        Button cancelChanges = findViewById(R.id.cancelChanges);
        picView = findViewById(R.id.pic_view);
        final TextView unclaim = findViewById(R.id.unclaim);

        final Typeface cabin_semiBold = Typeface.createFromAsset(getAssets(),"fonts/Cabin-SemiBold.ttf");
        final Typeface cabin_regular = Typeface.createFromAsset(getAssets(),"fonts/Cabin-Regular.ttf");

        header.setTypeface(cabin_semiBold);
        editComments.setTypeface(cabin_regular);
        editDate.setTypeface(cabin_regular);
        unclaim.setTypeface(cabin_semiBold);

        saveChanges.setTypeface(cabin_semiBold);
        cancelChanges.setTypeface(cabin_semiBold);

        //Set greater height for header if the peak name has more than 10   characters
        if( peakName.length() > 10){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) header.getLayoutParams();
            params.height = getResources().getDimensionPixelSize(R.dimen.text_view_big);
            header.setLayoutParams(params);
        }

        header.setText("Edit " + peakName + " Info");

        access.open();
        picView.setImageBitmap(access.query_image(tableName, peakName));
        access.close();

        editComments.setText(oldComments);
        editDate.setText(oldDate);

        //Date Picking logic
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date;

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                editDate.setText(sdf.format(myCalendar.getTime()));
            }

        };

        editDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditClaimActivity.this, R.style.datepicker, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //ON UPLOAD IMAGE CLICK
        newImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickPhoto.setType("image/*");
                pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(pickPhoto, "Select Picture"), 2);
            }
        });

        //Write out changes to the database and close the activity
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String newDate = editDate.getText().toString();
                final String newComments = editComments.getText().toString();

                access.open();

                if(imageSaved==null){
                    access.claimPeak(peakName, newDate, newComments, tableName);
                } else {
                    access.claimPeak(peakName, newDate, newComments, imageSaved, tableName);
                }

                access.close();

                finish(); //close the activity
            }
        });

        cancelChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        unclaim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set the view to the claim confirmation layout
                setContentView(R.layout.confirm_unclaim_layout);

                TextView unclaim_header = findViewById(R.id.unclaim_header);
                Button confirm_unclaim = findViewById(R.id.confirm_unclaim);
                Button cancel_unclaim = findViewById(R.id.cancel_unclaim);

                unclaim_header.setTypeface(cabin_regular);
                confirm_unclaim.setTypeface(cabin_semiBold);
                cancel_unclaim.setTypeface(cabin_semiBold);

                unclaim_header.setText("Are you sure you want to unclaim " + peakName + "?");

                confirm_unclaim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        access.open();
                        access.unclaimPeak(tableName, peakName);
                        access.close();

                        finish();
                    }
                });

                cancel_unclaim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        finish();
                    }
                });

            }
        });

    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (reqCode == 2) {
            if (resultCode == RESULT_OK) {
                //Get image from gallery
                try {
                    //Check for permission to read external storage
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (shouldShowRequestPermissionRationale(
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            // Explain to the user why we need to read the contacts
                        }

                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000); //Not sure what this request code means but it works

                        return;
                    }

                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    selectedImage = BitmapFactory.decodeStream(imageStream);
                    int width = selectedImage.getWidth();
                    int height = selectedImage.getHeight();

                    Log.d("IMAGE WIDTH", Integer.toString(width));
                    Log.d("IMAGE HEIGHT", Integer.toString(height));
                    Matrix matrix = new Matrix();

                    File file = new File(getPath(getApplicationContext(), imageUri));
                    int orientation = 0;
                    ExifInterface exif = null;
                    try {
                        exif = new ExifInterface(file.getAbsolutePath());
                        orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        Log.d("ORIENTATION", Integer.toString(orientation));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (orientation == 6) {
                        matrix.setRotate(90);
                        shareImage = Bitmap.createBitmap(selectedImage, 0, 0, width, height, matrix, false);
                    } else {
                        shareImage = selectedImage;
                    }

                    selectedImageScaled = getResizedBitmap(selectedImage, height / 5, width / 5, matrix);

                    imageSaved = getBitmapAsByteArray(selectedImageScaled);

                   // newPicView.setVisibility(View.VISIBLE);

                    picView.setImageBitmap(selectedImageScaled);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth, Matrix m) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation

        // resize the bit map
        m.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, m, false);
        return resizedBitmap;
    }


}

