package com.dti.cornell.events;

import android.support.v7.app.AppCompatActivity;
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
		AppCompatActivity activity = (AppCompatActivity) itemView.getContext();
		RecyclerFragment fragment = (RecyclerFragment) activity.getSupportFragmentManager()
				.findFragmentById(R.id.fragmentContainer);
		fragment.setType(RecyclerFragment.Type.DiscoverList);
		fragment.refreshViews();
	}
}
