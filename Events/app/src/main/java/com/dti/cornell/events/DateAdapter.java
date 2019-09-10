package com.dti.cornell.events;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dti.cornell.events.utils.Data;

class DateAdapter extends RecyclerView.Adapter<DateViewHolder>
{
	private final LayoutInflater inflater;

	public DateAdapter(Context context)
	{
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@NonNull
	@Override
	public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type)
	{
		return new DateViewHolder(inflater.inflate(R.layout.item_date, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull DateViewHolder dateViewHolder, int position)
	{
		dateViewHolder.configure(Data.DATES.get(position));
	}

	@Override
	public int getItemCount()
	{
		return Data.DATES.size();
	}
}
