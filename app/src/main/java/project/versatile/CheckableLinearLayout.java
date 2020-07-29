package project.versatile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.Toast;


public class CheckableLinearLayout extends LinearLayout implements Checkable {
    private Context mContext;

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public boolean isChecked() {
        CheckBox cb = (CheckBox) findViewById(R.id.contentCheckBox) ;

        return cb.isChecked() ;
    }

    @Override
    public void toggle() {
        CheckBox cb = (CheckBox) findViewById(R.id.contentCheckBox) ;

        setChecked(cb.isChecked() ? false : true) ;
    }

    @Override
    public void setChecked(boolean checked) {
        CheckBox cb = (CheckBox) findViewById(R.id.contentCheckBox) ;
        Log.d("!!!!!~~~~!! Registered", String.valueOf(checked));

        if (cb.isChecked() != checked) {
            cb.setChecked(checked);

            if (checked) {
                // send register message
                Log.d("!!!!!!!!!! Registered", String.valueOf(checked));

            } else {
                // send deregister message
            }

            /*
            if (cb.isChecked() == true) {
                DialogClick();
            } else {
                cb.setChecked(checked) ;
            }
            */
        }
    }

    public void DialogClick() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("").setMessage("컨텐츠 등록을 해지 하십니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(mContext.getApplicationContext(), "", Toast.LENGTH_LONG).show();
                CheckBox cb = (CheckBox) findViewById(R.id.contentCheckBox) ;
                cb.setChecked(false);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CheckBox cb = (CheckBox) findViewById(R.id.contentCheckBox) ;
                cb.setChecked(true);
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}