package com.dti.cornell.events;

/**
 * Created by jboss925 on 9/29/18.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dti.cornell.events.models.Organization;
import com.dti.cornell.events.utils.EventBusUtils;
import com.dti.cornell.events.utils.EventBusUtils;

public class OrganizationSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private final Context context;
    private final TextView name;
    private final Drawable background;
    private Organization organization;
    private int position;

    public OrganizationSearchViewHolder(View itemView)
    {
        super(itemView);
        ImageView image = itemView.findViewById(R.id.orgImageView);
        name = itemView.findViewById(R.id.orgNameView);
        background = itemView.getBackground();
        itemView.setOnClickListener(this);
        context = itemView.getContext();
    }

    public void configure(Organization organization, int position)
    {
        //todo get image
        this.organization = organization;
        this.position = position;
        name.setText(organization.name);
    }

    public void setSelected(boolean selected)
    {
        if (selected)
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.gray));
        else
            itemView.setBackground(background);
    }

    @Override
    public void onClick(View view)
    {
        EventBusUtils.SINGLETON.post(new EventBusUtils.OrganizationPickedSearch(organization));
        OrganizationActivity.startWithOrganization(organization, context);
    }
}

