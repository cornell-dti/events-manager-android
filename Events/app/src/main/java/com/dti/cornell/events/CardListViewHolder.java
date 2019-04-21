package com.dti.cornell.events;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dti.cornell.events.models.CardList;
import com.dti.cornell.events.utils.RecyclerUtil;

class CardListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
	private final TextView section;
	private final TextView seeMore;
	private final EventCardAdapter adapter;
	private final FloatingActionButton backButton;

	public CardListViewHolder(View itemView)
	{
		super(itemView);
		section = itemView.findViewById(R.id.section);
		seeMore = itemView.findViewById(R.id.seeMore);

		final LayoutInflater factory = LayoutInflater.from(itemView.getContext());
		final View toolbarView = factory.inflate(R.layout.toolbar, null);
		backButton = (FloatingActionButton) toolbarView.findViewById(R.id.back2);
		backButton.setAlpha(1f);
//		backButton = itemView.findViewById(R.id.back2);
		seeMore.setOnClickListener(this);

		RecyclerView recyclerView = itemView.findViewById(R.id.recyclerView);
		RecyclerUtil.addHorizontalSpacing(recyclerView);
		adapter = new EventCardAdapter(itemView.getContext());
		recyclerView.setAdapter(adapter);
	}

	public void configure(CardList cardList)
	{
		if(cardList.sectionText == ""){
			section.setText(cardList.section);
		} else{
			section.setText(cardList.sectionText);
		}
		seeMore.setVisibility(cardList.showSeeMore ? View.VISIBLE : View.GONE);
		adapter.setData(cardList.events);
	}

	@Override
	public void onClick(View view)
	{
		Log.d("SEE MORE CLICKED", "SEE MORE CLICKED");
		backButton.setVisibility(View.INVISIBLE);
//		backButton.setAlpha(1f);
		MainActivity activity = (MainActivity) itemView.getContext();
		activity.transitionToFragment(new EventListFragment());
	}
}
