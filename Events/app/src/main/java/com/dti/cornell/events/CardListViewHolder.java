package com.dti.cornell.events;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dti.cornell.events.models.CardList;
import com.dti.cornell.events.utils.SpacingItemDecoration;

public class CardListViewHolder extends AbstractCardListViewHolder implements View.OnClickListener
{
	private final TextView section;
	private final TextView seeMore;
	private final EventCardAdapter adapter;

	public CardListViewHolder(View itemView)
	{
		super(itemView);
		section = itemView.findViewById(R.id.section);
		seeMore = itemView.findViewById(R.id.seeMore);
		seeMore.setOnClickListener(this);

		RecyclerView recyclerView = itemView.findViewById(R.id.recyclerView);
		int cardMargin = itemView.getResources().getDimensionPixelSize(R.dimen.spacing_xxl);
		recyclerView.addItemDecoration(new SpacingItemDecoration(cardMargin, 0));
		adapter = new EventCardAdapter(itemView.getContext());
		recyclerView.setAdapter(adapter);
	}

	public void configure(CardList cardList)
	{
		section.setText(cardList.section);
		seeMore.setVisibility(cardList.showSeeMore ? View.VISIBLE : View.GONE);
		adapter.setData(cardList.events);
	}

	@Override
	public void onClick(View view)
	{
		MainActivity activity = (MainActivity) itemView.getContext();
		activity.transitionToFragment(new EventListFragment());
	}
}
