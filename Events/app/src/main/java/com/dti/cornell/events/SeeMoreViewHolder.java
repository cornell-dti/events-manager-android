package com.dti.cornell.events;

import android.support.v7.widget.RecyclerView;
import android.view.View;

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
		activity.transitionToFragment(new EventListFragment());
	}
}
