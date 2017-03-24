package ru.merkulyevsasha.news.presentation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;
import ru.merkulyevsasha.apprate.AppRateRequester;
import ru.merkulyevsasha.news.BuildConfig;
import ru.merkulyevsasha.news.R;
import ru.merkulyevsasha.news.data.db.DatabaseHelper;
import ru.merkulyevsasha.news.services.HttpService;
import ru.merkulyevsasha.news.helpers.Const;
import ru.merkulyevsasha.news.pojos.ItemNews;
import ru.merkulyevsasha.news.services.ServicesHelper;

import static ru.merkulyevsasha.news.services.HttpService.KEY_FINISH_NAME;
import static ru.merkulyevsasha.news.services.HttpService.KEY_UPDATE_NAME;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    public static final String KEY_NAV_ID = "navId";
    private static final String KEY_POSITION = "position";
    public static final String KEY_REFRESHING = "refreshing";


    @State int mNavId;
    @State String mSearchText;

    private DatabaseHelper mHelper;
    private RecyclerViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    @BindView(R.id.refreshLayout) public SwipeRefreshLayout mRefreshLayout;

    @BindView(R.id.drawer_layout) public DrawerLayout mDrawer;

    @BindView(R.id.nav_view) public NavigationView mNavigationView;

    @BindView(R.id.recyclerView) public RecyclerView mRecyclerView;

    @BindView(R.id.toolbar) public Toolbar mToolbar;

    private MenuItem mSearchItem;
    private SearchView mSearchView;

    private Const mConst;
    private BroadcastReceiver mReceiver;

    private HttpService mService;
    private boolean mBound = false;
    private AdView mAdView;


    /** Defines callbacks for service binding, passed to bindService() */
//    private final ServiceConnection mConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName className,
//                                       IBinder service) {
//            // We've bound to LocalService, cast the IBinder and get LocalService instance
//            HttpService.HttpServiceBinder binder = (HttpService.HttpServiceBinder) service;
//            mService = binder.getService();
//            mBound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            mBound = false;
//        }
//    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Icepick.saveInstanceState(this, outState);

        outState.putInt(KEY_POSITION, mLayoutManager.findFirstCompletelyVisibleItemPosition());
        outState.putBoolean(KEY_REFRESHING, mRefreshLayout.isRefreshing());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchViewText();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new RecyclerViewAdapter(this, new ArrayList<ItemNews>());
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startService(mNavId, false);
            }
        });

        mHelper = DatabaseHelper.getInstance(DatabaseHelper.getDbPath(this));
        mConst = new Const();

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final boolean finished = intent.getBooleanExtra(KEY_FINISH_NAME, false);
                final boolean updated = intent.getBooleanExtra(KEY_UPDATE_NAME, false);

                if (updated) {
                    new ReadNews().run(mNavId);
                }
                if (finished) {
                    mRefreshLayout.setRefreshing(false);
                }
            }
        };

        if (savedInstanceState == null) {
            mNavId = R.id.nav_all;
        } else {
            Icepick.restoreInstanceState(this, savedInstanceState);
            boolean isRefreshing = savedInstanceState.getBoolean(KEY_REFRESHING, false);
            mRefreshLayout.setRefreshing(isRefreshing);
        }
        setActivityTitle(mNavId);
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_right);

        AppRateRequester.Run(this, BuildConfig.APPLICATION_ID);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = BuildConfig.DEBUG_MODE
                ? new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build()
                : new AdRequest.Builder().addTestDevice("349C53FFD0654BDC5FF7D3D9254FC8E6").build();
        mAdView.loadAd(adRequest);

        ServicesHelper.register(this);
    }

    private boolean isRefreshing(){
        return mRefreshLayout.isRefreshing();
    }

    private void searchViewText() {
        mSearchItem.expandActionView();
        mSearchView.setQuery(mSearchText, false);
    }

    private void setActivityTitle(int navId) {
        int stringId = mConst.getTitleByNavId(navId);
        setTitle(stringId > 0 ? getResources().getString(stringId) : "");
    }

    private void startService(int navId, boolean isRefreshing){
        startService(new Intent(this, HttpService.class)
            .putExtra(KEY_NAV_ID, navId)
            .putExtra(KEY_REFRESHING, isRefreshing));
        // Bind to LocalService
//        Intent intent = new Intent(this, HttpService.class);
//        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HttpService.ACTION_NAME);
        registerReceiver(mReceiver, intentFilter);

        if (isRefreshing()){
            startService(mNavId, isRefreshing());
        } else {
            if (mSearchText == null || mSearchText.isEmpty()) {
                new ReadNews().run(mNavId);
            } else {
                search(mSearchText);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mReceiver);
        // Unbind from the service
//        if (mBound) {
//            unbindService(mConnection);
//            mBound = false;
//        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mSearchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);

        if (mSearchText != null && !mSearchText.isEmpty()) {
            searchViewText();
        }

        mSearchView.setOnQueryTextListener(this);

        MenuItem refreshItem = menu.findItem(R.id.action_refresh);
        refreshItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (!mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(true);
                    startService(mNavId, false);
                }
                return false;
            }
        });
        return true;
    }

    private void search(String searchText) {
        List<ItemNews> items = mHelper.query(searchText);
        if (items.size() > 0) {
            mAdapter.Items = items;
            mAdapter.notifyDataSetChanged();
        } else {
            Snackbar.make(this.findViewById(R.id.content_main), R.string.search_nofound_message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query.length() < 5) {
            Snackbar.make(this.findViewById(R.id.content_main), R.string.search_validation_message, Snackbar.LENGTH_LONG).show();
            return false;
        }
        mSearchText = query;
        search(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.isEmpty()) {
            mSearchText = "";
            new ReadNews().run(mNavId);
        }
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawer.closeDrawer(GravityCompat.START);

        setTitle(item.getTitle());
        mNavId = item.getItemId();

        new ReadNews().run(mNavId);

        return true;
    }

    private class ReadNews extends AsyncTask<Integer, Void, List<ItemNews>>{

        private int navId;

        private List<ItemNews> getItemNews(int navId) {
            return navId == R.id.nav_all
                    ? mHelper.selectAll()
                    : mHelper.select(navId);
        }

        public void run(int id) {
            navId = id;
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, navId);
        }

        @Override
        protected void onPostExecute(List<ItemNews> result) {
            super.onPostExecute(result);
            if (result.size() > 0) {
                mAdapter.Items = result;
                mAdapter.notifyDataSetChanged();
            } else {
                mRefreshLayout.setRefreshing(true);
                startService(navId, false);
            }
        }

        @Override
        protected List<ItemNews> doInBackground(Integer... params) {
            int navId = params[0];
            return getItemNews(navId);
        }
    }


    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

}


