package com.dti.cornell.events;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dti.cornell.events.models.Organization;
import com.dti.cornell.events.utils.EventBusUtils;
import com.google.common.eventbus.Subscribe;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jboss925 on 9/29/18.
 */

public class TagSearchAdapter extends RecyclerView.Adapter<TagSearchViewHolder>
{
    private final LayoutInflater inflater;
    private List<Integer> tagIDs;

    public TagSearchAdapter(Context context, List<Integer> tagIDs)
    {
        this.tagIDs = tagIDs;
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
    public TagSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new TagSearchViewHolder(inflater.inflate(R.layout.item_tag_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TagSearchViewHolder holder, int position)
    {
        int tagID = tagIDs.get(position);
        holder.configure(tagID, position);
    }

    @Override
    public int getItemCount()
    {
        return tagIDs.size();
    }

    public void updateList(List<Integer> tags)
    {
        tagIDs = tags;
        notifyDataSetChanged();
    }
}
