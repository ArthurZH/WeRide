package com.example.deanwen.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by liuchang on 8/1/15.
 */
public class MyAdapter extends BaseAdapter {

    List<FriendItem> list;

    Context ctxt;

    public void setList(List<FriendItem> list) {
        this.list = list;
    }

    LayoutInflater myInflatoer;

    public MyAdapter(List<FriendItem> list, Context ctxt) {
        this.list = list;
        this.ctxt = ctxt;
        myInflatoer = (LayoutInflater) ctxt
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        System.out.println("in constructor, print list size: " + list.size());

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = myInflatoer.inflate(R.layout.row, null);
        TextView text = (TextView) vi.findViewById(R.id.header);
        text.setText(list.get(position).getName());

        TextView email = (TextView) vi.findViewById(R.id.text);
        email.setText(list.get(position).getEmail());

        CheckedTextView checkedTextView = (CheckedTextView)vi.findViewById(R.id.checkbox);


        Boolean checked = list.get(position).isSelected();
        checkedTextView.setChecked(checked);


        return vi;
    }



}
