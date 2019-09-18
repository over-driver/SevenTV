package com.seventv.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.constraint.Guideline;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialize.util.UIUtils;
import com.seventv.R;
import com.seventv.network.NetworkBasic;
import com.seventv.view.SimpleSearchView;
import com.seventv.fragment.VideoListFragment;
import com.seventv.network.api.SevenAPI;
import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    public static final int ID_CENSORED = 0;
    public static final int ID_AMATEUR = 1;
    public static final int ID_UNCENSORED = 2;
    public static final int ID_CHINESE = 3;

    public static final int ID_FAVORITE = 4;
    public static final int ID_SETTING = 5;

    public static final Map<Integer, String> FRAGMENTS = new HashMap<Integer, String>() {{
        put(ID_CENSORED, SevenAPI.CATEGORY_CENCORED);
        put(ID_AMATEUR, SevenAPI.CATEGORY_AMATEUR);
        put(ID_UNCENSORED , SevenAPI.CATEGORY_UNCENCORED);
        put(ID_CHINESE, SevenAPI.CATEGORY_CHINESE);
    }};

    public static final Map<Integer, Integer> TITLES = new HashMap<Integer, Integer>(){{
       put(ID_CENSORED, R.string.censored_short);
       put(ID_AMATEUR, R.string.amateur_short);
       put(ID_CHINESE, R.string.chinese_short);
       put(ID_UNCENSORED, R.string.uncensored_short);
    }};

    private static final String KEY_FRAGMENT = "fragment_id";

    @BindView(R.id.app_bar)
    public AppBarLayout mAppBarLayout;
    @BindView(R.id.search_view)
    public SimpleSearchView mSearchView;
    @BindView(R.id.toolbar)
    public Toolbar mToolbar;
    private Bundle mSavedInstanceState;
    private Drawer mDrawer;
    private int mCurrentFragmentId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //R.layout.activity_main
        ButterKnife.bind(this);

        mSavedInstanceState = savedInstanceState;
        setSupportActionBar(mToolbar);

        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("auto_update", true)){
            Log.d("Main", "auto_update");
            NetworkBasic.checkUpdate(this, false);
        }

        initFragment();
        buildDrawer();

        if (Build.VERSION.SDK_INT >= 19) {
            mDrawer.getDrawerLayout().setFitsSystemWindows(false);
        }
    }



    public void initFragment(){
        FragmentManager fm = getSupportFragmentManager();

        if(mSavedInstanceState != null){
            mCurrentFragmentId = mSavedInstanceState.getInt(KEY_FRAGMENT);
        }

        FragmentTransaction transaction = fm.beginTransaction();
        for (Map.Entry<Integer, String> entry: FRAGMENTS.entrySet()){
            try{
                Fragment fragment = VideoListFragment.newInstance(entry.getValue());
                transaction.add(R.id.fragment_container, fragment, entry.getValue()).hide(fragment);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        transaction.commit();
        fm.executePendingTransactions();
    }

    private void setFragment(int id) {
        //getSupportActionBar().setTitle(getResources().getString(TITLES.get(id)));
        getSupportActionBar().setTitle(getString(TITLES.get(id)));
        if (mCurrentFragmentId == id){
            return;
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (mCurrentFragmentId > 0){
            Fragment oldFragment = fm.findFragmentByTag(FRAGMENTS.get(mCurrentFragmentId));
            if (oldFragment != null){
                transaction.hide(oldFragment);
            }
        }
        transaction.show(fm.findFragmentByTag(FRAGMENTS.get(id)));
        transaction.commit();
        mCurrentFragmentId = id;
    }

    public void buildDrawer(){

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        @ColorInt int colorPrimary = typedValue.data;

        Drawer drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(ID_CENSORED).withName(getString(R.string.censored)).withIcon(R.drawable.ic_censored).withIconTintingEnabled(true).withSelectedIconColor(colorPrimary).withSelectedTextColor(colorPrimary),
                        new PrimaryDrawerItem().withIdentifier(ID_AMATEUR).withName(getString(R.string.amateur)).withIcon(R.drawable.ic_amatuer).withIconTintingEnabled(true).withSelectedIconColor(colorPrimary).withSelectedTextColor(colorPrimary),
                        new PrimaryDrawerItem().withIdentifier(ID_UNCENSORED).withName(getString(R.string.uncensored)).withIcon(R.drawable.ic_uncensored).withIconTintingEnabled(true).withSelectedIconColor(colorPrimary).withSelectedTextColor(colorPrimary),
                        new PrimaryDrawerItem().withIdentifier(ID_CHINESE).withName(getString(R.string.chinese)).withIcon(R.drawable.ic_chinese).withIconTintingEnabled(true).withSelectedIconColor(colorPrimary).withSelectedTextColor(colorPrimary),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withIdentifier(ID_FAVORITE).withName(getString(R.string.favorite)).withIcon(R.drawable.ic_star).withIconTintingEnabled(true).withSelectable(false),
                        new PrimaryDrawerItem().withIdentifier(ID_SETTING).withName(getString(R.string.setting)).withIcon(R.drawable.ic_setting).withIconTintingEnabled(true).withSelectable(false)
                )
                .withSelectedItem(ID_CENSORED)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        int itemId = (int) drawerItem.getIdentifier();
                        if(itemId < 4){
                            setFragment((int) drawerItem.getIdentifier());
                        }else if(itemId == 4){
                            Intent intent = FavoriteActivity.newIntent(MainActivity.this);
                            startActivity(intent);
                        }else if(itemId == 5){
                            Intent intent = SettingActivity.newIntent(MainActivity.this);
                            startActivity(intent);
                        }
                        return false;
                    }
                })
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Guideline guideline = drawer.getHeader().findViewById(R.id.guideline_status_bar);
            guideline.setGuidelineBegin(UIUtils.getStatusBarHeight(this, true));
            findViewById(R.id.coordinator_layout).setPadding(0, UIUtils.getStatusBarHeight(this, true), 0, 0);
            //Guideline guideline1 = findViewById(R.id.guideline_status_bar1);
            //guideline1.setGuidelineBegin(UIUtils.getStatusBarHeight(this, true));

            //mAppBarLayout.setMinimumHeight(UIUtils.getStatusBarHeight(this, true));
            //findViewById(R.id.constraint_layout).setMinimumHeight(UIUtils.getStatusBarHeight(this, true));
        }

        mDrawer = drawer;
        //mDrawer.getHeader().findViewById()

        if (mSavedInstanceState != null) {
            mDrawer.setSelection(mSavedInstanceState.getInt(KEY_FRAGMENT));
        } else {
            mDrawer.setSelection(ID_CENSORED);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(item);
        mSearchView.setOnQueryTextListener(new SimpleSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try{
                    Log.d("QueryTextSubmit", "submitted: " + query);
                    Intent intent = SearchActivity.newIntent(MainActivity.this, FRAGMENTS.get(mCurrentFragmentId), query);
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
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_FRAGMENT, mCurrentFragmentId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume(){
        super.onResume();
        mSearchView.closeSearch();
    }
}
