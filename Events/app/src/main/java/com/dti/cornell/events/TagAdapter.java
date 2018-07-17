package com.dti.cornell.events;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dti.cornell.events.utils.Data;
import com.google.common.collect.ImmutableList;

public class TagAdapter extends RecyclerView.Adapter<TagViewHolder>
{
	private LayoutInflater inflater;
	private final ImmutableList<Integer> tags;

	public TagAdapter(Context context, ImmutableList<Integer> tags)
	{
		this.tags = tags;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@NonNull
	@Override
	public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		return new TagViewHolder(inflater.inflate(R.layout.item_tag, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull TagViewHolder holder, int position)
	{
		holder.configure(Data.tagForID.get(tags.get(position)));
	}

	@Override
	public int getItemCount()
	{
		return tags.size();
	}
}
