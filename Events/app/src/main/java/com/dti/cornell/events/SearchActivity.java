package com.dti.cornell.events;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.SearchView;
import android.widget.TextView;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.utils.EventBusUtils;
import com.dti.cornell.events.utils.SearchUtil;
import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

;

/**
 * Created by jboss925 on 9/3/18.
 */

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener {

    private static final String TAG = MyEventsFragment.class.getSimpleName();
    private SearchView searchBar;
    private EventAdapter adapter;
	private RecyclerView recyclerView;
	private boolean noResults = false;

    public static void start(Context context)
    {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchBar = findViewById(R.id.searchView);
        searchBar.setOnQueryTextListener(this);
			ConstraintLayout backgound = findViewById(R.id.searchBackground);
			TextView backgroundTextView = findViewById(R.id.searchBackgroundText);

			FloatingActionButton backButton = findViewById(R.id.back2);
        backButton.setOnClickListener(this);

			ViewPager pager = findViewById(R.id.searchViewPager);
        pager.setOffscreenPageLimit(Page.values().length);
			SearchAdapter searchFragmentAdapter = new SearchAdapter(getSupportFragmentManager());
        pager.setAdapter(searchFragmentAdapter);
        //DEPRECATED: RecyclerUtil.configureEvents(recyclerView);
        //setOnScrollListener();

    }

    //change selected date when scrolling through events
//    private void setOnScrollListener()
//    {
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
//        {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
//            {
//                int topItemPosition = layoutManager.findFirstVisibleItemPosition();
//                DateTime dateTime = adapter.getDateAtPosition(topItemPosition);
//
//                if (Data.equalsSelectedDate(dateTime))
//                    return;
//
//                Data.selectedDate = dateTime;
//                EventBusUtils.SINGLETON.post(new EventBusUtils.DateChanged(TAG));
//            }
//        });
//    }

    @Override
    public boolean onQueryTextChange(String newText) {
        EventBusUtils.SINGLETON.post(new EventBusUtils.SearchChanged(newText));
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String newText) {
        EventBusUtils.SINGLETON.post(new EventBusUtils.SearchChanged(newText));
        searchBar.clearFocus();
        return true;
    }

//    public void noResults(){
//        backgound.setVisibility(View.VISIBLE);
//        pager.setVisibility(View.GONE);
//        backgroundTextView.setText("No results found.");
//    }
//
//    public void someResults(){
//        backgound.setVisibility(View.GONE);
//        pager.setVisibility(View.VISIBLE);
//    }

    //events subscription

//    @Subscribe
//    public void onDateChanged(EventBusUtils.DateChanged dateChanged)
//    {
//        if (dateChanged.sender.equals(TAG))
//            return;
//
//        //scroll to the new date
//        int position = adapter.getPositionForDate(Data.selectedDate);
//        ((LinearLayoutManager) getFragmentRecyclerView().getLayoutManager()).scrollToPositionWithOffset(position, 0);
//    }

    @Override
    public void onClick(View view) {
	    switch (view.getId())
	    {
		    case R.id.back2:
		    	onBackPressed();
		    	return;
	    }
    }

    @Override
    public void onBackPressed() { //TODO is this needed?
        Log.e("HELP", "SHITT GOT PRESSED");
        setContentView(R.layout.activity_main);
        super.onBackPressed();
    }

    static class SearchAdapter extends FragmentPagerAdapter
    {
        private static final Page[] pages = Page.values();
	    private final List<Fragment> fragments = new ArrayList<>(pages.length);

        SearchAdapter(FragmentManager fm)
        {
            super(fm);
            createFragments();
        }
	    private void createFragments()
	    {
		    for (Page page : pages)
		    {
			    SearchActivity.SearchFragment fragment = new SearchActivity.SearchFragment();
			    Bundle args = new Bundle();
			    args.putSerializable(SearchActivity.SearchFragment.PAGE_KEY, page);
			    fragment.setArguments(args);
			    fragments.add(fragment);
		    }
	    }
        @Override
        public Fragment getItem(int position)
        {
            return fragments.get(position);
        }

        @Override
        public int getCount()
        {
            return fragments.size();
        }
    }

    public static class SearchFragment extends Fragment implements View.OnClickListener {
        private static final String TAG = SearchActivity.SearchFragment.class.getSimpleName();
        static final String PAGE_KEY = "page";
        private SearchActivity.Page page;

        RecyclerView recyclerView;
        private EventAdapter adapter;

	    @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            EventBusUtils.SINGLETON.register(this);
            Bundle args = getArguments();
            page = (SearchActivity.Page) args.getSerializable(PAGE_KEY);

            View view = inflater.inflate(R.layout.fragment_recycler, container, false);
            recyclerView = view.findViewById(R.id.recyclerView);
            adapter = new EventAdapter(getContext(), Collections.<Event>emptyList());
            recyclerView.setAdapter(adapter);
            configurePage(view);
            return view;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            EventBusUtils.SINGLETON.unregister(this);
        }

        private void configurePage(View view) {
            switch (page) {
                case Events:
                    break;
                case Orgs:
                    break;
                case Tags:
//                    view.findViewById(R.id.nextButton).setOnClickListener(this);
//                    RecyclerView recycler = view.findViewById(R.id.organizationsRecycler);
//                    recycler.setAdapter(new OrganizationAdapter(getContext(), Data.organizations(), true));
//                    RecyclerUtil.addVerticalSpacing(recycler);
                    break;
            }
        }

        @Override
        public void onClick(View view) {
        }

        @Subscribe
        public void onSearchChanged(EventBusUtils.SearchChanged searchChanged)
        {
            List<Event> events = SearchUtil.getEventsFromSearch(searchChanged.text);
            if(events.isEmpty()){
//                noResults();
//                noResults = true;
//                return true;
                return;
            }

            adapter.updateList(events);
        }
    }
    enum Page
    {
        Events, Orgs, Tags
    }
}
