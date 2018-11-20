package project.versatile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import fogos.control.FogOSControl;

public class FogOSMobility extends AppCompatActivity {

    private EditText editSearch;
    private String flexIDManager;
    private FogOSControl control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobility);
        editSearch = (EditText) findViewById(R.id.edittext);
        control = new FogOSControl();
    }

    public void onButton1Clicked(View v) {
        String search = editSearch.getText().toString();
        JSONArray response = control.queryMessage(search);
        try {
            JSONObject obj = response.getJSONObject(0);
            Toast.makeText(getApplicationContext(), "입력된 검색어: " + search + "\n첫번째 제목: " + obj.getString("title"), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "입력된 검색어: " + search + "\n오류 발생", Toast.LENGTH_LONG).show();
        }
    }
}
