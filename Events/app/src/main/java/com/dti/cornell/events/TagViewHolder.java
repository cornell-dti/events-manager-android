package com.dti.cornell.events;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.EventBusUtils;

public class TagViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
	private final Context context;
	private final Drawable background;
	private final Integer textColor;
	private int id;
	private int position;
	private boolean selected;
	private TextView text;

	public TagViewHolder(View itemView)
	{
		super(itemView);
		background = itemView.getBackground();
		itemView.setOnClickListener(this);
		selected = false;
		context = itemView.getContext();
		text = itemView.findViewById(R.id.tag_name_onboarding);
		textColor = text.getCurrentTextColor();
	}

	public void configure(int id, int position)
	{
		this.id = id;
		this.position = position;
		((TextView) itemView).setText(Data.tagForID.get(id));
	}

	public void setSelected(boolean selected)
	{
		if (selected) {
			itemView.setBackgroundResource(R.drawable.bg_round_button_lg_red_highlight);
			text.setTextColor(Color.WHITE);

		} else {
			itemView.setBackground(background);
			text.setTextColor(textColor);
		}
	}

	@Override
	public void onClick(View v)
	{
		//TODO go to tags list
		//TODO Check to see if the Activity name matches onboarding's activity name and don't open if so
		//TODO perhaps use the fact that you know the activity is onboarding to save the clicked
		//TODO tags in onboarding.
		Log.i("tag viewholder", ((Activity)v.getContext()).getLocalClassName());
		if(((Activity)v.getContext()).getLocalClassName().equalsIgnoreCase("OnboardingActivity")){
			selected = !selected;
			setSelected(selected);
			return;
		}
		TagActivity.startWithTag(context, this.id);
		EventBusUtils.SINGLETON.post(new EventBusUtils.TagSelected(id, position));
	}
}
