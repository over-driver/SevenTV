package com.seventv.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.seventv.R;
import com.seventv.SevenTVApplication;
import com.seventv.view.SimpleSearchView;
import com.seventv.fragment.VideoListFragment;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends BaseActivity {

    static String EXTRA_CATEGORY = "category";
    static String EXTRA_QUERY = "query";

    @BindView(R.id.view_pager)
    public ViewPager mViewPager;
    @BindView(R.id.tab_layout)
    public TabLayout mTabLayout;
    @BindView(R.id.search_view)
    public SimpleSearchView mSearchView;


    private String mQuery;
    private String mCategory;

    public static Intent newIntent(Context context, String category, String query){
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(EXTRA_CATEGORY, category);
        intent.putExtra(EXTRA_QUERY, query);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        mQuery = getIntent().getStringExtra(EXTRA_QUERY);
        Log.d("Search Activity", "query: " + mQuery);
        mCategory = getIntent().getStringExtra(EXTRA_CATEGORY);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("search");

        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new TabsPagerAdapter(getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) { }

            @Override
            public void onPageSelected(int i) {
                mCategory = MainActivity.FRAGMENTS.get(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) { }
        });
        mTabLayout.setupWithViewPager(mViewPager, false);
        for(Map.Entry<Integer, String> entry : MainActivity.FRAGMENTS.entrySet()){
            if (entry.getValue().equals(mCategory)){
                mViewPager.setCurrentItem(entry.getKey());
                break;
            }
        }

        mSearchView.alwaysShow(mQuery);

        mSearchView.setOnQueryTextListener(new SimpleSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try{
                    Intent intent = SearchActivity.newIntent(SearchActivity.this, mCategory, query);
                    startActivity(intent);
                } catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        mSearchView.setQuery(mQuery,false);
    }

    public class TabsPagerAdapter extends FragmentPagerAdapter {

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return VideoListFragment.newInstance(MainActivity.FRAGMENTS.get(position), mQuery);
        }

        @Override
        public int getCount() {
            return MainActivity.FRAGMENTS.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getString(MainActivity.TITLES.get(position));
            //return SevenTVApplication.myGetString(MainActivity.TITLES.get(position));
        }
    }
}
