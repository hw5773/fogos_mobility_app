package project.versatile;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LogItemView extends LinearLayout {
    TextView typeTextView;
    TextView valueTextView;
    TextView fromTextView;
    TextView toTextView;

    public LogItemView(Context context) {
        super(context);
        init(context);
    }

    public LogItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.log_item, this, true);

        typeTextView = (TextView) findViewById(R.id.type);
        valueTextView = (TextView) findViewById(R.id.value);
        fromTextView = (TextView) findViewById(R.id.from);
        toTextView = (TextView) findViewById(R.id.to);
    }

    public void setType(String type) {
        typeTextView.setText(type);
    }

    public void setValue(String value) {
        valueTextView.setText(value);
    }

    public void setFrom(String from) {
        fromTextView.setText(from);
    }

    public void setTo(String to) {
        toTextView.setText(to);
    }
}
