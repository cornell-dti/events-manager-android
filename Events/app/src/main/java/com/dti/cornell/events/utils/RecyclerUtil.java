package com.dti.cornell.events.utils;

import android.support.v7.widget.RecyclerView;

import com.dti.cornell.events.R;

public final class RecyclerUtil
{
	private RecyclerUtil() {}

	public static void configureTags(RecyclerView recycler)
	{
		int spacing = recycler.getContext().getResources().getDimensionPixelSize(R.dimen.spacing_l);
		recycler.addItemDecoration(new SpacingItemDecoration(spacing, 0));
	}

	public static void configureCardLists(RecyclerView recycler)
	{
		int cardMargin = recycler.getContext().getResources().getDimensionPixelSize(R.dimen.spacing_xxl);
		recycler.addItemDecoration(new SpacingItemDecoration(0, cardMargin));
	}

	public static void configureCards(RecyclerView recycler)
	{
		int cardMargin = recycler.getContext().getResources().getDimensionPixelSize(R.dimen.spacing_xxl);
		recycler.addItemDecoration(new SpacingItemDecoration(cardMargin, 0));
	}

	public static void configureEvents(RecyclerView recycler)
	{
		int margin = recycler.getContext().getResources().getDimensionPixelSize(R.dimen.spacing_xxl);
		recycler.addItemDecoration(new com.dti.cornell.events.utils.DividerItemDecoration(margin));
	}
}
