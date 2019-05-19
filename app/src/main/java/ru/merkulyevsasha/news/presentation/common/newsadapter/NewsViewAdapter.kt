package ru.merkulyevsasha.news.presentation.common.newsadapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.row_news_item.view.imageViewComment
import kotlinx.android.synthetic.main.row_news_item.view.imageViewDislike
import kotlinx.android.synthetic.main.row_news_item.view.imageViewLike
import kotlinx.android.synthetic.main.row_news_item.view.imageViewThumb
import kotlinx.android.synthetic.main.row_news_item.view.layoutButtonComment
import kotlinx.android.synthetic.main.row_news_item.view.layoutButtonDislike
import kotlinx.android.synthetic.main.row_news_item.view.layoutButtonLike
import kotlinx.android.synthetic.main.row_news_item.view.layoutButtonShare
import kotlinx.android.synthetic.main.row_news_item.view.newsDateSource
import kotlinx.android.synthetic.main.row_news_item.view.newsDescription
import kotlinx.android.synthetic.main.row_news_item.view.newsTitle
import kotlinx.android.synthetic.main.row_news_item.view.textViewComment
import kotlinx.android.synthetic.main.row_news_item.view.textViewDislike
import kotlinx.android.synthetic.main.row_news_item.view.textViewLike
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.presentation.common.ColorThemeResolver
import java.text.SimpleDateFormat
import java.util.*

class NewsViewAdapter constructor(
    private val context: Context,
    private val callbackClickHandler: CallbackClickHandler?,
    private val colorThemeResolver: ColorThemeResolver,
    private val items: MutableList<Article>
) : RecyclerView.Adapter<ItemViewHolder>() {

    private val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_news_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val item = items[position]

        val source = item.sourceName
        val title = item.title.trim()
        val description = item.description
        val url = item.pictureUrl

        val pubDate = item.pubDate
        holder.itemView.newsDateSource.text = String.format("%s %s", format.format(pubDate), source)

        initDescription(title, description, holder)

        holder.itemView.imageViewThumb.setImageResource(0)
        if (url.isNullOrEmpty()) {
            holder.itemView.imageViewThumb.visibility = View.GONE
        } else {
            holder.itemView.imageViewThumb.visibility = View.VISIBLE
            Glide.with(context).load(url).into(holder.itemView.imageViewThumb)
        }

        holder.itemView.textViewLike.text = item.usersLikeCount.toString()
        holder.itemView.textViewDislike.text = item.usersDislikeCount.toString()
        holder.itemView.textViewComment.text = item.usersCommentCount.toString()

        colorThemeResolver.setArticleActivityColor(item.isUserLiked, holder.itemView.textViewLike, holder.itemView.imageViewLike)
        colorThemeResolver.setArticleActivityColor(item.isUserDisliked, holder.itemView.textViewDislike, holder.itemView.imageViewDislike)
        colorThemeResolver.setArticleActivityColor(item.isUserCommented, holder.itemView.textViewComment, holder.itemView.imageViewComment)

        initClickListeners(holder)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItems(items: List<Article>) {
        this.items.clear()
        this.items.addAll(items)
        this.notifyDataSetChanged()
    }

    fun updateItems(items: List<Article>) {
        this.items.addAll(0, items) // TODO update existing items in the collection
        this.notifyDataSetChanged()
    }

    fun updateItem(item: Article) {
        val index = items.indexOfFirst { it.articleId == item.articleId }
        items[index] = item
        this.notifyDataSetChanged()
    }

    private fun initDescription(title: String, description: String?, holder: ItemViewHolder) {
        if (title == description || description.isNullOrEmpty()) {
            holder.itemView.newsDescription.visibility = View.GONE
        } else {
            holder.itemView.newsDescription.visibility = View.VISIBLE
            holder.itemView.newsDescription.text = description.trim { it <= ' ' }
        }
        holder.itemView.newsTitle.text = title
    }

    private fun initClickListeners(holder: ItemViewHolder) {
        holder.itemView.setOnClickListener {
            val newItem = items[holder.adapterPosition]
            callbackClickHandler?.onArticleCliked(newItem)
        }

        holder.itemView.layoutButtonLike.setOnClickListener {
            val newItem = items[holder.adapterPosition]
            callbackClickHandler?.onLikeClicked(newItem)
        }

        holder.itemView.layoutButtonComment.setOnClickListener {
            val newItem = items[holder.adapterPosition]
            callbackClickHandler?.onCommentClicked(newItem.articleId)
        }

        holder.itemView.layoutButtonDislike.setOnClickListener {
            val newItem = items[holder.adapterPosition]
            callbackClickHandler?.onDislikeClicked(newItem)
        }

        holder.itemView.layoutButtonShare.setOnClickListener {
            val newItem = items[holder.adapterPosition]
            callbackClickHandler?.onShareClicked(newItem)
        }
    }
}
