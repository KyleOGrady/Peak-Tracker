package kyle.peaktracker;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
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
import java.io.File;
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
                new DatePickerDialog(ClaimPeakActivity.this, R.style.datepicker, date, myCalendar
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

                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (shouldShowRequestPermissionRationale(
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            // Explain to the user why we need to read the contacts
                        }

                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);

                        // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                        // app-defined int constant that should be quite unique

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

                    File file= new File(getPath(getApplicationContext(), imageUri));
                    int orientation = 0;
                    ExifInterface exif = null;
                    try {
                        exif = new ExifInterface(file.getAbsolutePath());
                        orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        Log.d("ORIENTATION", Integer.toString(orientation));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(orientation == 6){
                        matrix.setRotate(90);
                    }

                    Bitmap selectedImageScaled = getResizedBitmap(selectedImage, height / 5, width / 5, matrix);
                    //Bitmap selectedImageScaled = Bitmap.createScaledBitmap(selectedImage, width/10, height/10, true);
//                    Log.d("SELECTED SIZE", Integer.toString(selectedImage.getByteCount()));
//                    Log.d("SELECTED SCALED SIZE", Integer.toString(selectedImageScaled.getByteCount()));


                    //Log.d("ORIENTATION", "MESSAGE " + path);
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

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth, Matrix m)
    {
        int width = bm.getWidth();
        int height = bm.getHeight();
//        Log.d("IMAGE WIDTH", Integer.toString(width));
//        Log.d("IMAGE HEIGHT", Integer.toString(height));
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation

       // m.preScale(1.0f, -1.0f);

        // resize the bit map
        m.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, m, false);
        return resizedBitmap;
    }

    public static String getPath(final Context context, final Uri uri) {

        String nopath = "no path";
        // check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return nopath;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return "no path";
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

}
