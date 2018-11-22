package project.versatile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import flexid.FlexID;
import flexid.FlexIDData;
import flexid.FlexIDFactory;

public class MobilityActivity extends AppCompatActivity {
    private static final String TAG = "FogOSMobilityActivity";
    TextView textView1; // My Flex ID
    TextView textView2; // My IP address
    TextView textView3; // Peer's Flex ID
    TextView textView4; // Peer's IP address
    Button startBtn;    // Start Button
    FlexIDFactory factory;
    FlexID myID, peer;
    Boolean flag = false;

    public static final String KEY_FLEX_ID_DATA = "flex";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Before setContentView");
        setContentView(R.layout.activity_mobility);

        Log.d(TAG, "Before constructing the Flex ID factory");
        factory = new FlexIDFactory();
        Log.d(TAG, "Before mapping the textView variables to the TextView resources");
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        startBtn = (Button) findViewById(R.id.startBtn);

        Log.d(TAG, "Before getting the intent");
        Intent intent = getIntent();
        Log.d(TAG, "Before processing the intent");
        processIntent(intent);
    }

    private void processIntent(Intent intent) {
        Log.d(TAG, "Starting processing the intent");
        if (intent != null) {
            Log.d(TAG, "Getting the extras from the bundle");
            Bundle bundle = intent.getExtras();
            Log.d(TAG, "Getting the bundle");
            FlexIDData data = (FlexIDData) bundle.getParcelable(KEY_FLEX_ID_DATA);
            Log.d(TAG, "Getting the data");
            peer = data.getId();
            Log.d(TAG, "Getting the Flex ID");
            myID = factory.getMyFlexID(peer);
            Log.d(TAG, "Getting my Flex ID");

            Log.d(TAG, "Before setting the textView 1");
            textView1.setText(peer.getIdentity().toString());
            Log.d(TAG, "Before setting the textView 2");
            textView2.setText(peer.getLocator().getAddr());
            Log.d(TAG, "Before setting the textView 3");
            textView3.setText(myID.getIdentity().toString());
            Log.d(TAG, "Before setting the textView 4");
            textView4.setText(myID.getLocator().getAddr());
        }
    }
}
