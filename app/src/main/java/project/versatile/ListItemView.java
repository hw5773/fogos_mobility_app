package project.versatile;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListItemView extends LinearLayout {
    TextView textView1;
    TextView textView2;

    public ListItemView(Context context) {
        super(context);
        init(context);
    }

    public ListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item, this, true);

        textView1 = (TextView) findViewById(R.id.title);
        textView2 = (TextView) findViewById(R.id.desc);
    }

    public void setTitle(String title) {
        textView1.setText(title);
    }

    public void setDesc(String desc) {
        textView2.setText(desc);
    }
}
