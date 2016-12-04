package ru.merkulyevsasha.news;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.Loader;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ru.merkulyevsasha.news.adapters.RecyclerViewAdapter;
import ru.merkulyevsasha.news.db.DatabaseHelper;
import ru.merkulyevsasha.news.loaders.LoaderCallbacks;
import ru.merkulyevsasha.news.models.ItemNews;

import static ru.merkulyevsasha.news.loaders.LoaderCallbacks.HTTP_LOADER_ID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    private Loader<List<ItemNews>> mHttpLoader;
    private LoaderCallbacks mLoaderCallbacks;
    private int mNavId;
    private DatabaseHelper mHelper;
    private RecyclerViewAdapter mAdapter;

    @Bind(R.id.refreshLayout)
    public SwipeRefreshLayout mRefreshLayout;

    @Bind(R.id.drawer_layout)
    public DrawerLayout mDrawer;

    @Bind(R.id.nav_view)
    public NavigationView mNavigationView;

    @Bind(R.id.recyclerView)
    public RecyclerView mRecyclerView;

    @Bind(R.id.toolbar)
    public Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        setTitle(R.string.news_all);

        mNavId = R.id.nav_all;

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new RecyclerViewAdapter(this, new ArrayList<ItemNews>());
        mRecyclerView.setAdapter(mAdapter);

        mLoaderCallbacks = new LoaderCallbacks(this, mAdapter);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                startHttpLoader(mNavId, false);
            }
        });

        mHelper = DatabaseHelper.getInstance(this);
        List<ItemNews> items = mHelper.selectAll();
        if (items.size() > 0) {
            mAdapter.Items = items;
            mAdapter.notifyDataSetChanged();
        }
        else {
            mRefreshLayout.setRefreshing(true);
            startHttpLoader(mNavId, true);
        }
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
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query.length() < 5) {
            Snackbar.make(this.findViewById(R.id.content_main), R.string.search_validation_message, Snackbar.LENGTH_LONG)
                    .show();
            return false;
        }
        List<ItemNews> items = mHelper.query(query);
        if (items.size() > 0) {
            mAdapter.Items = items;
            mAdapter.notifyDataSetChanged();
        } else{
            Snackbar.make(this.findViewById(R.id.content_main), R.string.search_nofound_message, Snackbar.LENGTH_LONG)
                    .show();
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawer.closeDrawer(GravityCompat.START);

        setTitle(item.getTitle());
        mNavId = item.getItemId();
        List<ItemNews> items = mNavId == R.id.nav_all? mHelper.selectAll() : mHelper.select(mNavId);
        if (items.size() > 0) {
            mAdapter.Items = items;
            mAdapter.notifyDataSetChanged();
            mRecyclerView.scrollToPosition(0);
        }

        return true;
    }

    private void startHttpLoader(int navId, boolean forceLoad)
    {
        Bundle args = new Bundle();
        args.putInt("navId", navId);
        if (mHttpLoader == null) {
            mHttpLoader = getSupportLoaderManager().initLoader(HTTP_LOADER_ID, args, mLoaderCallbacks);
        } else {
            mHttpLoader = getSupportLoaderManager().restartLoader(HTTP_LOADER_ID, args, mLoaderCallbacks);
        }
        if (forceLoad) {
            mHttpLoader.forceLoad();
        } else {
            mHttpLoader.onContentChanged();
        }
    }
}
