package project.versatile;

import FlexID.*;

import FlexID.FlexID;
import FogOSClient.FogOSClient;
import FogOSMessage.QueryMessage;
import FogOSMessage.ReplyMessage;
import FogOSMessage.RequestMessage;
import FogOSMessage.ResponseMessage;
import FogOSMessage.ReplyEntry;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


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

    private ListView mListView;
    private ContentAdapter mContentAdapter;

    TextView percent;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permissionCheck();
    }


    public void initialize() {

        fogos = new FogOSClient(Environment.getExternalStorageDirectory().getPath());

        // Generate the list with the search bar
        setContentView(R.layout.activity_main);
        editSearch = (EditText) findViewById(R.id.editText);
        listView = (ListView) findViewById(R.id.listView1);

        mListView = (ListView) findViewById(R.id.listView2);
        mContentAdapter = new ContentAdapter();
        mListView.setAdapter(mContentAdapter);
        FloatingActionButton contentAddButton = findViewById(R.id.contentAddButton);
        contentAddButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemDialog(FogOSMobilityClient.this);
            }
        });

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost1);
        tabHost.setup();

        TabHost.TabSpec searchTab = tabHost.newTabSpec("Tab Search");
        searchTab.setContent(R.id.content1);
        searchTab.setIndicator("검색");
        tabHost.addTab(searchTab);

        TabHost.TabSpec contentListTab = tabHost.newTabSpec("Tab Add");
        contentListTab.setContent(R.id.content2);
        contentListTab.setIndicator("컨텐츠 리스트");
        tabHost.addTab(contentListTab);

        genContentList();

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
                Locator loc = new Locator(InterfaceType.ETH, "147.46.114.86", 5556);
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

    void genContentList() {

        // 데이터 생성
        mContentAdapter.addItem("picture_1", "/somewhere/", true);
        mContentAdapter.addItem("picture_2", "/someplace/", true);

        /*
        Content[] contentList = fogos.getContentList();

        String a = "A";
        for (int i = 0; i < contentList.length; i++) {
            Content d = contentList[i];
            Log.d("", d.getName());
            Log.d("", d.getPath());
            if (d.isShared()) {
                Log.d("True -", a);
            } else {
                Log.d("False -", a);
            }

            mContentAdapter.addItem(d.getName(), d.getPath(), d.isShared());
        }
        */

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

    private void addItemDialog(Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);

        final LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.layout_add_url, null);
        final Button downloadButton = (Button) view.findViewById(R.id.content_download_button);
        final String[] fileUrl = {""};


        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        percent = (TextView)view.findViewById(R.id.progressPercent);


        builder.setView(view);
        builder.setTitle("컨텐츠 추가").setMessage("\n컨텐츠의 URL을 입력하세요");
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (fileUrl[0] != "") {
                    if (!mContentAdapter.isExist(fileUrl[0])) {
                        String downloadedFileName = fileUrl[0].substring(fileUrl[0].lastIndexOf('/')+1);

                        mContentAdapter.addItem(downloadedFileName, fileUrl[0],false);
                        mContentAdapter.notifyDataSetChanged();
                    }
                }
                dialog.cancel();
            }
        });
        builder.setPositiveButton("등록", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (fileUrl[0] != "") {
                    if (!mContentAdapter.isExist(fileUrl[0])) {
                        String downloadedFileName = fileUrl[0].substring(fileUrl[0].lastIndexOf('/')+1);

                        mContentAdapter.addItem(downloadedFileName, fileUrl[0],true);
                        mContentAdapter.notifyDataSetChanged();
                    }
                }
                // TODO: send register message
            }
        });

        builder.show();

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText urlInput = view.findViewById(R.id.content_url);
                fileUrl[0] = urlInput.getText().toString();

                fileUrl[0] = "https://hyeonmin-lee.github.io/files/HyeonminLee_cv.pdf";
                Log.d("!!!!!!!!!!!!!!!!!!!!! ", fileUrl[0]);

                File filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                String downloadFileName = fileUrl[0].substring(fileUrl[0].lastIndexOf('/'), fileUrl[0].length());
                File outputFile = new File(filePath + "/" + downloadFileName);

                // TODO: Must remove this code;
                outputFile.delete();

                if (outputFile.exists()) {
                    Toast.makeText(getApplicationContext(), "파일이 이미 존재합니다.", Toast.LENGTH_LONG).show();
                } else {
                    new DownloadFile().execute(fileUrl[0]);
                }
            }
        });

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

    private class DownloadFile extends AsyncTask<String, String, Long> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setProgress(0);
            progressBar.setMax(100);

        }

        @Override
        protected Long doInBackground(String... strings) {
            long fileLength = -1;

            try {

                String urlString = strings[0];
                String downloadFileName = urlString.substring(urlString.lastIndexOf('/')+1);

                URL url = new URL(urlString);
                URLConnection connection = url.openConnection();
                connection.connect();
                fileLength = connection.getContentLength();
                File filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                File outputFile = new File(filePath + "/" + downloadFileName);
                OutputStream output = new FileOutputStream(outputFile);

                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;

                    if (total > 0) {
                        float per = (float) (100*total / fileLength);
                        String tmpFileLen = String.valueOf(fileLength) + "KB";
                        if (fileLength > 1000) {
                            tmpFileLen = String.valueOf((int) fileLength/1000) + "MB";
                        }

                        String perStr = "Download " + (int) per + "% " + "(Size: " + tmpFileLen + ")" ;
                        publishProgress("" + (int) ((total * 100) / fileLength), perStr);
                    }
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return fileLength;
        }

        @Override
        protected void onProgressUpdate(String... values) {

            progressBar.setProgress(Integer.parseInt(values[0]));
            percent.setText(values[1]);

            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Long size) {
            super.onPostExecute(size);
        }
    }
}
