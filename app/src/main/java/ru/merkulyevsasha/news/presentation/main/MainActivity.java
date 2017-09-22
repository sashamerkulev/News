package ru.merkulyevsasha.news.presentation.main;

import android.annotation.SuppressLint;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;
import ru.merkulyevsasha.apprate.AppRateRequester;
import ru.merkulyevsasha.news.BuildConfig;
import ru.merkulyevsasha.news.NewsApp;
import ru.merkulyevsasha.news.R;
import ru.merkulyevsasha.news.data.db.DatabaseHelper;
import ru.merkulyevsasha.news.data.prefs.NewsSharedPreferences;
import ru.merkulyevsasha.news.data.utils.NewsConstants;
import ru.merkulyevsasha.news.helpers.BroadcastHelper;
import ru.merkulyevsasha.news.presentation.webview.WebViewActivity;
import ru.merkulyevsasha.news.newsservices.HttpService;
import ru.merkulyevsasha.news.pojos.ItemNews;
import ru.merkulyevsasha.news.newsjobs.NewsJob;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    public static final String KEY_NAV_ID = "ru.merkulyevsasha.news.key_navId";
    private static final String KEY_POSITION = "ru.merkulyevsasha.news.key_position";
    public static final String KEY_REFRESHING = "ru.merkulyevsasha.news.key_refreshing";

    @State int mNavId;
    @State String mSearchText;

    @BindView(R.id.refreshLayout) SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawer;
    @BindView(R.id.nav_view) NavigationView mNavigationView;
    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    private MenuItem mSearchItem;
    private SearchView mSearchView;

    @Inject NewsConstants newsConsts;
    @Inject DatabaseHelper db;
    @Inject NewsSharedPreferences prefs;

    private BroadcastReceiver mReceiver;

    private AdView mAdView;

    private RecyclerViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        NewsApp.getComponent().inject(this);

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
        mAdapter = new RecyclerViewAdapter(new ArrayList<ItemNews>(), new OnNewsItemClickListener() {
            @Override
            public void onItemClick(ItemNews item) {
                WebViewActivity.startActivity(MainActivity.this, newsConsts.getSourceNameTitle(item.getSourceNavId()), item);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startService(mNavId, false);
            }
        });

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final boolean finished = intent.getBooleanExtra(BroadcastHelper.KEY_FINISH_NAME, false);
                final boolean updated = intent.getBooleanExtra(BroadcastHelper.KEY_UPDATE_NAME, false);

                if (updated) {
                    new ReadNews().run(mNavId);
                }
                if (finished) {
                    mRefreshLayout.setRefreshing(false);
                }
            }
        };

        mNavId = R.id.nav_all;
        setActivityTitle(mNavId);

        AppRateRequester.Run(this, BuildConfig.APPLICATION_ID);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = BuildConfig.DEBUG_MODE
                ? new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("349C53FFD0654BDC5FF7D3D9254FC8E6").build()
                : new AdRequest.Builder().addTestDevice("349C53FFD0654BDC5FF7D3D9254FC8E6").build();
        mAdView.loadAd(adRequest);

        if (prefs.getFirstRunFlag()){
            NewsJob.scheduleJob();
            prefs.setFirstRunFlag();
        }
    }

    private boolean isRefreshing() {
        return mRefreshLayout.isRefreshing();
    }

    private void searchViewText() {
        mSearchItem.expandActionView();
        mSearchView.setQuery(mSearchText, false);
    }

    private void setActivityTitle(int navId) {
        setTitle(newsConsts.getSourceNameTitle(navId));
    }

    private void startService(int navId, boolean isRefreshing) {
        startService(new Intent(this, HttpService.class)
                .putExtra(KEY_NAV_ID, navId)
                .putExtra(KEY_REFRESHING, isRefreshing));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Icepick.saveInstanceState(this, outState);

        outState.putInt(KEY_POSITION, mLayoutManager.findFirstCompletelyVisibleItemPosition());
        outState.putBoolean(KEY_REFRESHING, mRefreshLayout.isRefreshing());

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Icepick.restoreInstanceState(this, savedInstanceState);

        boolean isRefreshing = savedInstanceState.getBoolean(KEY_REFRESHING, false);
        mRefreshLayout.setRefreshing(isRefreshing);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastHelper.ACTION_NAME);
        registerReceiver(mReceiver, intentFilter);

        if (isRefreshing()) {
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
        List<ItemNews> items = db.query(searchText);
        if (items.size() > 0) {
            mAdapter.setItems(items);
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

    private class ReadNews extends AsyncTask<Integer, Void, List<ItemNews>> {

        private int navId;

        private List<ItemNews> getItemNews(int navId) {
            return navId == R.id.nav_all
                    ? db.selectAll()
                    : db.select(navId);
        }

        public void run(int id) {
            navId = id;
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, navId);
        }

        @Override
        protected void onPostExecute(List<ItemNews> result) {
            super.onPostExecute(result);
            if (result.size() > 0) {
                mAdapter.setItems(result);
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

    private interface OnNewsItemClickListener {
        void onItemClick(ItemNews item);
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder> {

        private final List<ItemNews> items;
        private final OnNewsItemClickListener onNewsItemClickListener;

        private RecyclerViewAdapter(List<ItemNews> items, OnNewsItemClickListener onNewsItemClickListener) {
            this.items = items;
            this.onNewsItemClickListener = onNewsItemClickListener;
        }

        @Override
        public RecyclerViewAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_news_item, parent, false);
            return new RecyclerViewAdapter.ItemViewHolder(view);
        }


        @Override
        public void onBindViewHolder(RecyclerViewAdapter.ItemViewHolder holder, int position) {

            final ItemNews item = items.get(position);

            int sourceNavId = item.getSourceNavId();
            String source = newsConsts.getSourceNameTitle(sourceNavId);
            String title = item.getTitle().trim();
            String description = item.getDescription();

            Date pubDate = item.getPubDate();
            @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            holder.sourceAndDate.setText(format.format(pubDate) + " " + source);
            holder.title.setText(title);

            holder.description.setText(description == null ? "" : description.trim());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNewsItemClickListener.onItemClick(item);
                }
            });

        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void setItems(List<ItemNews> items){
            this.items.clear();
            this.items.addAll(items);
            this.notifyDataSetChanged();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {

            private final TextView sourceAndDate;
            private final TextView title;
            private final TextView description;

            ItemViewHolder(View itemView) {
                super(itemView);

                sourceAndDate = (TextView) itemView.findViewById(R.id.news_date_source);
                title = (TextView) itemView.findViewById(R.id.news_title);
                description = (TextView) itemView.findViewById(R.id.news_description);
            }
        }

    }

}


