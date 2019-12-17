package project.versatile;

import org.json.JSONObject;

import FlexID.*;

import FlexID.FlexID;
import FogOSClient.FogOSClient;
import FogOSMessage.QueryMessage;
import FogOSMessage.ReplyMessage;
import FogOSMessage.RequestMessage;
import FogOSMessage.ResponseMessage;
import FogOSMessage.ReplyEntry;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;


public class FogOSMobilityClient extends AppCompatActivity {
    public static final int REQUEST_CODE_MENU = 101;
    public static final String KEY_FLEX_ID_DATA = "flex";
    private static final String TAG = "FogOS";
    private static final int PERMISSON_REQUEST = 1001;

    private EditText editSearch;
    private ListView listView;
    private FogOSClient fogos;
    private ListAdapter listAdapter;
    private int count = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permissionCheck();
    }

    public void initialize() {
        fogos = new FogOSClient(Environment.getExternalStorageDirectory().getPath());

        // Generate the list with the search bar
        setContentView(R.layout.activity_main);
        editSearch = (EditText) findViewById(R.id.edittext);
        listView = (ListView) findViewById(R.id.listView);

        listAdapter = new ListAdapter();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RequestMessage requestMessage;
                ResponseMessage responseMessage;
                Intent intent = new Intent(getApplicationContext(), MobilityActivity.class);
                ListItem item = (ListItem) listAdapter.getItem(position);
                FlexID peer = item.getFlexID();
                Toast.makeText(getApplicationContext(), "선택: " + item.getTitle(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "After getting the peer's Flex ID");
                requestMessage = fogos.makeRequestMessage(peer);

                // TODO: We should change the test message below
                // fogos.sendRequestMessage(requestMessage);
                fogos.testRequestMessage(requestMessage);

                do {
                    responseMessage = fogos.getResponseMessage();
                } while (responseMessage == null);
                peer = responseMessage.getPeerID();
                // For Test
                Locator loc = new Locator(InterfaceType.ETH, "147.47.208.67", 5556);
                peer.setLocator(loc);
                Log.d(TAG, "After requesting the connection with the peer's Flex ID: " + peer.getStringIdentity() + "IP: " + peer.getLocator().getAddr() + " / Port: " + peer.getLocator().getPort());


                FlexIDParcel data = new FlexIDParcel(peer);
                Log.d(TAG, "After making the peer's Flex ID parcelable");
                intent.putExtra(KEY_FLEX_ID_DATA, data);
                Log.d(TAG, "After putting the extra data into the bundle");

                startActivityForResult(intent, REQUEST_CODE_MENU);
            }
        });
    }

    public void permissionCheck() {
        ArrayList<String> permissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.INTERNET);
        }

        if (permissions.size() > 0) {
            String[] reqPermissionArray = new String[permissions.size()];
            reqPermissionArray = permissions.toArray(reqPermissionArray);
            ActivityCompat.requestPermissions(this, reqPermissionArray, PERMISSON_REQUEST);
        } else {
            initialize();
        }

        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSON_REQUEST);
            }
        } else {
            initialize();
        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSON_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initialize();
                } else {
                    Toast.makeText(this, "No permission granted", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void onButton1Clicked(View v) {
        Log.d(TAG, "Button1 Clicked");
        QueryMessage queryMessage;
        ReplyMessage replyMessage;
        String keywords;
        ArrayList<ReplyEntry> replyList;

        count = (count + 1) % 2;
        Log.d(TAG, "Count: " + count);

        // Prepare the query message
        keywords = editSearch.getText().toString();
        Log.d(TAG, "Keyword: " + keywords);

        // input "test" for keywords, then the mock value will be returned.
        queryMessage = fogos.makeQueryMessage(keywords);
        Log.d(TAG, "queryMessage: " + queryMessage);

        // TODO: Send the query message and get the reply message
        // fogos.sendQueryMessage(queryMessage);
        fogos.testQueryMessage(queryMessage);
        do {
            replyMessage = fogos.getReplyMessage();
        } while (replyMessage == null);
        Log.d(TAG, "replyMessage: " + replyMessage);

        // TODO: ID List should be more abstracted.
        replyList = replyMessage.getReplyList();
        Log.d(TAG, "replyList: " + replyList);

        // Add the list with the list of the replies including a flex ID, a description, and a title
        try {
            listAdapter.delAllItem();
            ReplyEntry e;
            for (int i = 0; i < replyList.size(); i++) {
                e = replyList.get(i);
                listAdapter.addItem(new ListItem(e.getTitle(), e.getDesc(), e.getFlexID().getIdentity()));
            }
            listView.setAdapter(listAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ListAdapter extends BaseAdapter {
        ArrayList<ListItem> itemArray = new ArrayList<ListItem>();

        public void addItem(ListItem item) {
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
            ListItemView view = new ListItemView(getApplicationContext());
            ListItem item = itemArray.get(position);
            view.setTitle(item.getTitle());
            view.setDesc(item.getDesc());

            return view;
        }
    }
}
