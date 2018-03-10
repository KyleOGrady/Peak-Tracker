package kyle.peaktracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class ViewClaimInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_claim_info_layout);

    }
}
