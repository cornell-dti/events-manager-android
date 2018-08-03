package com.dti.cornell.events;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dti.cornell.events.models.Organization;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.RecyclerUtil;

public class OrganizationActivity extends AppCompatActivity implements View.OnClickListener
{
	public static final String ORGANIZATION_KEY = "organization";
	private TextView website;
	private TextView email;
	private TextView bio;
	private RecyclerView eventsRecycler;
	private RecyclerView tagRecycler;
	private Organization organization;

	public static void startWithOrganization(Organization organization, Context context)
	{
		Intent intent = new Intent(context, OrganizationActivity.class);
		intent.putExtra(ORGANIZATION_KEY, organization.toString());
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_organization);

		organization = Organization.fromString(getIntent().getExtras().getString(ORGANIZATION_KEY));
		findViews();
		configure(organization);
	}

	private void findViews()
	{
		findViewById(R.id.follow).setOnClickListener(this);
		website = findViewById(R.id.website);
		email = findViewById(R.id.email);
		bio = findViewById(R.id.bio);

		eventsRecycler = findViewById(R.id.eventsRecycler);
		RecyclerUtil.configureCards(eventsRecycler);

		tagRecycler = findViewById(R.id.tagRecycler);
		RecyclerUtil.configureTags(tagRecycler);
	}

	private void configure(Organization organization)
	{
		website.setText(organization.website);
		email.setText(organization.email);
		bio.setText(organization.description);

		EventCardAdapter adapter = new EventCardAdapter(this);
		eventsRecycler.setAdapter(adapter);
		adapter.setData(Data.events());

		tagRecycler.setAdapter(new TagAdapter(this, organization.tagIDs));
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
			case R.id.follow:
				//TODO follow
				break;
		}
	}
}
