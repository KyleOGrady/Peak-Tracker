package kyle.peaktracker;

import android.app.Dialog;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class EditClaimActivity extends AppCompatActivity {

    DatabaseAccess access = DatabaseAccess.getInstance(this);
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
        final String comments = bundle.getString("COMMENTS");
        final String date = bundle.getString("DATE");

        final EditText editComments = findViewById(R.id.editComments);
        final EditText editDate = findViewById(R.id.editDate);
        Button saveChanges = findViewById(R.id.saveChanges);

        editComments.setText(comments);
        editDate.setText(date);

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String date = editDate.getText().toString();
                String comments = editComments.getText().toString();

                access.open();
                access.claimPeak(peakName, date, comments, tableName);
                access.close();

                finish();
            }
        });

    }
}
