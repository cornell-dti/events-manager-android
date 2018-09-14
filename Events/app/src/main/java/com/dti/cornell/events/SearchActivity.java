package com.dti.cornell.events;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TextView;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.EventBusUtils;
import com.dti.cornell.events.utils.Internet;
import com.dti.cornell.events.utils.RecyclerUtil;
import com.dti.cornell.events.utils.SearchUtil;
import com.dti.cornell.events.utils.SettingsUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.Subscribe;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by jboss925 on 9/3/18.
 */

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener {

    private static final String TAG = MyEventsFragment.class.getSimpleName();
    private SearchView searchBar;
    private EventAdapter adapter;
    private TextView backgroundTextView;
    private ConstraintLayout backgound;
    private FloatingActionButton backButton;
    private ViewPager pager;
    private RecyclerView recyclerView;
    private SearchAdapter searchFragmentAdapter;
    private boolean noResults = false;

    public static void start(Context context)
    {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchBar = findViewById(R.id.searchView);
        searchBar.setOnQueryTextListener(this);
        backgound = findViewById(R.id.searchBackground);
        backgroundTextView = findViewById(R.id.searchBackgroundText);

        backButton = findViewById(R.id.back2);
        backButton.setOnClickListener(this);

        pager = findViewById(R.id.searchViewPager);
        searchFragmentAdapter = new SearchActivity.SearchAdapter(getSupportFragmentManager());;
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
        if(view.getId() == R.id.back2){
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        Log.e("HELP", "SHIT GOT PRESSED");
        setContentView(R.layout.activity_main);
        super.onBackPressed();
    }

    public static class SearchAdapter extends FragmentPagerAdapter
    {
        private static final SearchActivity.Page[] pages = SearchActivity.Page.values();

        public SearchAdapter(FragmentManager fm)
        {
            super(fm);
        }
        @Override
        public Fragment getItem(int position)
        {
            SearchActivity.SearchFragment fragment = new SearchActivity.SearchFragment();
            Bundle args = new Bundle();
            args.putSerializable(SearchActivity.SearchFragment.PAGE_KEY, pages[position]);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount()
        {
            return pages.length;
        }
    }

    public static class SearchFragment extends Fragment implements View.OnClickListener {
        private static final String TAG = SearchActivity.SearchFragment.class.getSimpleName();
        public static final String PAGE_KEY = "page";
        private static final ImmutableMap<SearchActivity.Page, Integer> layoutForPage = ImmutableMap
                .of(Page.Events, R.layout.fragment_search_page,
                        Page.Orgs, R.layout.fragment_search_page,
                        Page.Tags, R.layout.fragment_search_page);
        private SearchActivity.Page page;

        public RecyclerView recyclerView;
        private EventAdapter adapter;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            EventBusUtils.SINGLETON.register(this);

            Bundle args = getArguments();
            page = (SearchActivity.Page) args.getSerializable(PAGE_KEY);

            int layout = layoutForPage.get(page);
            View view = inflater.inflate(layout, container, false);
            //onViewCreated(view, null);
            recyclerView = view.findViewById(R.id.followingRecycler);
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
            adapter = new EventAdapter(getContext(), events);
            recyclerView.setAdapter(adapter);
        }
    }
    enum Page
    {
        Events, Orgs, Tags
    }
}
