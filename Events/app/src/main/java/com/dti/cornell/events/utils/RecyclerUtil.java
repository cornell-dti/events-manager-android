package com.dti.cornell.events.utils;

import com.dti.cornell.events.R;

import androidx.recyclerview.widget.RecyclerView;

public final class RecyclerUtil
{
	private RecyclerUtil() {}

	public static void addDivider(RecyclerView recycler)
	{
		int margin = recycler.getContext().getResources().getDimensionPixelSize(R.dimen.spacing_xxl);
		recycler.addItemDecoration(new com.dti.cornell.events.utils.DividerItemDecoration(margin));
	}

	public static void addHorizontalSpacing(RecyclerView recycler)
	{
		int margin = recycler.getContext().getResources().getDimensionPixelSize(R.dimen.spacing_xxl);
		recycler.addItemDecoration(new SpacingItemDecoration(margin, 0));
	}

	public static void addTagHorizontalSpacing(RecyclerView recycler)
	{
		int margin = recycler.getContext().getResources().getDimensionPixelSize(R.dimen.spacing_s);
		recycler.addItemDecoration(new SpacingItemDecoration(margin, 0));
	}

	public static void addVerticalSpacing(RecyclerView recycler)
	{
		int margin = recycler.getContext().getResources().getDimensionPixelSize(R.dimen.spacing_xxl);
		recycler.addItemDecoration(new SpacingItemDecoration(0, margin));

	}
}
