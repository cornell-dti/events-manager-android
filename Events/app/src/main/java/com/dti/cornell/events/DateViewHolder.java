package com.dti.cornell.events;

import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.EventBusUtils;

import org.joda.time.DateTime;

public class DateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
	private static final String TAG = DateViewHolder.class.getSimpleName();
	private final TextView weekday;
	private final TextView date;
	private DateTime dateTime;

	public DateViewHolder(@NonNull View itemView)
	{
		super(itemView);
		weekday = itemView.findViewById(R.id.weekday);
		date = itemView.findViewById(R.id.date);
		date.setOnClickListener(this);
	}

	public void configure(DateTime dateTime)
	{
		this.dateTime = dateTime;
		weekday.setText(dateTime.toString("EEE"));
		date.setText(dateTime.toString("d"));

		if (Data.equalsSelectedDate(dateTime))
			setSelected();
		else
			setUnselected();
	}

	@Override
	public void onClick(View view)
	{
		setSelected();
		Data.selectedDate = this.dateTime;
		EventBusUtils.SINGLETON.post(new EventBusUtils.DateChanged(TAG));
	}

	private void setUnselected()
	{
		date.setSelected(false);
		date.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.gray));
	}

	private void setSelected()
	{
		date.setSelected(true);
		date.setTextColor(Color.WHITE);
	}
}
