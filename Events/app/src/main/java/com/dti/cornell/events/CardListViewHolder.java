package com.dti.cornell.events;

import android.view.View;
import android.widget.TextView;

import com.dti.cornell.events.models.CardList;
import com.dti.cornell.events.utils.RecyclerUtil;

import androidx.recyclerview.widget.RecyclerView;

class CardListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
	private final TextView section;
	private final TextView seeMore;
	private final EventCardAdapter adapter;
//	private final FloatingActionButton backButton;

	public CardListViewHolder(View itemView)
	{
		super(itemView);
		section = itemView.findViewById(R.id.section);
		seeMore = itemView.findViewById(R.id.seeMore);
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
		MainActivity activity = (MainActivity) itemView.getContext();
		activity.showBackButton();
		activity.transitionToFragment(new EventListFragment());
	}
}
