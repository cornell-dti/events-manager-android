package com.dti.cornell.events.utils;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Add margin between {@link RecyclerView} items with this.
 */
public class SpacingItemDecoration extends RecyclerView.ItemDecoration
{
	private final int horiz;
	private final int vert;

	public SpacingItemDecoration(int horiz, int vert)
	{
		this.horiz = horiz;
		this.vert = vert;
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
	{
		outRect.left = horiz;
		outRect.top = vert;

		//extra spacing for last item
		if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1)
		{
			outRect.right = horiz;
			outRect.bottom = vert;
		}
	}
}
