package com.dti.cornell.events;

/**
 * Created by jboss925 on 9/29/18.
 */

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dti.cornell.events.models.Organization;
import com.dti.cornell.events.utils.EventBusUtils;
import com.google.common.eventbus.Subscribe;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.models.Organization;
import com.dti.cornell.events.utils.EventBusUtils;
import com.google.common.eventbus.Subscribe;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrganizationSearchAdapter extends RecyclerView.Adapter<OrganizationSearchViewHolder>
{
    private final LayoutInflater inflater;
    private List<Organization> organizations;
    private final Set<Organization> selected;

    public OrganizationSearchAdapter(Context context, List<Organization> organizations)
    {
        this.organizations = organizations;
        selected = new HashSet<>(organizations.size());
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
    public OrganizationSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new OrganizationSearchViewHolder(inflater.inflate(R.layout.item_organiztion_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrganizationSearchViewHolder holder, int position)
    {
        Organization org = organizations.get(position);
        holder.configure(org, position);
    }

    @Override
    public int getItemCount()
    {
        return organizations.size();
    }

    @Subscribe
    public void onOrganizationSelected(EventBusUtils.OrganizationSelected organizationSelected)
    {
        if (selected.contains(organizationSelected.organization))
            selected.remove(organizationSelected.organization);
        else
            selected.add(organizationSelected.organization);
        notifyItemChanged(organizationSelected.position);
    }

    public void updateList(List<Organization> orgs)
    {
        organizations = orgs;
        notifyDataSetChanged();
    }
}

