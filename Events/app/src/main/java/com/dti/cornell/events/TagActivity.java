package com.dti.cornell.events;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.TagUtil;


/**
 * Created by jboss925 on 9/22/18.
 */

public class TagActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = MyEventsFragment.class.getSimpleName();

	public static void startWithTag(Context context, int tagID)
    {
        Intent intent = new Intent(context, TagActivity.class);
        intent.putExtra("TAG_ID", tagID);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

			FloatingActionButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(this);

			RecyclerView recyclerView = findViewById(R.id.recyclerView);
			int tagID = getIntent().getIntExtra("TAG_ID", 0);
        Log.e("CALLED", tagID + "");
        Log.e("CALLED", TagUtil.suggestEventsForTagID(tagID).toString());
        EventAdapter adapter = new EventAdapter(this, TagUtil.suggestEventsForTagID(tagID));

        recyclerView.setAdapter(adapter);
			RecyclerView.LayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

			TextView tagNameTitle = findViewById(R.id.tagTitle);
        tagNameTitle.setText(Data.tagForID.get(tagID));

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
            case R.id.backButton:
                onBackPressed();
                return;
        }
    }

    @Override
    public void onBackPressed() { //TODO is this needed?
        super.onBackPressed();
    }

//    public static class SearchAdapter extends FragmentPagerAdapter
//    {
//        private static final com.dti.cornell.events.SearchActivity.Page[] pages = com.dti.cornell.events.SearchActivity.Page.values();
//        private final List<Fragment> fragments = new ArrayList<>(pages.length);
//
//        public SearchAdapter(FragmentManager fm)
//        {
//            super(fm);
//            createFragments();
//        }
//        private void createFragments()
//        {
//            for (com.dti.cornell.events.SearchActivity.Page page : pages)
//            {
//                com.dti.cornell.events.SearchActivity.SearchEventFragment fragment = new com.dti.cornell.events.SearchActivity.SearchEventFragment();
//                Bundle args = new Bundle();
//                args.putSerializable(com.dti.cornell.events.SearchActivity.SearchEventFragment.PAGE_KEY, page);
//                fragment.setArguments(args);
//                fragments.add(fragment);
//            }
//        }
//        @Override
//        public Fragment getItem(int position)
//        {
//            return fragments.get(position);
//        }
//
//        @Override
//        public int getCount()
//        {
//            return fragments.size();
//        }
//    }

}
