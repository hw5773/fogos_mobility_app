package project.versatile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import java.util.ArrayList;
import java.util.Date;

public class FogOSMobility extends AppCompatActivity implements TextView.OnEditorActionListener {

    private ListView listView;
    private EditText editSearch;
    private MyListAdapter myListAdapter;
    private ArrayList<list_item> list_itemArrayList;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_app1);
        editSearch = (EditText) findViewById(R.id.editSearch);
        editSearch.setOnEditorActionListener(this);
        listView = (ListView) findViewById(R.id.my_listview);

        list_itemArrayList = new ArrayList<list_item>();

        list_itemArrayList.add(
                new list_item(R.mipmap.ic_launcher, "보라돌이", "제목1", new Date(System.currentTimeMillis()), "내용1")
        );

        list_itemArrayList.add(
                new list_item(R.mipmap.ic_launcher, "뚜비", "제목2", new Date(System.currentTimeMillis()), "내용2")
        );

        list_itemArrayList.add(
                new list_item(R.mipmap.ic_launcher, "나나", "제목3", new Date(System.currentTimeMillis()), "내용3")
        );

        list_itemArrayList.add(
                new list_item(R.mipmap.ic_launcher, "뽀", "제목4", new Date(System.currentTimeMillis()), "내용4")
        );

        list_itemArrayList.add(
                new list_item(R.mipmap.ic_launcher, "햇님", "제목5", new Date(System.currentTimeMillis()), "내용5")
        );

        myListAdapter = new MyListAdapter(FogOSMobility.this, list_itemArrayList);
        listView.setAdapter(myListAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test_app1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == R.id.editSearch && actionId == EditorInfo.IME_ACTION_DONE)
        {
            EditText idEdit = (EditText)findViewById(R.id.editSearch);
            Toast.makeText(getApplicationContext(), idEdit.getText().toString(), Toast.LENGTH_LONG).show();
        }

        return false;
    }
}
