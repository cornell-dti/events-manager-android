package com.dti.cornell.events;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class TagViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
	public TagViewHolder(View itemView)
	{
		super(itemView);
		itemView.setOnClickListener(this);
	}

	public void configure(String tag)
	{
		((TextView) itemView).setText(tag);
	}

	@Override
	public void onClick(View v)
	{
		//TODO go to tags list
	}
}
