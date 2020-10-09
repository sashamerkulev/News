package ru.merkulyevsasha.coreandroid.common.newsadapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.merge_articles_buttons.view.imageViewComment
import kotlinx.android.synthetic.main.merge_articles_buttons.view.imageViewDislike
import kotlinx.android.synthetic.main.merge_articles_buttons.view.imageViewLike
import kotlinx.android.synthetic.main.merge_articles_buttons.view.layoutButtonComment
import kotlinx.android.synthetic.main.merge_articles_buttons.view.layoutButtonDislike
import kotlinx.android.synthetic.main.merge_articles_buttons.view.layoutButtonLike
import kotlinx.android.synthetic.main.merge_articles_buttons.view.layoutButtonShare
import kotlinx.android.synthetic.main.merge_articles_buttons.view.textViewComment
import kotlinx.android.synthetic.main.merge_articles_buttons.view.textViewDislike
import kotlinx.android.synthetic.main.merge_articles_buttons.view.textViewLike
import kotlinx.android.synthetic.main.merge_title_layout.view.imageViewThumb
import kotlinx.android.synthetic.main.merge_title_layout.view.layoutSourceName
import kotlinx.android.synthetic.main.merge_title_layout.view.newsDateSource
import kotlinx.android.synthetic.main.merge_title_layout.view.newsTitle
import kotlinx.android.synthetic.main.row_news.view.newsDescription
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.coreandroid.R
import ru.merkulyevsasha.coreandroid.common.ColorThemeResolver
import java.text.SimpleDateFormat
import java.util.*

class NewsViewAdapter constructor(
    private val context: Context,
    private val articleCallbackClickHandler: ArticleClickCallbackHandler?,
    private val sourceArticleClickCallbackHandler: SourceArticleClickCallbackHandler?,
    private val likeCallbackClickHandler: ArticleLikeCallbackClickHandler?,
    private val commentCallbackClickHandler: ArticleCommentArticleCallbackClickHandler?,
    private val shareCallbackClickHandler: ArticleShareCallbackClickHandler?,
    private val colorThemeResolver: ColorThemeResolver,
    private val items: MutableList<Article>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_news, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).bind(position)
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
        if (items.isEmpty()) return
        var count = 0
        val addedItems = ArrayList<Article>(items.size)
        val mapArticles = this.items.map { it.articleId }
        for (item in items) {
            if (mapArticles.contains(item.articleId)) {
                val oldItemIndex = this.items.indexOfFirst { it.articleId == item.articleId }
                this.items[oldItemIndex] = item
                this.notifyItemChanged(oldItemIndex)
            } else {
                addedItems.add(item)
                count++
            }
        }
        if (count > 0) {
            this.items.addAll(0, addedItems)
            this.notifyItemRangeChanged(0, count)
        }
    }

    fun updateItem(item: Article) {
        val index = items.indexOfFirst { it.articleId == item.articleId }
        items[index] = item
        this.notifyItemChanged(index)
    }

    @SuppressLint("SetTextI18n")
    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {

            val item = items[position]

            val source = item.sourceName
            val title = item.title.trim()
            val description = item.description
            val url = item.pictureUrl

            val pubDate = item.pubDate
            itemView.newsDateSource.text = "${format.format(pubDate)} $source"

            initDescription(title, description)

            itemView.imageViewThumb.setImageResource(0)
            if (url.isNullOrEmpty()) {
                itemView.imageViewThumb.visibility = View.GONE
            } else {
                itemView.imageViewThumb.visibility = View.VISIBLE
                Glide.with(context).load(url).into(itemView.imageViewThumb)
            }

            itemView.textViewLike.text = item.usersLikeCount.toString()
            itemView.textViewDislike.text = item.usersDislikeCount.toString()
            itemView.textViewComment.text = item.usersCommentCount.toString()

            colorThemeResolver.setArticleActivityColor(item.isUserLiked, itemView.textViewLike, itemView.imageViewLike)
            colorThemeResolver.setArticleActivityColor(item.isUserDisliked, itemView.textViewDislike, itemView.imageViewDislike)
            colorThemeResolver.setArticleActivityColor(item.isUserCommented, itemView.textViewComment, itemView.imageViewComment)

            initClickListeners()
        }

        private fun initDescription(title: String, description: String?) {
            if (title == description || description.isNullOrEmpty()) {
                itemView.newsDescription.visibility = View.GONE
            } else {
                itemView.newsDescription.visibility = View.VISIBLE
                itemView.newsDescription.text = description.trim { it <= ' ' }
            }
            itemView.newsTitle.text = title
        }

        private fun initClickListeners() {
            itemView.setOnClickListener {
                val newItem = items[adapterPosition]
                articleCallbackClickHandler?.onArticleCliked(newItem)
            }

            itemView.layoutSourceName.setOnClickListener {
                val newItem = items[adapterPosition]
                sourceArticleClickCallbackHandler?.onSourceArticleCliked(newItem.sourceId, newItem.sourceName)
            }

            itemView.layoutButtonLike.setOnClickListener {
                val newItem = items[adapterPosition]
                likeCallbackClickHandler?.onArticleLikeClicked(newItem)
            }

            itemView.layoutButtonComment.setOnClickListener {
                val newItem = items[adapterPosition]
                commentCallbackClickHandler?.onArticleCommentArticleClicked(newItem.articleId)
            }

            itemView.layoutButtonDislike.setOnClickListener {
                val newItem = items[adapterPosition]
                likeCallbackClickHandler?.onArticleDislikeClicked(newItem)
            }

            itemView.layoutButtonShare.setOnClickListener {
                val newItem = items[adapterPosition]
                shareCallbackClickHandler?.onArticleShareClicked(newItem)
            }
        }
    }

}
