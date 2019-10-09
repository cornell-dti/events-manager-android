package com.dti.cornell.events;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dti.cornell.events.models.Organization;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.EventBusUtils;

/**
 * Created by jboss925 on 9/29/18.
 */

public class TagSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final Context context;
    private final TextView tagTextView;
    private  String tag;
    private int tagID;
    private final Drawable background;
    private int position;

    public TagSearchViewHolder(View itemView) {
        super(itemView);
        tagTextView = itemView.findViewById(R.id.tagTextView);
        background = itemView.getBackground();
        itemView.setOnClickListener(this);
        context = itemView.getContext();
    }

    public void configure(int tagID, int position) {
        this.position = position;
        this.tag = Data.tagForID.get(tagID);
        this.tagID = tagID;
        tagTextView.setText(tag);
    }

    @Override
    public void onClick(View view) {
        EventBusUtils.SINGLETON.post(new EventBusUtils.TagPickedSearch(tag));
        TagActivity.startWithTag(context, tagID);
    }
}
