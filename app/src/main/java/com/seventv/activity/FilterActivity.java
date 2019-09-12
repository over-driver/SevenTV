package com.seventv.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.seventv.R;
import com.seventv.fragment.VideoListFragment;

import butterknife.ButterKnife;

public class FilterActivity extends AppCompatActivity {

    static String EXTRA_CATEGORY = "category";
    static String EXTRA_QUERY = "query";
    static String EXTRA_FILTER = "filter";
    static String EXTRA_FILTER_NAME = "filter_name";
    static String EXTRA_QUERY_NAME = "query_name";

    public static Intent newIntent(Context context, String category, String filter, String filterName, String query, String queryName){
        Intent intent = new Intent(context, FilterActivity.class);
        intent.putExtra(EXTRA_CATEGORY, category);
        intent.putExtra(EXTRA_QUERY, query);
        intent.putExtra(EXTRA_QUERY_NAME, queryName);
        intent.putExtra(EXTRA_FILTER, filter);
        intent.putExtra(EXTRA_FILTER_NAME, filterName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra(EXTRA_FILTER_NAME) + ": " + getIntent().getStringExtra(EXTRA_QUERY_NAME));

        Fragment fragment = VideoListFragment.newInstance(getIntent().getStringExtra(EXTRA_CATEGORY), getIntent().getStringExtra(EXTRA_FILTER), getIntent().getStringExtra(EXTRA_QUERY));
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
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

}
