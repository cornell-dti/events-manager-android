package com.dti.cornell.events;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dti.cornell.events.utils.EventBusUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;

import java.util.HashSet;
import java.util.Set;

class TagAdapter extends RecyclerView.Adapter<TagViewHolder>
{
	private enum Style {
		WHITE,
		RED
	}

	private final LayoutInflater inflater;
	private final ImmutableList<Integer> tags;
	private final boolean selectable;
	public final Set<Integer> selected;
	private final boolean onboardingTag;

	public TagAdapter(Context context, ImmutableList<Integer> tags, boolean selectable, boolean onboardingTag)
	{
		this.tags = tags;
		this.selectable = selectable;
		this.onboardingTag = onboardingTag;
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
		if (onboardingTag) {
			return new TagViewHolder(inflater.inflate(R.layout.item_tag, parent, false));
		} else {
			return new TagViewHolder(inflater.inflate(R.layout.item_tag_onboarding, parent, false));
		}
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

	public Set<Integer> getSelectedTags() {
		return selected;
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
