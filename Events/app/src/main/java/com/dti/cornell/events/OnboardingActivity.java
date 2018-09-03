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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.ImmutableMap;

public class OnboardingActivity extends AppCompatActivity
{
	private ViewPager pager;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_onboarding);

		pager = findViewById(R.id.pager);
		OnboardingAdapter adapter = new OnboardingAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);
	}

	public void flipPage()
	{
		if (pager.getCurrentItem() + 1 < pager.getAdapter().getCount())
			pager.setCurrentItem(pager.getCurrentItem() + 1);
		else
		{
			//TODO check to make sure all fields are set
//			SettingsUtil.SINGLETON.setFirstRun();
			finish();
		}
	}

	public static class OnboardingAdapter extends FragmentPagerAdapter
	{
		private static final Page[] pages = Page.values();

		public OnboardingAdapter(FragmentManager fm)
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
		public static final String PAGE_KEY = "page";
		private static final ImmutableMap<Page, Integer> layoutForPage = ImmutableMap
				.of(Page.GetStarted, R.layout.onboarding_1,
						Page.Login, R.layout.onboarding_2,
						Page.FollowOrganizations, R.layout.onboarding_3,
						Page.FollowTags, R.layout.onboarding_4);
		private Page page;

		public static final int SIGN_IN = 1;
		private GoogleSignInClient signInClient;

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
					configureGoogleSignIn(view);
					break;
				case FollowOrganizations:
					view.findViewById(R.id.nextButton).setOnClickListener(this);
					break;
				case FollowTags:
					view.findViewById(R.id.doneButton).setOnClickListener(this);
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

			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			try
			{
				GoogleSignInAccount account = task.getResult(ApiException.class);
				Log.i(TAG, "updateAccount: " + account.toJson());
			}
			catch (ApiException e) { Log.e(TAG, "sign in failed with code: " + e.getStatusCode()); }
		}
	}
	enum Page
	{
		GetStarted, Login, FollowOrganizations, FollowTags
	}
}
