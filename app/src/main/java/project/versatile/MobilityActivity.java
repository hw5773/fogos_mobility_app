package project.versatile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import flexid.FlexID;
import flexid.FlexIDData;
import flexid.FlexIDFactory;

public class MobilityActivity extends AppCompatActivity {
    private static final String TAG = "FogOSMobilityActivity";
    private static int counter = 0;
    private TimerTask task;
    private Timer timer;

    FlexID myID, peer;
    Boolean flag = false;

    TextView textView1; // My Flex ID
    TextView textView2; // My IP address
    TextView textView3; // Peer's Flex ID
    TextView textView4; // Peer's IP address
    Button startBtn;    // Start Button
    ListView logListView;
    FlexIDFactory factory;
    LogListAdapter logListAdapter;

    public static final String KEY_FLEX_ID_DATA = "flex";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Before setContentView");
        setContentView(R.layout.activity_mobility);

        Log.d(TAG, "Before constructing the Flex ID factory");
        factory = new FlexIDFactory();
        logListAdapter = new LogListAdapter();
        Log.d(TAG, "Before mapping the textView variables to the TextView resources");
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        startBtn = (Button) findViewById(R.id.startBtn);

        Log.d(TAG, "Before getting the intent");
        Intent intent = getIntent();
        Log.d(TAG, "Before processing the intent");

        if (flag == false) {
            Log.d(TAG, "Process Intent");
            processIntent(intent);
        }

        if (flag == true) {
            startBtn.setText("실험 중지");
            processLog();
        }
/*
        task = new TimerTask() {
            @Override
            public void run() {
                counter++;
                textView1.setText(counter);
            }
        };

        timer = new Timer();
        timer.schedule(task, 3000);
*/
    }

    private void processIntent(Intent intent) {
        Log.d(TAG, "Starting processing the intent");
        if (intent != null) {
            Log.d(TAG, "Getting the extras from the bundle");
            Bundle bundle = intent.getExtras();
            Log.d(TAG, "Getting the bundle");

            try {
                FlexIDData data = (FlexIDData) bundle.getParcelable(KEY_FLEX_ID_DATA);
                Log.d(TAG, "Getting the data");
                peer = data.getId();
                Log.d(TAG, "Getting the Flex ID");
                myID = factory.getMyFlexID(peer);
                Log.d(TAG, "Getting my Flex ID");

                Log.d(TAG, "Before setting the textView 1");
                textView1.setText(new String(peer.getIdentity()));
                Log.d(TAG, "Before setting the textView 2");
                textView2.setText(peer.getLocator().getAddr());
                Log.d(TAG, "Before setting the textView 3");
                textView3.setText(new String(myID.getIdentity()));
                Log.d(TAG, "Before setting the textView 4");
                textView4.setText(myID.getLocator().getAddr());
            } catch (Exception e) {
                Log.d(TAG, "Exception: " + Log.getStackTraceString(e));
            }
        }
    }

    void processLog() {
        Log.d(TAG, "process log");
    }

    class LogListAdapter extends BaseAdapter {
        ArrayList<LogItem> itemArray = new ArrayList<LogItem>();

        public void addItem(LogItem item) {
            itemArray.add(item);
        }

        public void delAllItem() {
            itemArray.clear();
        }

        @Override
        public int getCount() {
            return itemArray.size();
        }

        @Override
        public Object getItem(int position) {
            return itemArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LogItemView view = new LogItemView(getApplicationContext());
            LogItem item = itemArray.get(position);
            view.setType(item.getType().name());
            view.setValue(item.getValue());
            view.setFrom(item.getFrom());
            view.setTo(item.getTo());

            return view;
        }
    }
}
