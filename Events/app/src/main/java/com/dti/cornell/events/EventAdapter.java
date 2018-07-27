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
		data.add(new EventItem(lastDate));

		//prepend a subheader in front of each event that has a different date
		DateTimeComparator comparator = DateTimeComparator.getDateOnlyInstance();
		for (Event event : events)
		{
			if (comparator.compare(lastDate, event.startTime) != 0)
			{
				lastDate = event.startTime;
				data.add(new EventItem(lastDate));
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
			((SubheaderViewHolder) holder).configure(data.get(position).subheader.toString(DATE_FORMAT));
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
		else
			return data.get(position).subheader;
	}

	/**
	 * Finds the position of the subheader for the given date, or where that subheader would've been
	 * inserted. The position will be a valid index.
	 *
	 * Approximately O(logn) for binary search, worst-case O(n) walk upward if all events belong to
	 * the same day.
	 *
	 * @param dateTime Date
	 * @return Position of item matching date
	 */
	public int getPositionForDate(DateTime dateTime)
	{
		//binary search returns correct position OR -(pos+1) where item would've been inserted
		int position = Collections.binarySearch(data, new EventItem(dateTime));
		if (position < 0)
			return Math.max(-(position + 1), getItemCount()-1); //don't exceed data length

		//find subheader of events of this date by walking upward
		while (getItemViewType(position) != SUBHEADER)
			position -= 1;
		return position;
	}
}
