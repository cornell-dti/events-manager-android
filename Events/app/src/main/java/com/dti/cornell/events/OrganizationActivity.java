package com.dti.cornell.events;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.models.Organization;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.Internet;
import com.dti.cornell.events.utils.OrganizationUtil;
import com.dti.cornell.events.utils.RecyclerUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.stream.Collectors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.recyclerview.widget.RecyclerView;

public class OrganizationActivity extends AppCompatActivity implements View.OnClickListener, Data.DataUpdateListener
{
	private static final String ORGANIZATION_KEY = "organization";
	private TextView website;
	private TextView email;
	private TextView bio;
	private ScrollView scrollView;
	private RecyclerView eventsRecycler;
	private RecyclerView tagRecycler;
	private Organization organization;
	private boolean userIsFollowing;
	private TextView followingButton;
	private TextView title;
	private ImageView toolbarImage;

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
		Data.registerListener(this);

		organization = Organization.fromString(getIntent().getExtras().getString(ORGANIZATION_KEY));
		findViews();
		configure(organization);
		followingButton = findViewById(R.id.follow);
		userIsFollowing = OrganizationUtil.userIsFollowing(organization.id);
		if(userIsFollowing){
			setFollowButtonState();
		}
		scrollView.smoothScrollTo(0,0);
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

		toolbarImage = findViewById(R.id.toolbarImage);
		title = findViewById(R.id.toolbarTitle);

		website = findViewById(R.id.website);
		email = findViewById(R.id.email);
		bio = findViewById(R.id.bio);
		FloatingActionButton backButton = findViewById(R.id.backButton);
		backButton.setOnClickListener(this);
		scrollView = findViewById(R.id.scrollView);
		eventsRecycler = findViewById(R.id.eventsRecycler);
		RecyclerUtil.addHorizontalSpacing(eventsRecycler);

		tagRecycler = findViewById(R.id.tagRecycler);
		RecyclerUtil.addHorizontalSpacing(tagRecycler);
	}

	private void configure(Organization organization)
	{
		Internet.getImageForOrganization(organization, toolbarImage);
		website.setText(organization.website);
		email.setText(organization.email);
		bio.setText(organization.description);
		title.setText(organization.name);
		Log.d("ORGANIZATION", title.getText().toString());
		EventCardAdapter adapter = new EventCardAdapter(this);
		eventsRecycler.setAdapter(adapter);
		adapter.setData(Data.events().stream().filter((val) -> {
			return val.organizerID == this.organization.id;
		}).collect(Collectors.toList()));

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
			case R.id.share:
				ShareCompat.IntentBuilder.from(this)
						.setType("text/plain")
						.setChooserTitle("Choose how to share this organization")
						.setText("https://www.cuevents.org/org/" + this.organization.id)
						.startChooser();
				break;
		}
	}

	@Override
	public void eventUpdate(List<Event> e) {
		EventCardAdapter adapter = new EventCardAdapter(this);
		eventsRecycler.setAdapter(adapter);
		adapter.setData(Data.events().stream().filter((val) -> {
			return val.organizerID == this.organization.id;
		}).collect(Collectors.toList()));

		tagRecycler.setAdapter(new TagAdapter(this, organization.tagIDs, false));
	}

	@Override
	public void orgUpdate(List<Organization> o) {
		int orgID = organization.id;
		organization = Data.organizationForID.get(orgID);
		email.setText(organization.email);
	}

	@Override
	public void tagUpdate(List<String> t) {

	}
}
