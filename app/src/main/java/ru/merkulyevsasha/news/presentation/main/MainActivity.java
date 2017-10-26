package ru.merkulyevsasha.news.presentation.main;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import dagger.android.AndroidInjection;
import ru.merkulyevsasha.apprate.AppRateRequester;
import ru.merkulyevsasha.news.BuildConfig;
import ru.merkulyevsasha.news.R;
import ru.merkulyevsasha.news.data.db.DatabaseHelper;
import ru.merkulyevsasha.news.data.prefs.NewsSharedPreferences;
import ru.merkulyevsasha.news.data.utils.NewsConstants;
import ru.merkulyevsasha.news.helpers.BroadcastHelper;
import ru.merkulyevsasha.news.newsservices.HttpService;
import ru.merkulyevsasha.news.pojos.ItemNews;
import ru.merkulyevsasha.news.newsjobs.NewsJob;
import ru.merkulyevsasha.news.presentation.webview.WebViewActivity;

public class MainActivity extends AppCompatActivity
        implements MainView,
        NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    public static final String KEY_NAV_ID = "ru.merkulyevsasha.news.key_navId";
    private static final String KEY_POSITION = "ru.merkulyevsasha.news.key_position";
    public static final String KEY_REFRESHING = "ru.merkulyevsasha.news.key_refreshing";

    @Inject NewsConstants newsConsts;
    @Inject DatabaseHelper db;
    @Inject NewsSharedPreferences prefs;
    @Inject MainPresenter pres;

    @BindView(R.id.appbar_layout) AppBarLayout appbarLayout;
    @BindView(R.id.collapsinng_toolbar_layout) CollapsingToolbarLayout collapsToolbar;
    @BindView(R.id.refreshLayout) SwipeRefreshLayout refreshLayout;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.adView) AdView adView;

    @BindView(R.id.content_main) View root;

    private BroadcastReceiver broadcastReceiver;

    private NewsViewAdapter adapter;
    private LinearLayoutManager layoutManager;

    private NewsReaderTask newsReader;
    private NewsSearcherTask newsSearcher;

    private MenuItem searchItem;
    private SearchView searchView;

    private AppbarScrollExpander appbarScrollExpander;
    private boolean expanded;
    private int position;

    private int navId;
    private String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        AndroidInjection.inject(this);

        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pres.onPrepareToSearch();
            }
        });

        appbarScrollExpander = new AppbarScrollExpander(recyclerView, appbarLayout);
        appbarScrollExpander.setExpanded(expanded);
        collapsToolbar.setTitleEnabled(false);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new NewsViewAdapter(this, new ArrayList<ItemNews>(), new OnNewsItemClickListener() {
            @Override
            public void onItemClick(ItemNews item) {
                pres.onItemClicked(item);
            }
        });
        recyclerView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pres.onRefresh(navId);
            }
        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final boolean finished = intent.getBooleanExtra(BroadcastHelper.KEY_FINISH_NAME, false);
                final boolean updated = intent.getBooleanExtra(BroadcastHelper.KEY_UPDATE_NAME, false);

                pres.onReceived(navId, updated, finished);
            }
        };

        navId = R.id.nav_all;
        setTitle(newsConsts.getSourceNameTitle(navId));

        AppRateRequester.Run(this, BuildConfig.APPLICATION_ID);

        AdRequest adRequest = BuildConfig.DEBUG_MODE
                ? new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("349C53FFD0654BDC5FF7D3D9254FC8E6").build()
                : new AdRequest.Builder().addTestDevice("349C53FFD0654BDC5FF7D3D9254FC8E6").build();
        adView.loadAd(adRequest);

        if (prefs.getFirstRunFlag()){
            NewsJob.scheduleJob();
            prefs.setFirstRunFlag();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_POSITION, layoutManager.findFirstCompletelyVisibleItemPosition());
        outState.putBoolean(KEY_REFRESHING, refreshLayout.isRefreshing());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        boolean isRefreshing = savedInstanceState.getBoolean(KEY_REFRESHING, false);
        position = savedInstanceState.getInt(KEY_POSITION, -1);
        expanded = savedInstanceState.getBoolean(KEY_POSITION, true);
        refreshLayout.setRefreshing(isRefreshing);
    }

    @Override
    public void onPause() {
        if (newsReader != null) newsReader.cancel(false);
        if (newsSearcher != null) newsSearcher.cancel(false);
        if (adView != null) {
            adView.pause();
        }
        pres.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        pres.setView(this);
        pres.onResume(refreshLayout.isRefreshing(), navId, searchText);
        if (adView != null) {
            adView.resume();
        }
        appbarLayout.setExpanded(expanded);
        if (position > 0) layoutManager.scrollToPosition(position);
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        if (searchText != null && !searchText.isEmpty()) {
            pres.onPrepareToSearch();
        }

        searchView.setOnQueryTextListener(this);

        MenuItem refreshItem = menu.findItem(R.id.action_refresh);
        refreshItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (!refreshLayout.isRefreshing()) {
                    pres.onRefresh(navId);
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query.length() < 5) {
            Snackbar.make(root, R.string.search_validation_message, Snackbar.LENGTH_LONG).show();
            return false;
        }
        searchText = query;
        pres.onSearch(searchText);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.isEmpty()) {
            searchText = "";
            pres.onCancelSearch(navId);
        }
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);

        setTitle(item.getTitle());
        navId = item.getItemId();

        pres.onSelectSource(navId);

        return true;
    }

    @Override
    public void loadFreshNews(int navId) {
        refreshLayout.setRefreshing(true);
        startService(navId, false);
    }

    @Override
    public void readNews(int navId) {
        newsReader = new NewsReaderTask();
        newsReader.run(navId);
    }

    @Override
    public void searchNews(String searchText) {
        newsSearcher = new NewsSearcherTask();
        newsSearcher.run(searchText);
    }

    @Override
    public void startLoadingNewsService() {
        startService(navId, refreshLayout.isRefreshing());
    }

    private LocalBroadcastManager getLocalBroadcastManager(){
        return LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void registerBroadcastReceiver() {
        getLocalBroadcastManager().registerReceiver(broadcastReceiver, new IntentFilter(BroadcastHelper.ACTION_NAME));
    }

    @Override
    public void unregisterBroadcastReceiver() {
        getLocalBroadcastManager().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void prepareToSearch() {
        searchItem.expandActionView();
        searchView.setQuery(searchText, false);
    }

    @Override
    public void showWebViewScreen(ItemNews item) {
        WebViewActivity.startActivity(MainActivity.this, newsConsts.getSourceNameTitle(item.getSourceNavId()), item);
    }

    @Override
    public void hideProgress() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void showItems(List<ItemNews> result) {
        adapter.setItems(result);
    }

    @Override
    public void showNoSearchResultMessage() {
        Snackbar.make(root, R.string.search_nofound_message, Snackbar.LENGTH_LONG).show();
    }

    private void startService(int navId, boolean isRefreshing) {
        startService(new Intent(this, HttpService.class)
                .putExtra(KEY_NAV_ID, navId)
                .putExtra(KEY_REFRESHING, isRefreshing));
    }

    @SuppressLint("StaticFieldLeak")
    private class NewsReaderTask extends AsyncTask<Integer, Void, List<ItemNews>> {

        private int navId;

        void run(int id) {
            navId = id;
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, navId);
        }

        @Override
        protected void onPostExecute(List<ItemNews> result) {
            super.onPostExecute(result);
            if (!isCancelled()) {
                pres.onLoadResult(navId, result);
            }
            newsReader = null;
        }

        @Override
        protected List<ItemNews> doInBackground(Integer... params) {
            int navId = params[0];
            return navId == R.id.nav_all
                    ? db.selectAll()
                    : db.select(navId);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class NewsSearcherTask extends AsyncTask<String, Void, List<ItemNews>> {

        void run(String searchText) {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, searchText);
        }

        @Override
        protected void onPostExecute(List<ItemNews> result) {
            super.onPostExecute(result);
            if (!isCancelled()) {
                pres.onSearchResult(result);
            }
            newsSearcher = null;
        }

        @Override
        protected List<ItemNews> doInBackground(String... params) {
            String searchText = params[0];
            return db.query(searchText);
        }
    }

    private interface OnNewsItemClickListener {
        void onItemClick(ItemNews item);
    }

    private class NewsViewAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        private final List<ItemNews> items;
        private final OnNewsItemClickListener onNewsItemClickListener;
        private final Context context;

        private NewsViewAdapter(Context context, List<ItemNews> items, OnNewsItemClickListener onNewsItemClickListener) {
            this.context = context;
            this.items = items;
            this.onNewsItemClickListener = onNewsItemClickListener;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_news_item, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {

            final ItemNews item = items.get(position);

            int sourceNavId = item.getSourceNavId();
            String source = newsConsts.getSourceNameTitle(sourceNavId);
            String title = item.getTitle().trim();
            String description = item.getDescription();
            String url = item.getPictureUrl();

            Date pubDate = item.getPubDate();
            @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            holder.sourceAndDate.setText(String.format("%s %s", format.format(pubDate), source));

            if (title.equals(description) || description == null || description.isEmpty()){
                holder.description.setVisibility(View.GONE);
            } else {
                holder.description.setVisibility(View.VISIBLE);
                holder.description.setText(description.trim());
            }
            holder.title.setText(title);
            holder.thumb.setImageResource(0);
            if (url == null){
                holder.thumb.setVisibility(View.GONE);
            } else {
                holder.thumb.setVisibility(View.VISIBLE);
                Glide.with(context).load(url).into(holder.thumb);
            }

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

        void setItems(List<ItemNews> items){
            this.items.clear();
            this.items.addAll(items);
            this.notifyDataSetChanged();
        }

    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.news_date_source) TextView sourceAndDate;
        @BindView(R.id.news_title) TextView title;
        @BindView(R.id.news_description) TextView description;
        @BindView(R.id.imageview_thumb) ImageView thumb;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}


