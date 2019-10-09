package com.dti.cornell.events;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dti.cornell.events.utils.EventBusUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;

import java.util.HashSet;
import java.util.Set;

class TagAdapter extends RecyclerView.Adapter<TagViewHolder>
{
	private final LayoutInflater inflater;
	private final ImmutableList<Integer> tags;
	private final boolean selectable;
	private final Set<Integer> selected;

	public TagAdapter(Context context, ImmutableList<Integer> tags, boolean selectable)
	{
		this.tags = tags;
		this.selectable = selectable;
		selected = new HashSet<>(tags.size());
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		EventBusUtils.SINGLETON.register(this);
	}

	@Override
	public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView)
	{
		super.onDetachedFromRecyclerView(recyclerView);
		EventBusUtils.SINGLETON.unregister(this);
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
		holder.configure(tags.get(position), position);
		if (selectable)
			holder.setSelected(selected.contains(tags.get(position)));
	}

	@Override
	public int getItemCount()
	{
		return tags.size();
	}

	@Subscribe
	public void onTagSelected(EventBusUtils.TagSelected tagSelected)
	{
		if (selected.contains(tagSelected.tagID))
			selected.remove(tagSelected.tagID);
		else
			selected.add(tagSelected.tagID);
		notifyItemChanged(tagSelected.position);
	}
}
