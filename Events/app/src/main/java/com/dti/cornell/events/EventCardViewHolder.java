package com.dti.cornell.events;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class EventCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
	private final ImageView image;
	private final TextView title;
	private final TextView location;
	private final TextView month;
	private final TextView day;
	private final TextView startTime;
	private final TextView numGoing;

	public EventCardViewHolder(View itemView)
	{
		super(itemView);
		image = itemView.findViewById(R.id.image);
		title = itemView.findViewById(R.id.title);
		location = itemView.findViewById(R.id.location);
		month = itemView.findViewById(R.id.month);
		day = itemView.findViewById(R.id.day);
		startTime = itemView.findViewById(R.id.startTime);
		numGoing = itemView.findViewById(R.id.numGoing);
	}

	public void configure(Event event)
	{
		//TODO
	}

	@Override
	public void onClick(View v)
	{
		//TODO
	}
}
