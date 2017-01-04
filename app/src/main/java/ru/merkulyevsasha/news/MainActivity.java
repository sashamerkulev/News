package ru.merkulyevsasha.news;

import android.app.PendingIntent;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;
import ru.merkulyevsasha.news.adapters.RecyclerViewAdapter;
import ru.merkulyevsasha.news.db.DatabaseHelper;
import ru.merkulyevsasha.news.loaders.HttpService;
import ru.merkulyevsasha.news.models.Const;
import ru.merkulyevsasha.news.models.ItemNews;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    public static final String KEY_NAV_ID = "navId";
    public static final String KEY_INTENT = "intent";

    public static final String KEY_POSITION = "position";
    public static final String KEY_REFRESHING = "refreshing";
    public static final String KEY_SEARCHTEXT = "searchtext";

    public static final int STATUS_FINISH = 1001;

    @State
    int mNavId;
    @State
    String mSearchText;

    private DatabaseHelper mHelper;
    private RecyclerViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

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


    private MenuItem mSearchItem;
    private SearchView mSearchView;

    private Const mConst;


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

//        outState.putInt(KEY_NAV_ID, mNavId);
//        outState.putString(KEY_SEARCHTEXT, mSearchText);
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
                searchViewText();            }
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

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                refreshNews();
            }
        });

        mHelper = DatabaseHelper.getInstance(DatabaseHelper.getDbPath(this));
        mConst = new Const();

        if (savedInstanceState == null) {
            mNavId = R.id.nav_all;
            List<ItemNews> items = getItemNews(mNavId);
            if (items.size() > 0) {
                mAdapter.Items = items;
                mAdapter.notifyDataSetChanged();
            } else {
                mRefreshLayout.setRefreshing(true);
                refreshNews();
            }
        } else {

//            mNavId = savedInstanceState.getInt(KEY_NAV_ID, R.id.nav_all);
//            mSearchText = savedInstanceState.getString(KEY_SEARCHTEXT);
            Icepick.restoreInstanceState(this, savedInstanceState);

            int position = savedInstanceState.getInt(KEY_POSITION, -1);
            boolean isRefreshing = savedInstanceState.getBoolean(KEY_REFRESHING, false);
            mRefreshLayout.setRefreshing(isRefreshing);

            if (mSearchText == null || mSearchText.isEmpty()){
                mAdapter.Items = getItemNews(mNavId);
                mAdapter.notifyDataSetChanged();
            } else {
                search(mSearchText);
            }

            if (position > 0) {
                mLayoutManager.scrollToPosition(position);
            }
        }
        setActivityTitle(mNavId);
    }

    private void searchViewText() {
        mSearchItem.expandActionView();
        mSearchView.setQuery(mSearchText, false);
    }


    private void setActivityTitle(int navId){
        int stringId = mConst.getTitleByNavId(navId);
        setTitle(stringId > 0 ? getResources().getString(stringId) : "");
    }

    private List<ItemNews> getItemNews(int navId){
        return navId == R.id.nav_all
                ? mHelper.selectAll()
                : mHelper.select(navId);
    }

    private void refreshNews(){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pi = createPendingResult(mNavId, notificationIntent, 0);
        startService(new Intent(this, HttpService.class)
                .putExtra(KEY_NAV_ID, mNavId).putExtra(KEY_INTENT, pi));
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
                    refreshNews();
                }
                return false;
            }
        });
        return true;
    }

    private void search(String searchText){
        List<ItemNews> items = mHelper.query(searchText);
        if (items.size() > 0) {
            mAdapter.Items = items;
            mAdapter.notifyDataSetChanged();
        } else{
            Snackbar.make(this.findViewById(R.id.content_main), R.string.search_nofound_message, Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query.length() < 5) {
            Snackbar.make(this.findViewById(R.id.content_main), R.string.search_validation_message, Snackbar.LENGTH_LONG)
                    .show();
            return false;
        }
        mSearchText = query;
        search(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.isEmpty()) {
            mAdapter.Items = getItemNews(mNavId);
            mAdapter.notifyDataSetChanged();
        }
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawer.closeDrawer(GravityCompat.START);

        setTitle(item.getTitle());
        mNavId = item.getItemId();
        List<ItemNews> items = getItemNews(mNavId);
        if (items.size() > 0) {
            mAdapter.Items = items;
            mAdapter.notifyDataSetChanged();
            mRecyclerView.scrollToPosition(0);
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == STATUS_FINISH){

            mAdapter.Items = getItemNews(mNavId);
            mAdapter.notifyDataSetChanged();

            mRefreshLayout.setRefreshing(false);
        }
    }

}
