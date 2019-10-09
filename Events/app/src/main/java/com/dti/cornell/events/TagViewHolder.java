package com.dti.cornell.events;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.EventBusUtils;

public class TagViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
	private final Context context;
	private final Drawable background;
	private int id;
	private int position;

	public TagViewHolder(View itemView)
	{
		super(itemView);
		background = itemView.getBackground();
		itemView.setOnClickListener(this);
		context = itemView.getContext();
	}

	public void configure(int id, int position)
	{
		this.id = id;
		this.position = position;
		((TextView) itemView).setText(Data.tagForID.get(id));
	}

	public void setSelected(boolean selected)
	{
		if (selected)
			itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.gray));
		else
			itemView.setBackground(background);
	}

	@Override
	public void onClick(View v)
	{
		//TODO go to tags list
		//TODO Check to see if the Activity name matches onboarding's activity name and don't open if so
		//TODO perhaps use the fact that you know the activity is onboarding to save the clicked
		//TODO tags in onboarding.
		if(((Activity)v.getContext()).getLocalClassName().equalsIgnoreCase("Onboarding")){
			return;
		}
		TagActivity.startWithTag(context, this.id);
		EventBusUtils.SINGLETON.post(new EventBusUtils.TagSelected(id, position));
	}
}
