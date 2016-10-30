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
import java.util.List;

import ru.merkulyevsasha.news.R;
import ru.merkulyevsasha.news.WebViewActivity;
import ru.merkulyevsasha.news.models.ItemNews;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder>{

    private Activity mActivity;
    public List<ItemNews> Items;

    public RecyclerViewAdapter(Activity activity, List<ItemNews> items){
        Items = items;
        mActivity = activity;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        ItemViewHolder holder = new ItemViewHolder(view, new OnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent link_intent = new Intent(mActivity, WebViewActivity.class);
                ItemNews item = Items.get(position);
                link_intent.putExtra("link", item.Link);
                mActivity.startActivity(link_intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        String title = Items.get(position).Title.trim();
        String description = Items.get(position).Description;
        Date pubDate = Items.get(position).PubDate;
        if (pubDate == null){
            holder.mTitle.setText(title);
        } else {
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            holder.mTitle.setText(format.format(pubDate) + " " + title);
        }
        holder.mDescription.setText(description == null ? "" : description.trim());
    }

    @Override
    public int getItemCount() {
        return Items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView mTitle;
        private TextView mDescription;

        public ItemViewHolder(View itemView, final OnClickListener clickListener) {
            super(itemView);
            mTitle = (TextView)itemView.findViewById(R.id.news_title);
            mDescription = (TextView)itemView.findViewById(R.id.news_description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(v, getPosition());
                }
            });
        }
    }

    public interface OnClickListener {
        void onItemClick(View v, int position);
    }

}
