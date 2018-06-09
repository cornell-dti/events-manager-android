package com.dti.cornell.events;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dti.cornell.events.utils.EventUtils;

public class EventViewHolder extends RecyclerView.ViewHolder
{
	private final TextView startTime;
	private final TextView endTime;
	private final TextView title;
	private final TextView location;
	private final TextView friendsGoing;

	public EventViewHolder(View itemView)
	{
		super(itemView);
		startTime = itemView.findViewById(R.id.startTime);
		endTime = itemView.findViewById(R.id.endTime);
		title = itemView.findViewById(R.id.eventTitle);
		location = itemView.findViewById(R.id.eventLocation);
		friendsGoing = itemView.findViewById(R.id.friendsGoing);
	}

	public void configure(Event event)
	{
		startTime.setText(event.startTime.toString(EventUtils.timeFormat));
		endTime.setText(event.endTime.toString(EventUtils.timeFormat));
		title.setText(event.name);
		location.setText(event.location);
		friendsGoing.setText(event.friendsGoing);
	}
}
