package com.example.deanwen.myapplication;

/**
 * Created by XiaodongZhou on 8/1/15.
 */
import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by liuchang on 8/1/15.
 */
public class MyAdapter2 extends BaseAdapter {

    List<FriendItem> list;
    Context ctxt;
    LayoutInflater myInflater;

    public MyAdapter2(List<FriendItem> list, Context ctxt) {
        this.list = list;
        this.ctxt = ctxt;
        myInflater = (LayoutInflater) ctxt
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = myInflater.inflate(R.layout.row2, null);
        TextView text = (TextView) vi.findViewById(R.id.header2);
        text.setText(list.get(position).getName());

        TextView email = (TextView) vi.findViewById(R.id.text2);
        email.setText(list.get(position).getEmail());

        final String str = list.get(position).getEmail();
        final Button getOffButton = (Button) vi.findViewById(R.id.getOffButton);
        getOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long time = System.currentTimeMillis();
                Record r = FBActivity.db.getRecord(str);
                r.set_end_time(String.valueOf(time));
                FBActivity.db.updateRecord(r);
                List<Record> records = FBActivity.db.getAllRecords();

                for(Record record : records) {
                    if(record.get_end_time().equals("dummy")){
                        Record newRecord = new Record(record.get_email(),record.get_name(),record.get_start_time(),
                                record.get_end_time());
                        newRecord.set_start_time(String.valueOf(time));
                        FBActivity.db.updateRecord(newRecord);
                    }
                }
                getOffButton.setEnabled(false);
            }
        });
        return vi;
    }
}
