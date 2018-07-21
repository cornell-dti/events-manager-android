package com.dti.cornell.events;

import android.view.View;

public class SeeMoreViewHolder extends AbstractCardListViewHolder implements View.OnClickListener
{
	public SeeMoreViewHolder(View itemView)
	{
		super(itemView);
		itemView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
//		((MainActivity) itemView.getContext()).transitionToFragment(new DiscoverListFragment());
	}
}
