package project.versatile;

import FlexID.FlexID;
import FlexID.FlexIDFactory;
import FogOSSecurity.Role;
import FogOSSecurity.SecureFlexIDSession;
import FogOSSocket.FlexIDSession;
import FogOSSocket.SessionLogger;
import FogOSSocket.LogItem;
import FogOSSocket.LogType;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.ByteArrayDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.upstream.UdpDataSource;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MobilityActivity extends AppCompatActivity implements TransferListener {
    private static final String TAG = "FogOSMobilityActivity";
    private static final int MAX_PACKET_SIZE = 32768;
    private static int counter = 0;
    private static boolean ready = false;
    private TimerTask task;
    private Timer timer;
    String prevIP;
    byte b[] = new byte[MAX_PACKET_SIZE];

    File tempDir, tempFile;

    FlexID myID, peer;
    boolean flag = false;
    boolean change = false;
    int test = 0;
    SessionLogger sessionLogger;

    TextView textView1;     // My Flex ID
    TextView textView2;     // My IP address
    TextView textView3;     // Peer's Flex ID
    TextView textView4;     // Peer's IP address
    Button startBtn;        // Start Button
    ListView logListView;
    FlexIDFactory factory;
    SecureFlexIDSession secureFlexIDSession;
    FlexIDSession session;
    LogListAdapter logListAdapter;
    BackgroundThread backgroundThread;
    ReceiverThread receiverThread;
    ReadyThread readyThread;

    SimpleExoPlayer player;
    PlayerView playerView;

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
        playerView = (PlayerView) findViewById(R.id.player_view);

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

        tempDir = new File(Environment.getExternalStorageDirectory(), "temp");
        if(!tempDir.isDirectory()) {
            if (!tempDir.mkdirs()) {
                Toast.makeText(this, "폴더 생성 실패", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "폴더 생성 성공", Toast.LENGTH_SHORT).show();
            }
        }
        tempFile = new File(tempDir, "temp.mp4");

        player = ExoPlayerFactory.newSimpleInstance(this.getApplicationContext());
        playerView.setPlayer(player);

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

            Log.d(TAG, "세션 초기화");
            Log.d(TAG, "나의 IP: " + myID.getLocator().getAddr());
            Log.d(TAG, "나의 Port: " + myID.getLocator().getPort());
            Log.d(TAG, "상대의 IP: " + peer.getLocator().getAddr());
            Log.d(TAG, "상대의 Port: " + peer.getLocator().getPort());

            prevIP = myID.getLocator().getAddr();

            Log.d(TAG, "세션 생성 성공");
            receiverThread = new ReceiverThread();
            Log.d(TAG, "수신 쓰레드");
            receiverThread.setRunning(true);
            Log.d(TAG, "수신 쓰레드 시작");

            // Init the thread to invoke the handleMessage periodically
            Log.d(TAG, "백그라운드 쓰레드 시작");
            backgroundThread = new BackgroundThread();
            backgroundThread.setRunning(true);

            Log.d(TAG, "WiFi 모니터링 쓰레드 시작");
            readyThread = new ReadyThread();

            receiverThread.start();
            backgroundThread.start();
            readyThread.start();

            //player.prepare(getMediaSourceFromByteArray(b));
            Toast.makeText(this, "실험 시작", Toast.LENGTH_LONG).show();
        }
        else {
            flag = false;
            startBtn.setText("실험 시작");
            Toast.makeText(this, "실험 종료", Toast.LENGTH_LONG).show();
            boolean retry = true;
            backgroundThread.setRunning(false);
            player.stop(true);

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

    private void prepareExoPlayerFromFileUri(Uri uri){
        DataSpec dataSpec = new DataSpec(uri);
        final FileDataSource fileDataSource = new FileDataSource();
        try {
            fileDataSource.open(dataSpec);
        } catch (FileDataSource.FileDataSourceException e) {
            e.printStackTrace();
        }

        DataSource.Factory factory = () -> fileDataSource;
        MediaSource audioSource = new ExtractorMediaSource(fileDataSource.getUri(),
                factory, new DefaultExtractorsFactory(), null, null);

        player.prepare(audioSource);
    }

    // API to get a MediaSource from byte array, put this method to the code in line 198.
    private MediaSource getMediaSourceFromByteArray(byte[] data) {
        final ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(data);
        DataSource.Factory factory = () -> byteArrayDataSource;

        MediaSource mediaSource = new ExtractorMediaSource(byteArrayDataSource.getUri(),
                factory, new DefaultExtractorsFactory(), null, null);
        return mediaSource;
    }

    // example code for playing a content from the outside http server
    private MediaSource getMediaSourceFromHttp() {
        String sample = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";

        Uri uri = Uri.parse(sample);
        String userAgent = Util.getUserAgent(this.getApplicationContext(), "fog_os");
        DataSource.Factory httpSourceFactory = new DefaultHttpDataSourceFactory(userAgent, this);
        return new ProgressiveMediaSource.Factory(httpSourceFactory).createMediaSource(uri);
    }

    private MediaSource prepareExoplayerFromFogOsSocket(FlexIDSession session, int limit) {
        String sample = "udp://147.47.208.67:5556"; // meaningless
        Uri uri = Uri.parse(sample);
        DataSpec dataSpec = new DataSpec(uri);

        FogOsDataSource fogOsDataSource = new FogOsDataSource(session, limit, MAX_PACKET_SIZE);
        try {
            fogOsDataSource.open(dataSpec);
        } catch (FogOsDataSource.FogOsDataSourceException e) {
            e.printStackTrace();
        }

        DataSource.Factory factory = () -> fogOsDataSource;
        MediaSource mediaSource = new ExtractorMediaSource(fogOsDataSource.getUri(), factory,
                new DefaultExtractorsFactory(), null, null);
        return mediaSource;
    }

    private void processIntent(Intent intent) {
        Log.d(TAG, "Starting processing the intent");
        if (intent != null) {
            Log.d(TAG, "Getting the extras from the bundle");
            Bundle bundle = intent.getExtras();
            Log.d(TAG, "Getting the bundle");

            try {
                FlexIDParcel data = (FlexIDParcel) bundle.getParcelable(KEY_FLEX_ID_DATA);
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

    /* Test Code to change the WiFi setting */
    /*
    private void changeWifiSetting() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Activity.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        String newSsid = "FogOS2";
        String pass = "mmlab2015";
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + newSsid + "\"";
        conf.preSharedKey = "\"" + pass + "\"";

        //int ret = wifiManager.updateNetwork(conf);

        Log.d(TAG, "Current SSID: " + ssid);
        //Log.d(TAG, "Return Value: " + ret);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

        if (list == null) {
            Log.d(TAG, "Returned null");
        } else {
            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + newSsid + "\"")) {
                    Toast.makeText(getApplicationContext(), "Device is disconnecting from FogOS1", Toast.LENGTH_LONG).show();
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    Toast.makeText(getApplicationContext(), "Device is connecting to FogOS2", Toast.LENGTH_LONG).show();
                    wifiManager.reconnect();
                    break;
                }
            }
            // Toast.makeText(getApplicationContext(), "Now send MapUpdate message to Peer and Flex ID Manager", Toast.LENGTH_LONG).show();
        }
    }
    */

    // Get a new item for the listview
    private void handleMessage(Message msg) {
        int numOfLog = 0;
        if (sessionLogger == null) {
            Log.d(TAG, "SessionLogger is null");
        } else {
            Log.d(TAG, "Finding the log");
            numOfLog = sessionLogger.getNumOfLog();
            Log.d(TAG, "Number of Log: " + numOfLog);

            if (numOfLog > 0) {
                LogItem item;
                for (int i=0; i<numOfLog; i++) {
                    Log.d(TAG, "Logger in index: " + i);
                    item = sessionLogger.getSessionLog();
                    logListAdapter.addItem(item);
                    if (item.getType() == LogType.REBINDING) {
                        textView2.setText(item.getTo());
                    }
                }

                // Test Code: Changing the AP to the other one.
                /*
                if ((change == false) && (logListAdapter.getCount() > 0)) {
                    //changeWifiSetting();
                    change = true;
                }
                */
                logListView.setAdapter(logListAdapter);
            }
        }
    }

    @Override
    public void onTransferInitializing(DataSource source, DataSpec dataSpec, boolean isNetwork) {
        Log.d("listner","transfer initializing");
    }

    @Override
    public void onTransferStart(DataSource source, DataSpec dataSpec, boolean isNetwork) {
        Log.d("listner","transfer starting");
    }

    @Override
    public void onBytesTransferred(DataSource source, DataSpec dataSpec, boolean isNetwork, int bytesTransferred) {
        Log.d("listner","transferred byte: " + bytesTransferred);
    }

    @Override
    public void onTransferEnd(DataSource source, DataSpec dataSpec, boolean isNetwork) {
        Log.d("listner","transfer ended");
    }

    class LogListAdapter extends BaseAdapter {
        ArrayList<LogItem> itemArray = new ArrayList<LogItem>();

        public void addItem(LogItem item) {
            itemArray.add(0, item);
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
                    sleep(1000);
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
            System.out.println("Ready is set to true.");
            if (session != null)
                session.setReadyToConnect(true);
        }
    }

    class ReceiverThread extends Thread {
        boolean running = false;

        void setRunning(boolean b) { running = b; }

        @Override
        public void run() {
            session = new FlexIDSession(myID, peer, null, true);
            sessionLogger = session.getSessionLogger();
            //secureFlexIDSession = new SecureFlexIDSession(Role.INITIATOR, myID, peer);
            //sessionLogger = secureFlexIDSession.getFlexIDSession().getSessionLogger();
            //secureFlexIDSession.doHandshake();
            int limit = 1056768;
            runOnUiThread(() -> {
                player.prepare(prepareExoplayerFromFogOsSocket(session, limit));

                // player.prepare(getMediaSourceFromHttp());
                player.setPlayWhenReady(true);
            });
            /*
            try {
                if (tempFile.exists()) {
                    tempFile.delete();
                    tempFile.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(tempFile);
                int recv = 0;

                // limit = ((b[0] << 24) & 0xff) | ((b[1] << 16) & 0xff) | ((b[2] << 8) & 0xff) | (b[3] & 0xff);
                // boolean check = true;

                while (running) {
                    int count = session.receive(b);

                    if (count > 0) {
                        recv += count;
                        /* Log.v("buff", "count: " + count + " recv:" + recv);
                        // fos.write(b, 0, count);
                        if(check) {
                            Log.v("mckwak", byteArrayToHex(b, 128));
                            check = false;
                        }
                    }
                    if (recv >= limit) {
                        Log.v("receiver", "running = false");
                        break;
                    }
                }
                // fos.close();
                runOnUiThread(() -> {
                    // Log.v("buff", "ui thread started");
                    // prepareExoPlayerFromFileUri(Uri.fromFile(tempFile));
                    // player.setPlayWhenReady(true);
                    // Log.v("buff", "exoplayer ready");
                });
                session.close();
            } catch (Exception e){
                e.printStackTrace();
            } */
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
    static String byteArrayToHex(byte[] a, int len) {
        byte[] tmp = new byte[len];
        int idx = 0;
        StringBuilder sb = new StringBuilder();
        System.arraycopy(a, 0, tmp, 0, len);

        for (final byte b: tmp) {
            sb.append(String.format("0x%02x, ", b & 0xff));
            idx++;
            if (idx % 8 == 0)
                sb.append("\n");
        }
        return sb.toString();
    }

}