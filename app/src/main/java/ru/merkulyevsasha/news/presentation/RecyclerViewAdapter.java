package ru.merkulyevsasha.news.presentation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ru.merkulyevsasha.news.R;
import ru.merkulyevsasha.news.helpers.Const;
import ru.merkulyevsasha.news.pojos.ItemNews;

import static ru.merkulyevsasha.news.presentation.WebViewActivity.KEY_LINK;
import static ru.merkulyevsasha.news.presentation.WebViewActivity.KEY_TITLE;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder>{

    private final WeakReference<Activity> mRef;
    private final Const mConst;
    public List<ItemNews> Items;

    public RecyclerViewAdapter(Activity activity, List<ItemNews> items){
        Items = items;
        mRef = new WeakReference(activity);
        mConst = new Const();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new ItemViewHolder(view, new OnClickListener() {
            @Override
            public void onItemClick(int position) {

                Activity activity = mRef.get();
                if (activity != null) {
                    Intent link_intent = new Intent(activity, WebViewActivity.class);
                    ItemNews item = Items.get(position);
                    link_intent.putExtra(KEY_LINK, item.Link);

                    link_intent.putExtra(KEY_TITLE, getSourceNameTitle(item.SourceNavId));
                    activity.startActivity(link_intent);
                }
            }
        });
    }

    private String getSourceNameTitle(int navId) {
        Activity activity = mRef.get();
        if (activity == null)
            return "";
        int stringId = mConst.getTitleByNavId(navId);
        return stringId > 0 ? activity.getResources().getString(stringId) : "";
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        int sourceNavId = Items.get(position).SourceNavId;
        String source= getSourceNameTitle(sourceNavId);
        String title = Items.get(position).Title.trim();
        String description = Items.get(position).Description;
        Date pubDate = Items.get(position).PubDate;
        if (pubDate == null){
            holder.mTitle.setText(source + " " + title);
        } else {
            @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
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
