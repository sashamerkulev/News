package ru.merkulyevsasha.news.presentation.main;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import ru.merkulyevsasha.news.data.utils.NewsConstants;
import ru.merkulyevsasha.news.helpers.BroadcastHelper;
import ru.merkulyevsasha.news.newsjobs.NewsJob;
import ru.merkulyevsasha.news.models.Article;

public class MainActivity extends AppCompatActivity
        implements MainView, NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    public static final String KEY_NAV_ID = "ru.merkulyevsasha.news.key_navId";
    private static final String KEY_POSITION = "ru.merkulyevsasha.news.key_position";
    private static final String KEY_EXPANDED = "ru.merkulyevsasha.news.key_expanded";
    public static final String KEY_REFRESHING = "ru.merkulyevsasha.news.key_refreshing";

    @Inject NewsConstants newsConsts;
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

    @BindView(R.id.button_up) View buttonUp;

    private NewsViewAdapter adapter;
    private LinearLayoutManager layoutManager;

    private MenuItem searchItem;
    private SearchView searchView;

    private boolean expanded = true;
    private int position;

    private int navId;
    private String searchText;

    private AppbarScrollExpander appbarScrollExpander;

    private BroadcastReceiver broadcastReceiver;

    private int lastVisibleItemPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(view -> pres.onPrepareToSearch());

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
        adapter = new NewsViewAdapter(this, new ArrayList<>(), item -> pres.onItemClicked(item));
        recyclerView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(() -> pres.onRefresh(navId));
        refreshLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.colorAccent));
        refreshLayout.setColorSchemeResources(R.color.white);

        navId = R.id.nav_all;
        setTitle(newsConsts.getSourceNameTitle(navId));

        AppRateRequester.Run(this, BuildConfig.APPLICATION_ID);

        AdRequest adRequest = BuildConfig.DEBUG_MODE
                ? new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("349C53FFD0654BDC5FF7D3D9254FC8E6").build()
                : new AdRequest.Builder().addTestDevice("349C53FFD0654BDC5FF7D3D9254FC8E6").build();
        adView.loadAd(adRequest);

        pres.bindView(this);
        pres.onCreateView();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final boolean finished = intent.getBooleanExtra(BroadcastHelper.KEY_FINISH_NAME, false);
                final boolean updated = intent.getBooleanExtra(BroadcastHelper.KEY_UPDATE_NAME, false);

                pres.onReceived(navId, updated, finished);
            }
        };

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (lastVisibleItemPosition >= layoutManager.findFirstVisibleItemPosition()) {
                    lastVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    if (lastVisibleItemPosition > 5 && !refreshLayout.isRefreshing()) {
                        buttonUp.setVisibility(View.VISIBLE);
                    } else {
                        buttonUp.setVisibility(View.GONE);
                    }
                } else {
                    lastVisibleItemPosition = layoutManager.findFirstVisibleItemPosition() - 1;
                    buttonUp.setVisibility(View.GONE);
                }
            }
        });

        buttonUp.setOnClickListener(view -> {
            layoutManager.scrollToPosition(0);
            lastVisibleItemPosition = 0;
            buttonUp.setVisibility(View.GONE);
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_NAV_ID, navId);
        outState.putInt(KEY_POSITION, layoutManager.findFirstVisibleItemPosition());
        outState.putBoolean(KEY_REFRESHING, refreshLayout.isRefreshing());
        outState.putBoolean(KEY_EXPANDED, appbarScrollExpander.getExpanded());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        boolean isRefreshing = savedInstanceState.getBoolean(KEY_REFRESHING, false);
        position = savedInstanceState.getInt(KEY_POSITION, -1);
        navId = savedInstanceState.getInt(KEY_NAV_ID, R.id.nav_all);
        expanded = savedInstanceState.getBoolean(KEY_EXPANDED, true);
        refreshLayout.setRefreshing(isRefreshing);
    }

    @Override
    public void onPause() {
        position = layoutManager.findFirstVisibleItemPosition();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        if (adView != null) {
            adView.pause();
        }
        pres.unbindView();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        pres.bindView(this);
        pres.onResume(refreshLayout.isRefreshing(), navId, searchText);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(BroadcastHelper.ACTION_LOADING));
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
        //pres.onDestroy();
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
        searchView = (SearchView) searchItem.getActionView();

        if (searchText != null && !searchText.isEmpty()) {
            pres.onPrepareToSearch();
        }

        searchView.setOnQueryTextListener(this);

        MenuItem refreshItem = menu.findItem(R.id.action_refresh);
        refreshItem.setOnMenuItemClickListener(menuItem -> {
            if (!refreshLayout.isRefreshing()) {
                pres.onRefresh(navId);
            }
            return false;
        });
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query.length() < 3) {
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
    public void prepareToSearch() {
        searchItem.expandActionView();
        searchView.setQuery(searchText, false);
    }

    @Override
    public void showDetailScreen(@NonNull Article item) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary)).build();
        customTabsIntent.launchUrl(this, Uri.parse(item.getLink()));
    }

    @Override
    public void showProgress() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void showItems(@NonNull List<? extends Article> result) {
        adapter.setItems(result);
    }

    @Override
    public void showNoSearchResultMessage() {
        Snackbar.make(root, R.string.search_nofound_message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showMessageError() {
        Snackbar.make(root, R.string.something_went_wrong_message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void scheduleJob() {
        NewsJob.Companion.scheduleJob();
    }

    private interface OnNewsItemClickListener {
        void onItemClick(Article item);
    }

    private class NewsViewAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        private final List<Article> items;
        private final OnNewsItemClickListener onNewsItemClickListener;
        private final Context context;

        private NewsViewAdapter(Context context, List<Article> items, OnNewsItemClickListener onNewsItemClickListener) {
            this.context = context;
            this.items = items;
            this.onNewsItemClickListener = onNewsItemClickListener;
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_news_item, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

            final Article item = items.get(position);

            int sourceNavId = item.getSourceNavId();
            String source = newsConsts.getSourceNameTitle(sourceNavId);
            String title = item.getTitle().trim();
            String description = item.getDescription();
            String url = item.getPictureUrl();

            Date pubDate = item.getPubDate();
            @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            holder.sourceAndDate.setText(String.format("%s %s", format.format(pubDate), source));

            if (title.equals(description) || description == null || description.isEmpty()) {
                holder.description.setVisibility(View.GONE);
            } else {
                holder.description.setVisibility(View.VISIBLE);
                holder.description.setText(description.trim());
            }
            holder.title.setText(title);
            holder.thumb.setImageResource(0);
            if (url == null) {
                holder.thumb.setVisibility(View.GONE);
            } else {
                holder.thumb.setVisibility(View.VISIBLE);
                Glide.with(context).load(url).into(holder.thumb);
            }

            holder.itemView.setOnClickListener(v -> onNewsItemClickListener.onItemClick(item));

        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        void setItems(List<? extends Article> items) {
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


