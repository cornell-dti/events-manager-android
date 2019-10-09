package com.dti.cornell.events.utils;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.dti.cornell.events.R;

/**
 * An extension of the given divider to include margins
 */
class DividerItemDecoration extends RecyclerView.ItemDecoration
{
	private final int margins;
	@Nullable
	private Drawable divider = null;
	private int orientation = -1;

	public DividerItemDecoration(int margins)
	{
		this.margins = margins;
	}

	@Override
	public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
	{
		super.getItemOffsets(outRect, view, parent, state);

		//no divider for the 1st list item
		if (parent.getChildAdapterPosition(view) == 0)
			return;

		//otherwise make space for the divider
		Drawable divider = getDivider(parent);
		int orientation = getOrientation(parent);
		if (orientation == LinearLayoutManager.HORIZONTAL)
			outRect.left = divider.getIntrinsicWidth();
		else
			outRect.top = divider.getIntrinsicHeight();
	}

	@Override
	public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
	{
		Drawable divider = getDivider(parent);
		int orientation = getOrientation(parent);
		int numDividers = parent.getChildCount();

		//draw dividers perpendicular to the RecyclerView's orientation
		if (orientation == LinearLayoutManager.HORIZONTAL)
		{
			int width = divider.getIntrinsicWidth();

			int top = parent.getPaddingTop() + margins;
			int bottom = parent.getHeight() - parent.getPaddingBottom() - margins;

			for (int i = 0; i < numDividers; i++)
			{
				View child = parent.getChildAt(i);
				RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();

				//draw AFTER the child (to its right)
				int left = child.getRight() + layoutParams.rightMargin;
				int right = left + width;

				divider.setBounds(left, top, right, bottom);
				divider.draw(c);
			}
		}
		else
		{
			int height = divider.getIntrinsicHeight();

			int left = parent.getPaddingLeft() + margins;
			int right = parent.getWidth() - parent.getPaddingRight() - margins;

			for (int i = 0; i < numDividers; i++)
			{
				View child = parent.getChildAt(i);
				RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();

				//draw after the child (underneath it)
				int top = child.getBottom() + layoutParams.bottomMargin;
				int bottom = top + height;

				divider.setBounds(left, top, right, bottom);
				divider.draw(c);
			}
		}
	}

	private Drawable getDivider(View view)
	{
		if (divider == null)
			divider = view.getContext().getDrawable(R.drawable.divider);
		return divider;
	}

	private int getOrientation(RecyclerView recyclerView)
	{
		if (orientation == -1)
			orientation = ((LinearLayoutManager) recyclerView.getLayoutManager()).getOrientation();
		return orientation;
	}
}
