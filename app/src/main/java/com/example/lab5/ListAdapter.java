package com.example.lab5;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<String> {

    private ArrayList<String> dataList;
    private LayoutInflater inflater;

    public ListAdapter(Context context, ArrayList<String> dataList) {
        super(context, 0, dataList);
        this.dataList = dataList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            holder = new ViewHolder();
            holder.text = convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.text.setText(dataList.get(position));
        return convertView;
    }

    static class ViewHolder {
        TextView text;
    }
}
