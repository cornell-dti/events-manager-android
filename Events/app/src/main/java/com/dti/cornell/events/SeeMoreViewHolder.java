package com.dti.cornell.events;

import android.view.View;

import com.dti.cornell.events.utils.Data;

import androidx.recyclerview.widget.RecyclerView;

class SeeMoreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
	public SeeMoreViewHolder(View itemView)
	{
		super(itemView);
		itemView.findViewById(R.id.seeAll).setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		MainActivity activity = (MainActivity) itemView.getContext();
		activity.transitionToFragment(new EventListFragment(Data.events()));
		activity.showBackButton();
		activity.setToolbarText(R.string.page_all_events);
		activity.expandToolBar();
	}
}
