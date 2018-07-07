package com.dti.cornell.events;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class TagViewHolder extends RecyclerView.ViewHolder
{
	public TagViewHolder(View itemView)
	{
		super(itemView);
	}

	public void configure(Tag tag)
	{
		((TextView) itemView).setText(tag.name);
	}
}
