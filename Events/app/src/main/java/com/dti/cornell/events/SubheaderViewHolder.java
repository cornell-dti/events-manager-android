package com.dti.cornell.events;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class SubheaderViewHolder extends RecyclerView.ViewHolder
{
	public SubheaderViewHolder(@NonNull View itemView)
	{
		super(itemView);
	}

	public void configure(String text)
	{
		((TextView) itemView).setText(text);
	}
}
