package com.dti.cornell.events;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.models.EventItem;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jaggerbrulato on 3/21/18.
 */

public class EventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	private final LayoutInflater inflater;
	private final List<EventItem> data;

	private static final String DATE_FORMAT = "EEEE, MMMM d";

	private static final int EVENT = 0;
	private static final int SUBHEADER = 1;

	public EventAdapter(Context context, List<Event> events)
	{
		data = addSubheadersToEvents(events);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	private List<EventItem> addSubheadersToEvents(List<Event> events)
	{
		if (events.isEmpty())
			return Collections.emptyList();

		//append 1st date subheader
		List<EventItem> data = new ArrayList<>(events.size() * 2);
		DateTime lastDate = events.get(0).startTime;
		data.add(new EventItem(lastDate.toString(DATE_FORMAT)));

		//prepend a subheader in front of each event that has a different date
		DateTimeComparator comparator = DateTimeComparator.getDateOnlyInstance();
		for (Event event : events)
		{
			if (comparator.compare(lastDate, event.startTime) != 0)
			{
				lastDate = event.startTime;
				data.add(new EventItem(lastDate.toString(DATE_FORMAT)));
			}
			data.add(new EventItem(event));
		}

		return data;
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		if (viewType == EVENT)
			return new EventViewHolder(inflater.inflate(R.layout.item_event, parent, false));
		else
			return new SubheaderViewHolder(inflater.inflate(R.layout.item_subheader, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
	{
		if (getItemViewType(position) == EVENT)
			((EventViewHolder) holder).configure(data.get(position).event);
		else
			((SubheaderViewHolder) holder).configure(data.get(position).subheader);
	}

	@Override
	public int getItemCount()
	{
		return data.size();
	}

	@Override
	public int getItemViewType(int position)
	{
		if (data.get(position).event != null)
			return EVENT;
		else
			return SUBHEADER;
	}

	public DateTime getDateAtPosition(int position)
	{
		if (getItemViewType(position) == EVENT)
			return data.get(position).event.startTime;
		else //if there's a subheader at this position, the next position must be a date
			return data.get(position + 1).event.startTime;
	}
}
