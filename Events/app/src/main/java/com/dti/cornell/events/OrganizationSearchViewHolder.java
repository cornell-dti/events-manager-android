package com.dti.cornell.events;

/**
 * Created by jboss925 on 9/29/18.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dti.cornell.events.models.Organization;
import com.dti.cornell.events.utils.EventBusUtils;
import com.dti.cornell.events.utils.EventBusUtils;
import com.dti.cornell.events.utils.Internet;

public class OrganizationSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private final Context context;
    private final TextView name;
    private ImageView image;
    private final Drawable background;
    private Organization organization;
    private ProgressBar imageLoadingBar;
    private int position;

    public OrganizationSearchViewHolder(View itemView)
    {
        super(itemView);
        image = itemView.findViewById(R.id.orgImageView);
        name = itemView.findViewById(R.id.orgNameView);
        imageLoadingBar = itemView.findViewById(R.id.imageLoadingBar2);
        background = itemView.getBackground();
        itemView.setOnClickListener(this);
        context = itemView.getContext();
    }

    public void configure(Organization organization, int position)
    {
        this.organization = organization;
        this.position = position;
        name.setText(organization.name);
        Internet.getImageForOrganizationStopProgress(this.organization, image, imageLoadingBar);
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

