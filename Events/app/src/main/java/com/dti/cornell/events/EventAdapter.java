package com.dti.cornell.events;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dti.cornell.events.models.Event;

import java.util.List;

/**
 * Created by jaggerbrulato on 3/21/18.
 */

public class EventAdapter extends RecyclerView.Adapter<EventViewHolder> {
    private LayoutInflater mInflater;
    private List<Event> dataSource;

    public EventAdapter(Context context, List<Event> items) {
        dataSource = items;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new EventViewHolder(mInflater.inflate(R.layout.item_event, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position)
    {
        holder.configure(dataSource.get(position));
    }

    @Override
    public int getItemCount()
    {
        return dataSource.size();
    }
}
