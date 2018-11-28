package project.versatile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import versatile.flexid.FlexID;
import versatile.flexid.FlexIDFactory;
import versatile.flexidsession.FlexIDSession;

public class MobilityActivity extends AppCompatActivity {
    private static final String TAG = "FogOSMobilityActivity";
    private static int counter = 0;
    private static boolean ready = false;
    private TimerTask task;
    private Timer timer;

    FlexID myID, peer;
    Boolean flag = false;
    int test = 0;

    TextView textView1;     // My Flex ID
    TextView textView2;     // My IP address
    TextView textView3;     // Peer's Flex ID
    TextView textView4;     // Peer's IP address
    Button startBtn;        // Start Button
    ListView logListView;
    FlexIDFactory factory;
    FlexIDSession session;
    LogListAdapter logListAdapter;
    BackgroundThread backgroundThread;
    ReceiverThread receiverThread;
    ReadyThread readyThread;

    private final LogHandler logHandler = new LogHandler(this);

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
        logListView = (ListView) findViewById(R.id.logList);

        logListAdapter = new LogListAdapter();
        logListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogItem item = (LogItem) logListAdapter.getItem(position);
                if (item.getType() == LogType.DATA) {
                    Toast.makeText(getApplicationContext(), "Data) From " + item.getFrom() + " to " + item.getTo() + " : "
                            + item.getValue(), Toast.LENGTH_LONG).show();
                }
                else if (item.getType() == LogType.REBINDING) {
                    Toast.makeText(getApplicationContext(), "Rebinding) From " + item.getFrom() + " to " + item.getTo() + " : "
                            + item.getValue(), Toast.LENGTH_LONG).show();
                }
            }
        });

        Log.d(TAG, "Before getting the intent");
        Intent intent = getIntent();
        Log.d(TAG, "Before processing the intent");

        processIntent(intent);
    }

    // Function that is invoked when the button is clicked
    public void onButton2Clicked(View v) {
        if (flag == false) {
            Log.d(TAG, "실험 시작");
            flag = true;
            startBtn.setText("실험 종료");

            // Init the thread to invoke the handleMessage periodically
            Log.d(TAG, "백그라운드 쓰레드 시작");
            backgroundThread = new BackgroundThread();
            backgroundThread.setRunning(true);
            backgroundThread.start();

            Log.d(TAG, "세션 초기화");
            Log.d(TAG, "나의 IP: " + myID.getLocator().getAddr());
            Log.d(TAG, "나의 Port: " + myID.getLocator().getPort());
            Log.d(TAG, "상대의 IP: " + peer.getLocator().getAddr());
            Log.d(TAG, "상대의 Port: " + peer.getLocator().getPort());

            Log.d(TAG, "세션 생성 성공");
            receiverThread = new ReceiverThread();
            Log.d(TAG, "수신 쓰레드");
            receiverThread.setRunning(true);
            Log.d(TAG, "수신 쓰레드 시작");
            receiverThread.start();

            readyThread = new ReadyThread();
            readyThread.start();

            Toast.makeText(this, "실험 시작", Toast.LENGTH_LONG).show();
        }
        else {
            flag = false;
            startBtn.setText("실험 시작");
            Toast.makeText(this, "실험 종료", Toast.LENGTH_LONG).show();
            boolean retry = true;
            backgroundThread.setRunning(false);

            while (retry) {
                try {
                    backgroundThread.join();
                    retry = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
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
                textView1.setText(new String(myID.getIdentity()));
                Log.d(TAG, "Before setting the textView 2");
                textView2.setText(myID.getLocator().getAddr());
                Log.d(TAG, "Before setting the textView 3");
                textView3.setText(new String(peer.getIdentity()));
                Log.d(TAG, "Before setting the textView 4");
                textView4.setText(peer.getLocator().getAddr());
            } catch (Exception e) {
                Log.d(TAG, "Exception: " + Log.getStackTraceString(e));
            }
        }
    }

    void processLog() {
        Log.d(TAG, "process log");
    }

    public static void setReady(boolean ready) {
        MobilityActivity.ready = ready;
    }

    private void changeWifiSetting() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Activity.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        String newSsid = "mmlab2";
        String pass = "mmlab2017";
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + newSsid + "\"";
        conf.preSharedKey = "\"" + pass + "\"";

        int ret = wifiManager.addNetwork(conf);

        Log.d(TAG, "Current SSID: " + ssid);
        Log.d(TAG, "Return Value: " + ret);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

        if (list == null) {
            Log.d(TAG, "Returned null");
        } else {
            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + newSsid + "\"")) {
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();

                    break;
                }
            }
        }
    }

    // Get a new item for the listview
    private void handleMessage(Message msg) {
        // TODO: Add a new item to the list.
        if (test == 0) {
            logListAdapter.addItem(new LogItem(LogType.DATA, 12, peer.getLocator().getAddr(), myID.getLocator().getAddr()));
            test++;
            changeWifiSetting();
        } else if (test == 1) {
            textView2.setText("192.168.1.2");
            logListAdapter.addItem(new LogItem(LogType.REBINDING, 15.3, myID.getLocator().getAddr(), "192.168.1.2"));
            test++;
        } else {
            // Toast.makeText(getApplicationContext(), "Test", Toast.LENGTH_LONG).show();
        }
        logListView.setAdapter(logListAdapter);
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

    public class BackgroundThread extends Thread {
        boolean running = false;

        void setRunning(boolean b) {
            running = b;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    Log.getStackTraceString(e);
                }
                logHandler.sendMessage(logHandler.obtainMessage());
            }
        }
    }

    class ReadyThread extends Thread {
        @Override
        public void run() {
            System.out.println("Ready Thread is started.");
            while (ready == false) {}
            System.out.println("ready is set to true.");
            if (session != null)
                session.setReady(true);
        }
    }

    class ReceiverThread extends Thread {
        boolean running = false;

        void setRunning(boolean b) { running = b; }

        @Override
        public void run() {
            session = new FlexIDSession(myID, peer);
            int recv = -1, limit = 0;
            byte b[] = new byte[1024];

            while (recv < 0)
                recv = session.receive(b);

            limit = ((b[0] << 24) & 0xff) | ((b[1] << 16) & 0xff) | ((b[2] << 8) & 0xff) | (b[3] & 0xff);

            while (running) {
                recv += session.receive(b);

                if (recv >= limit)
                    running = true;
            }
        }
    }

    static class LogHandler extends Handler {
        private final WeakReference<MobilityActivity> mActivity;
        public LogHandler(MobilityActivity activity) {
            mActivity = new WeakReference<MobilityActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MobilityActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }
}