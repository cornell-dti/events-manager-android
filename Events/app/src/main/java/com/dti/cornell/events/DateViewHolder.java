package com.dti.cornell.events;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.joda.time.DateTime;

public class DateViewHolder extends RecyclerView.ViewHolder
{
	private final TextView weekday;
	private final TextView date;

	public DateViewHolder(@NonNull View itemView)
	{
		super(itemView);
		weekday = itemView.findViewById(R.id.weekday);
		date = itemView.findViewById(R.id.date);
	}

	public void configure(DateTime dateTime)
	{
		weekday.setText(dateTime.toString("EEE"));
		date.setText(dateTime.toString("d"));
	}
}
