package com.dti.cornell.events;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dti.cornell.events.models.Event;

import java.util.Collections;
import java.util.List;

/**
 * Created by jaggerbrulato on 3/21/18.
 */

class EventCardAdapter extends RecyclerView.Adapter<EventCardViewHolder> {
	private final LayoutInflater inflater;
	private List<Event> data = Collections.emptyList();

	public EventCardAdapter(Context context)
	{
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@NonNull
	@Override
	public EventCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		return new EventCardViewHolder(inflater.inflate(R.layout.card_event, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull EventCardViewHolder holder, int position)
	{
		holder.configure(data.get(position));
	}

	@Override
	public int getItemCount()
	{
		return data.size();
	}

	public void setData(List<Event> data)
	{
		this.data = data;
		notifyDataSetChanged();
	}
}
