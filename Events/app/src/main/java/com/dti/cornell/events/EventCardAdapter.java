package com.dti.cornell.events;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by jaggerbrulato on 3/21/18.
 */

public class EventCardAdapter extends RecyclerView.Adapter<EventCardViewHolder> {
	private LayoutInflater mInflater;
	private List<Event> dataSource;

	public EventCardAdapter(Context context, List<Event> items)
	{
		dataSource = items;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@NonNull
	@Override
	public EventCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		return new EventCardViewHolder(mInflater.inflate(R.layout.card_event, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull EventCardViewHolder holder, int position)
	{
		holder.configure(dataSource.get(position));
	}

	@Override
	public int getItemCount()
	{
		return dataSource.size();
	}
}
