package ru.merkulyevsasha.news.loaders;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.List;

import ru.merkulyevsasha.news.R;
import ru.merkulyevsasha.news.adapters.RecyclerViewAdapter;
import ru.merkulyevsasha.news.models.ItemNews;

public class LoaderCallbacks implements LoaderManager.LoaderCallbacks<List<ItemNews>> {

    public static final int HTTP_LOADER_ID = 1;

    private Activity mContext;
    private RecyclerViewAdapter mAdapter;

    public LoaderCallbacks(Activity context, RecyclerViewAdapter adapter){
        mContext = context;
        mAdapter = adapter;
    }

    @Override
    public Loader<List<ItemNews>> onCreateLoader(int id, Bundle args) {
        return new NewsHttpLoader(mContext, args);
    }

    @Override
    public void onLoadFinished(Loader<List<ItemNews>> loader, List<ItemNews> data) {
        if (data != null) {
            mAdapter.Items = data;
            mAdapter.notifyDataSetChanged();
        }
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) mContext.findViewById(R.id.refreshLayout);
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<List<ItemNews>> loader) {
    }

}
