package project.versatile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ContentAdapter extends BaseAdapter {

    private ArrayList<ContentListViewItem> listViewItemList = new ArrayList<ContentListViewItem>();

    public ContentAdapter() {
    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // ViewHoldr 패턴
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_content_item, parent, false);
        }

        TextView nameView = (TextView) convertView.findViewById(R.id.content_name);
        TextView pathView = (TextView) convertView.findViewById(R.id.content_path);

        ContentListViewItem listViewItem = listViewItemList.get(position);
        nameView.setText(listViewItem.getName());
        pathView.setText(listViewItem.getPath());
        ((ListView)parent).setItemChecked(position, listViewItem.getShared());

        return convertView;
    }

    public void addItem(String name, String path, Boolean shared) {
        ContentListViewItem item = new ContentListViewItem();
        item.setName(name);
        item.setPath(path);
        item.setShared(shared);
        listViewItemList.add(item);
    }

    public boolean isExist(String text) {
        for (int i = 0; i < listViewItemList.size(); i++) {
            ContentListViewItem item = listViewItemList.get(i);
            if (item.getName() == text) {
                return true;
            }
        }
        return false;
    }

}