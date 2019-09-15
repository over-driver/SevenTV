package com.seventv.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.seventv.R;
import com.seventv.SevenTVApplication;
import com.seventv.fragment.DbIdolListFragment;
import com.seventv.fragment.DbVideoListFragment;
import com.seventv.view.SimpleSearchView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteActivity extends BaseActivity {

    private static final int ID_VIDEO = 0;
    private static final int ID_IDOL = 1;
    private int current_id = ID_VIDEO;

    @BindView(R.id.view_pager)
    public ViewPager mViewPager;
    @BindView(R.id.tab_layout)
    public TabLayout mTabLayout;
    @BindView(R.id.search_view)
    public SimpleSearchView mSearchView;
    @BindView(R.id.tab_layout_bottom)
    public TabLayout mTabLayoutBottom;

    public static Intent newIntent(Context context){
        return new Intent(context, FavoriteActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.favorite));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new FavoriteActivity.TabsPagerAdapter(getSupportFragmentManager()));

        mTabLayout.setupWithViewPager(mViewPager, false);
        mTabLayoutBottom.addTab(mTabLayoutBottom.newTab().setText(getString(R.string.video)).setTag(ID_VIDEO));
        mTabLayoutBottom.addTab(mTabLayoutBottom.newTab().setText(getString(R.string.info_idol)).setTag(ID_IDOL));

        mTabLayoutBottom.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(current_id != (int) tab.getTag()){
                    current_id = (int) tab.getTag();
                    getSupportActionBar().setTitle(getString(R.string.favorite));
                    mViewPager.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_favorite, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(item);

        mSearchView.setOnSearchViewListener(new SimpleSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                String title = (String) getSupportActionBar().getTitle();
                int idx = title.indexOf(':');
                String query = (idx < 0) ? null : title.substring(idx+1);
                mSearchView.setQuery(query, false);
            }

            @Override
            public void onSearchViewClosed() {
                String query = mSearchView.getQuery();
                if(query.length() > 0){
                    getSupportActionBar().setTitle(getString(R.string.favorite) + ":" + query);
                }else{
                    getSupportActionBar().setTitle(getString(R.string.favorite));
                }
                mViewPager.getAdapter().notifyDataSetChanged();
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class TabsPagerAdapter extends FragmentStatePagerAdapter {

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object){
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            String title = (String) getSupportActionBar().getTitle();
            int idx = title.indexOf(':');
            String query = (idx < 0) ? "" : title.substring(idx+1);
            switch (current_id){
                case ID_VIDEO:
                    return DbVideoListFragment.newInstance(MainActivity.FRAGMENTS.get(position), query);
                case ID_IDOL:
                    return DbIdolListFragment.newInstance(MainActivity.FRAGMENTS.get(position), query);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return MainActivity.FRAGMENTS.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(MainActivity.TITLES.get(position));
        }
    }
}
