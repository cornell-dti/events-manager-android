package com.dti.cornell.events;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.dti.cornell.events.models.Organization;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.OrganizationUtil;
import com.dti.cornell.events.utils.RecyclerUtil;

public class OrganizationActivity extends AppCompatActivity implements View.OnClickListener
{
	private static final String ORGANIZATION_KEY = "organization";
	private TextView website;
	private TextView email;
	private TextView bio;
	private RecyclerView eventsRecycler;
	private RecyclerView tagRecycler;
	private Organization organization;
	private boolean userIsFollowing;
	private TextView followingButton;
	private TextView title;

	public static void startWithOrganization(Organization organization, Context context)
	{
		Intent intent = new Intent(context, OrganizationActivity.class);
		intent.putExtra(ORGANIZATION_KEY, organization.toString());
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTheme(R.style.AppTheme_NoActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_organization);

		organization = Organization.fromString(getIntent().getExtras().getString(ORGANIZATION_KEY));
		findViews();
		configure(organization);
		followingButton = findViewById(R.id.follow);
		userIsFollowing = OrganizationUtil.userIsFollowing(organization.id);
		if(userIsFollowing){
			setFollowButtonState();
		}
	}

	@Override
	public void onStart(){
		super.onStart();
		setFollowButtonState();
	}

	private void setFollowButtonState(){
		if(userIsFollowing){
			followingButton.setTextAppearance(R.style.mainButtonSelected);
			followingButton.setBackgroundResource(R.drawable.bg_round_button_red);
			followingButton.setText(R.string.button_following);
		} else {
			followingButton.setTextAppearance(R.style.mainButton);
			followingButton.setBackgroundResource(R.drawable.bg_round_button_white);
			followingButton.setText(R.string.button_follow);
		}
	}



	private void findViews()
	{
		findViewById(R.id.follow).setOnClickListener(this);
		title = findViewById(R.id.toolbarTitle);

		website = findViewById(R.id.website);
		email = findViewById(R.id.email);
		bio = findViewById(R.id.bio);
		FloatingActionButton backButton = findViewById(R.id.backButton);
		backButton.setOnClickListener(this);

		eventsRecycler = findViewById(R.id.eventsRecycler);
		RecyclerUtil.addHorizontalSpacing(eventsRecycler);

		tagRecycler = findViewById(R.id.tagRecycler);
		RecyclerUtil.addHorizontalSpacing(tagRecycler);
	}

	private void configure(Organization organization)
	{
		website.setText(organization.website);
		email.setText(organization.email);
		bio.setText(organization.description);
		title.setText(organization.name);
		Log.d("ORGANIZATION", title.getText().toString());
		EventCardAdapter adapter = new EventCardAdapter(this);
		eventsRecycler.setAdapter(adapter);
		adapter.setData(Data.events());

		tagRecycler.setAdapter(new TagAdapter(this, organization.tagIDs, false));
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
			case R.id.backButton:
				onBackPressed();
				break;
			case R.id.follow:
				if(OrganizationUtil.userIsFollowing(organization.id)){
					OrganizationUtil.unfollowOrganization(organization.id);
					userIsFollowing = false;
					setFollowButtonState();
				} else {
					OrganizationUtil.followOrganization(organization.id);
					userIsFollowing = true;
					setFollowButtonState();
				}
				break;
		}
	}
}
