package com.dti.cornell.events;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dti.cornell.events.models.Organization;

public class OrganizationActivity extends AppCompatActivity
{
	public static final String ORGANIZATION_KEY = "organization";
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
	}
}
