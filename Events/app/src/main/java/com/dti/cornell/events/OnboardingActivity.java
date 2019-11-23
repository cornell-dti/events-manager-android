package com.dti.cornell.events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.Toast;

import com.dti.cornell.events.models.Organization;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.EventBusUtils;
import com.dti.cornell.events.utils.Internet;
import com.dti.cornell.events.utils.OrganizationUtil;
import com.dti.cornell.events.utils.RecyclerUtil;
import com.dti.cornell.events.utils.SettingsUtil;
import com.dti.cornell.events.utils.TagUtil;
import com.google.android.flexbox.AlignContent;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class OnboardingActivity extends AppCompatActivity
{
	private ViewPager pager;
	private static TagAdapter tagAdapter;
	private static OrganizationAdapter orgAdapter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		setTheme(R.style.AppTheme_NoActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_onboarding);

		pager = findViewById(R.id.pager);
		OnboardingAdapter adapter = new OnboardingAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);

		//Data.getData();
	}

	private void flipPage()
	{
		if (pager.getCurrentItem() + 1 < pager.getAdapter().getCount())
			pager.setCurrentItem(pager.getCurrentItem() + 1);
		else
		{
			if (SettingsUtil.SINGLETON.getName() != null
					&& SettingsUtil.SINGLETON.getEmail() != null)
			{
				//TODO check to make sure all fields are set
			//SettingsUtil.SINGLETON.setFirstRun();
				finish();
			}
			else
				Toast.makeText(this, R.string.onboarding_error, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onBackPressed() {
		if (pager.getCurrentItem() > 0)
			pager.setCurrentItem(pager.getCurrentItem() - 1);
		return;
	}

	static class OnboardingAdapter extends FragmentPagerAdapter
	{
		private static final Page[] pages = Page.values();

		OnboardingAdapter(FragmentManager fm)
		{
			super(fm);
		}
		@Override
		public Fragment getItem(int position)
		{
			OnboardingFragment fragment = new OnboardingFragment();
			Bundle args = new Bundle();
			args.putSerializable(OnboardingFragment.PAGE_KEY, pages[position]);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount()
		{
			return pages.length;
		}
	}

	public static class OnboardingFragment extends Fragment implements View.OnClickListener
	{
		private static final String TAG = OnboardingFragment.class.getSimpleName();
		static final String PAGE_KEY = "page";
		private static final ImmutableMap<Page, Integer> layoutForPage = ImmutableMap
				.of(Page.GetStarted, R.layout.onboarding_1,
						Page.Login, R.layout.onboarding_2,
						Page.FollowOrganizations, R.layout.onboarding_3,
						Page.FollowTags, R.layout.onboarding_4);
		private Page page;

		//sign in page
		static final int SIGN_IN = 1;
		private GoogleSignInClient signInClient;
		private ImageView image;

		@Nullable
		@Override
		public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
		{
			Bundle args = getArguments();
			page = (Page) args.getSerializable(PAGE_KEY);

			int layout = layoutForPage.get(page);
			View view = inflater.inflate(layout, container, false);
			configurePage(view);
			return view;
		}

		private void configurePage(View view)
		{
			switch (page)
			{
				case GetStarted:
					view.findViewById(R.id.getStartedButton).setOnClickListener(this);
					break;
				case Login:
					image = view.findViewById(R.id.profileImage);
					configureGoogleSignIn(view);
					break;
				case FollowOrganizations:
					view.findViewById(R.id.nextButton).setOnClickListener(this);
					RecyclerView recycler = view.findViewById(R.id.organizationsRecycler);
					orgAdapter = new OrganizationAdapter(getContext(), Data.organizations(), true, true);
					recycler.setAdapter(orgAdapter);
					Log.i("onboarding organizations", Data.organizations().toString());
					RecyclerUtil.addVerticalSpacing(recycler);
					break;
				case FollowTags:
					view.findViewById(R.id.doneButton).setOnClickListener(this);
					recycler = view.findViewById(R.id.tagRecycler);
					FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
					layoutManager.canScrollVertically();
					recycler.setLayoutManager(layoutManager);
					tagAdapter = new TagAdapter(getContext(), ImmutableList.copyOf(Data.tagForID.keySet()), true, false);
					recycler.setAdapter(new TagAdapter(getContext(), ImmutableList.copyOf(Data.tagForID.keySet()), true, false));
					Log.i("ongboarding tags", Data.tagForID.keySet().toString());
					RecyclerUtil.addVerticalSpacing(recycler);
					break;
			}
		}

		private void configureGoogleSignIn(View view)
		{
			GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
					.requestIdToken("498336876169-ic648e1orr7mjc3ts47o99f9uphpa01f.apps.googleusercontent.com")
					.requestEmail()
					.requestProfile()
					.setHostedDomain("cornell.edu")
					.build();
			signInClient = GoogleSignIn.getClient(getContext(), options);
			view.findViewById(R.id.signInButton).setOnClickListener(this);
		}

		@Override
		public void onClick(View view)
		{
			switch (view.getId())
			{
				case R.id.signInButton:
					Log.d("Pressed sign in button", "Sign in button pressed");
					Intent signInIntent = signInClient.getSignInIntent();
					startActivityForResult(signInIntent, SIGN_IN);
					((OnboardingActivity) getActivity()).flipPage();
					break;
				case R.id.getStartedButton:
					((OnboardingActivity) getActivity()).flipPage();
					break;
				case R.id.nextButton:
					saveSelectedOrganizations(orgAdapter.getSelectedOrganizations());
					((OnboardingActivity) getActivity()).flipPage();
					break;
				case R.id.doneButton:
					saveSelectedTags(tagAdapter.getSelectedTags());
					((OnboardingActivity) getActivity()).flipPage();
					break;
			}
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data)
		{
			super.onActivityResult(requestCode, resultCode, data);

			if (requestCode != SIGN_IN)
				return;

			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			try { setAccount(task.getResult(ApiException.class)); }
			catch (ApiException e) { Log.e(TAG, "sign in failed with code: " + e.getStatusCode()); }
		}

		private void setAccount(GoogleSignInAccount account)
		{
			SettingsUtil.SINGLETON.setName(account.getDisplayName());
			SettingsUtil.SINGLETON.setEmail(account.getEmail());
			SettingsUtil.SINGLETON.setImageUrl(account.getPhotoUrl().toString());
			Internet.downloadImage(account.getPhotoUrl().toString(), image);
			//TODO send server id token, save response
		}

		public void saveSelectedTags(Set<Integer> selectedTags) {
			if (!selectedTags.isEmpty()) {
				for (Integer id : selectedTags) {
					TagUtil.addTagToList(id);
				}
			}
		}

		public void saveSelectedOrganizations(Set<Organization> selectedOrgs) {
			if (!selectedOrgs.isEmpty()) {
				for (Organization org : selectedOrgs) {
					OrganizationUtil.followOrganization(org.id);
				}
			}
		}

	}
	enum Page
	{
		GetStarted, Login, FollowOrganizations, FollowTags
	}
}
