package com.dti.cornell.events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaggerbrulato on 3/21/18.
 */

public class EventAdapter extends BaseAdapter {


    private Context mContext;
    private LayoutInflater mInflater;
    private List<Event> mDataSource;

    public EventAdapter(Context context, List<Event> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    //1
    @Override
    public int getCount() {
        return mDataSource.size();
    }

    //2
    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    //3
    @Override
    public long getItemId(int position) {
        return position;
    }

    //4
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.event_info, parent, false);
        ((TextView) rowView.findViewById(R.id.startTime)).setText(((Event) getItem(position)).startTime);
        ((TextView) rowView.findViewById(R.id.endTime)).setText(((Event) getItem(position)).endTime);
        ((TextView) rowView.findViewById(R.id.eventTitle)).setText(((Event) getItem(position)).name);
        ((TextView) rowView.findViewById(R.id.eventLocation)).setText(((Event) getItem(position)).location);
        ((TextView) rowView.findViewById(R.id.friendsGoing)).setText(String.valueOf(((Event) getItem(position)).friendsGoing));

        return rowView;
    }
}
