package ru.merkulyevsasha.news.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.merkulyevsasha.news.R;
import ru.merkulyevsasha.news.WebViewActivity;
import ru.merkulyevsasha.news.models.ItemNews;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder>{

    private final Activity mActivity;
    private final Map<Integer, Integer> mSources = new HashMap<Integer, Integer>();
    public List<ItemNews> Items;

    public RecyclerViewAdapter(Activity activity, List<ItemNews> items){
        Items = items;
        mActivity = activity;

        mSources.put(R.id.nav_lenta, R.string.news_lenta);
        mSources.put(R.id.nav_rbc, R.string.news_rbc);
        mSources.put(R.id.nav_wot, R.string.wot);
        //mSources.put(R.id.nav_topwar, R.string.news_topwar);
        mSources.put(R.id.nav_interfax, R.string.news_interfax);

        mSources.put(R.id.nav_vesti, R.string.news_vesti);
        mSources.put(R.id.nav_rt, R.string.news_rt);
//        mSources.put(R.id.nav_inosmi, R.string.news_inosmi);
//        mSources.put(R.id.nav_ria, R.string.news_rianovosti);
        mSources.put(R.id.nav_mixednews, R.string.news_mixednews);

        mSources.put(R.id.nav_rg, R.string.news_rg);
        mSources.put(R.id.nav_ng, R.string.news_ng);
        mSources.put(R.id.nav_kp, R.string.news_kp);
        mSources.put(R.id.nav_km, R.string.news_km);
        mSources.put(R.id.nav_aftershock, R.string.news_aftershock);

        mSources.put(R.id.nav_odnako, R.string.news_odnako);
        mSources.put(R.id.nav_aif, R.string.news_aif);
        mSources.put(R.id.nav_bbcrusshian, R.string.news_bbc_russian);
        mSources.put(R.id.nav_tass, R.string.news_tass);
        mSources.put(R.id.nav_nauka, R.string.news_nauka);

        mSources.put(R.id.nav_politikaru, R.string.news_politikaru);
        mSources.put(R.id.nav_mk, R.string.news_mk);
        mSources.put(R.id.nav_cnews, R.string.news_cnews);
        mSources.put(R.id.nav_mailru, R.string.news_mailru);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new ItemViewHolder(view, new OnClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent link_intent = new Intent(mActivity, WebViewActivity.class);
                ItemNews item = Items.get(position);
                link_intent.putExtra("link", item.Link);
                mActivity.startActivity(link_intent);
            }
        });
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        int sourceNavId = Items.get(position).SourceNavId;
        String source= "";
        if (mSources.containsKey(sourceNavId)) {
            int stringId = mSources.get(sourceNavId);
            source = mActivity.getResources().getString(stringId);
        }
        String title = Items.get(position).Title.trim();
        String description = Items.get(position).Description;
        Date pubDate = Items.get(position).PubDate;
        if (pubDate == null){
            holder.mTitle.setText(source + " " + title);
        } else {
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            holder.mTitle.setText(source + " " + format.format(pubDate) + " " + title);
        }
        holder.mDescription.setText(description == null ? "" : description.trim());
    }

    @Override
    public int getItemCount() {
        return Items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        private final TextView mTitle;
        private final TextView mDescription;

        public ItemViewHolder(View itemView, final OnClickListener clickListener) {
            super(itemView);
            mTitle = (TextView)itemView.findViewById(R.id.news_title);
            mDescription = (TextView)itemView.findViewById(R.id.news_description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    public interface OnClickListener {
        void onItemClick(int position);
    }

}
