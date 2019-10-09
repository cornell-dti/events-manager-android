package com.dti.cornell.events;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class SubheaderViewHolder extends RecyclerView.ViewHolder
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
