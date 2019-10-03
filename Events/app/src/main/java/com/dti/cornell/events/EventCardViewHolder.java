package com.dti.cornell.events;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.models.Location;
import com.dti.cornell.events.models.Organization;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.Internet;
import com.dti.cornell.events.utils.TagUtil;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class EventCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, Data.DataUpdateListener
{
	private final TextView title;
	private final TextView location;
	private final TextView month;
	private final TextView day;
	private final TextView startTime;
	private final TextView numGoing;
	private final ImageView image;
	private Event event;

	public EventCardViewHolder(View itemView)
	{
		super(itemView);
		Data.registerListener(this);
		image = itemView.findViewById(R.id.image);
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
		Location loc = Data.locationForID.get(event.locationID);
		location.setText((loc != null ? loc.room + ", ": "") + (loc != null ? loc.building : ""));
		month.setText(event.startTime.toString("MMM"));
		day.setText(event.startTime.toString("d"));
		startTime.setText(event.startTime.toString("h:mm a"));
		numGoing.setText(Integer.toString(event.numAttendees));
		Internet.getImageForEvent(this.event, this.image);
	}


	@Override
	public void onClick(View view)
	{
		DetailsActivity.startWithEvent(Data.getEventFromID(event.id), itemView.getContext());
		for(Integer id : event.tagIDs){
			TagUtil.addTagToList(id);
		}
	}

	@Override
	public void eventUpdate(List<Event> e) {
		Internet.getImageForEvent(this.event, this.image);
	}

	@Override
	public void orgUpdate(List<Organization> o) {

	}

	@Override
	public void tagUpdate(List<String> t) {

	}
}
