package com.dti.cornell.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.Internet;
import com.dti.cornell.events.utils.RecyclerUtil;
import com.dti.cornell.events.utils.SettingsUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class OnboardingActivity extends AppCompatActivity
{
	private ViewPager pager;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		setTheme(R.style.AppTheme_NoActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_onboarding);

		pager = findViewById(R.id.pager);
		OnboardingAdapter adapter = new OnboardingAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);
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
//			SettingsUtil.SINGLETON.setFirstRun();
				finish();
			}
			else
				Toast.makeText(this, R.string.onboarding_error, Toast.LENGTH_LONG).show();
		}
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
					recycler.setAdapter(new OrganizationAdapter(getContext(), Data.organizations(), true));
					RecyclerUtil.addVerticalSpacing(recycler);
					break;
				case FollowTags:
					view.findViewById(R.id.doneButton).setOnClickListener(this);
					recycler = view.findViewById(R.id.tagRecycler);
					recycler.setAdapter(new TagAdapter(getContext(), ImmutableList.copyOf(Data.tagForID.keySet()), true));
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
					break;
				case R.id.getStartedButton:
				case R.id.nextButton:
				case R.id.doneButton:
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

			Log.d("On Activity Result", "At Activity Result");
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			Log.d("On Activity Result", task.toString());
			try { setAccount(task.getResult(ApiException.class)); }
			catch (ApiException e) { Log.e(TAG, "sign in failed with code: " + e.getStatusCode()); }
		}

		private void setAccount(GoogleSignInAccount account)
		{
			Log.d(TAG, "begin set account");
			SettingsUtil.SINGLETON.setName(account.getDisplayName());
			SettingsUtil.SINGLETON.setEmail(account.getEmail());
			SettingsUtil.SINGLETON.setImageUrl(account.getPhotoUrl().toString());
//			Internet.downloadImage(account.getPhotoUrl().toString(), image);
			//TODO send server id token, save response
			Log.d(TAG, "updateAccount: " + account.toJson());
		}
	}
	enum Page
	{
		GetStarted, Login, FollowOrganizations, FollowTags
	}
}
