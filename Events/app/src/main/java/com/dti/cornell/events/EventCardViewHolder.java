package com.dti.cornell.events;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.utils.TagUtil;

public class EventCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
	private final TextView title;
	private final TextView location;
	private final TextView month;
	private final TextView day;
	private final TextView startTime;
	private final TextView numGoing;
	private Event event;

	public EventCardViewHolder(View itemView)
	{
		super(itemView);
		ImageView image = itemView.findViewById(R.id.image);
		title = itemView.findViewById(R.id.title);
		location = itemView.findViewById(R.id.location);
		month = itemView.findViewById(R.id.month);
		day = itemView.findViewById(R.id.day);
		startTime = itemView.findViewById(R.id.startTime);
		numGoing = itemView.findViewById(R.id.numGoing);
		itemView.setOnClickListener(this);
	}

	public void configure(Event event)
	{
		this.event = event;
		title.setText(event.title);
		location.setText(event.location);
		month.setText(event.startTime.toString("MMM"));
		day.setText(event.startTime.toString("d"));
		startTime.setText(event.startTime.toString("h:mm a"));
		numGoing.setText(Integer.toString(event.participantIDs.size()));
	}


	@Override
	public void onClick(View view)
	{
		DetailsActivity.startWithEvent(event, itemView.getContext());
		for(Integer id : event.tagIDs){
			TagUtil.addTagToList(id);
		}
	}
}
