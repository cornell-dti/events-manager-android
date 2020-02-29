package com.dti.cornell.events;

import android.graphics.drawable.Drawable;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dti.cornell.events.models.Organization;
import com.dti.cornell.events.utils.EventBusUtils;
import com.dti.cornell.events.utils.Internet;

public class OrganizationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
	private final TextView name;
	private ImageView image;
	private final Drawable background;
	private Organization organization;
	private int position;
	private boolean selectable;

	public OrganizationViewHolder(View itemView)
	{
		super(itemView);
		image = itemView.findViewById(R.id.image);
		name = itemView.findViewById(R.id.name);
		background = itemView.getBackground();
		itemView.setOnClickListener(this);
	}

	public void configure(Organization organization, int position, boolean selectable)
	{
		this.organization = organization;
		this.position = position;
		this.selectable = selectable;
		name.setText(organization.name);
		Internet.getImageForOrganization(organization, image);
	}

	public void setSelected(boolean selected)
	{
		if (selected)
			itemView.setBackgroundResource(R.drawable.bg_round_button_lg_gray_highlight);
		else
			itemView.setBackground(background);
	}

	@Override
	public void onClick(View view)
	{
		EventBusUtils.SINGLETON.post(new EventBusUtils.OrganizationSelected(organization, position));
		if(!selectable){
			OrganizationActivity.startWithOrganization(organization, view.getContext());
		}
	}
}
