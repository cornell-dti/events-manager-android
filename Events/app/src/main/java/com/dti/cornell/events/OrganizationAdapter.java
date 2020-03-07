package com.dti.cornell.events;

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

class OrganizationAdapter extends RecyclerView.Adapter<OrganizationViewHolder>
{
	private final LayoutInflater inflater;
	private List<Organization> organizations;
	private final Set<Organization> selected;
	private final boolean selectable;
	private boolean onboardingOrg;

	public OrganizationAdapter(Context context, List<Organization> organizations, boolean selectable, boolean onboardingOrg)
	{
		this.organizations = organizations;
		this.selectable = selectable;
		selected = new HashSet<>(organizations.size());
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		EventBusUtils.SINGLETON.register(this);
		this.onboardingOrg = onboardingOrg;
	}

	@Override
	public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView)
	{
		super.onDetachedFromRecyclerView(recyclerView);
		EventBusUtils.SINGLETON.unregister(this);
	}

	@NonNull
	@Override
	public OrganizationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		if (onboardingOrg) {
			return new OrganizationViewHolder(inflater.inflate(R.layout.item_onboarding_organization, parent, false));
		} else {
			return new OrganizationViewHolder(inflater.inflate(R.layout.item_organization, parent, false));
		}
	}

	@Override
	public void onBindViewHolder(@NonNull OrganizationViewHolder holder, int position)
	{
		Organization org = organizations.get(position);
		holder.configure(org, position, selectable);
		if (selectable)
			holder.setSelected(selected.contains(org));
	}

	@Override
	public int getItemCount()
	{
		return organizations.size();
	}

	public Set<Organization> getSelectedOrganizations() {
	    return selected;
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
