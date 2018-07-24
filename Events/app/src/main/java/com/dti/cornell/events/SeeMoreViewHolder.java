package com.dti.cornell.events;

import android.view.View;

public class SeeMoreViewHolder extends AbstractCardListViewHolder implements View.OnClickListener
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
