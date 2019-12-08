package ru.merkulyevsasha.articlecomments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.merge_articles_buttons.view.imageViewDislike
import kotlinx.android.synthetic.main.merge_articles_buttons.view.imageViewLike
import kotlinx.android.synthetic.main.merge_articles_buttons.view.layoutButtonDislike
import kotlinx.android.synthetic.main.merge_articles_buttons.view.layoutButtonLike
import kotlinx.android.synthetic.main.merge_articles_buttons.view.layoutButtonShare
import kotlinx.android.synthetic.main.merge_articles_buttons.view.textViewDislike
import kotlinx.android.synthetic.main.merge_articles_buttons.view.textViewLike
import kotlinx.android.synthetic.main.merge_title_layout.view.imageViewThumb
import kotlinx.android.synthetic.main.merge_title_layout.view.newsDateSource
import kotlinx.android.synthetic.main.merge_title_layout.view.newsTitle
import kotlinx.android.synthetic.main.row_articlecomment.view.commentImageViewDislike
import kotlinx.android.synthetic.main.row_articlecomment.view.commentImageViewLike
import kotlinx.android.synthetic.main.row_articlecomment.view.commentTextViewDislike
import kotlinx.android.synthetic.main.row_articlecomment.view.commentTextViewLike
import kotlinx.android.synthetic.main.row_articlecomment.view.commenter
import kotlinx.android.synthetic.main.row_articlecomment.view.imageViewCommentAvatar
import kotlinx.android.synthetic.main.row_articlecomment.view.layoutCommentButtonDislike
import kotlinx.android.synthetic.main.row_articlecomment.view.layoutCommentButtonLike
import kotlinx.android.synthetic.main.row_articlecomment.view.layoutCommentButtonShare
import kotlinx.android.synthetic.main.row_articlecomment.view.textViewComment
import kotlinx.android.synthetic.main.row_articlecomment.view.textViewCommentDate
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComment
import ru.merkulyevsasha.core.models.ArticleOrComment
import ru.merkulyevsasha.coreandroid.common.AvatarShower
import ru.merkulyevsasha.coreandroid.common.ColorThemeResolver
import ru.merkulyevsasha.coreandroid.common.newsadapter.ArticleCommentLikeCallbackClickHandler
import ru.merkulyevsasha.coreandroid.common.newsadapter.ArticleCommentShareCallbackClickHandler
import ru.merkulyevsasha.coreandroid.common.newsadapter.ArticleLikeCallbackClickHandler
import ru.merkulyevsasha.coreandroid.common.newsadapter.ArticleShareCallbackClickHandler
import java.text.SimpleDateFormat
import java.util.*

class CommentsViewAdapter constructor(
    private val context: Context,
    private val likeCallbackClickHandler: ArticleLikeCallbackClickHandler?,
    private val shareCallbackClickHandler: ArticleShareCallbackClickHandler?,
    private val commentLikeCallbackClickHandler: ArticleCommentLikeCallbackClickHandler?,
    private val commentShareCallbackClickHandler: ArticleCommentShareCallbackClickHandler?,
    private val colorThemeResolver: ColorThemeResolver,
    private val items: MutableList<ArticleOrComment>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ARTICLE_DETAIL_VIEW_TYPE = 1
        const val COMMENT_VIEW_TYPE = 2
    }

    private val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    private val avatarShower = AvatarShower()

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) ARTICLE_DETAIL_VIEW_TYPE else COMMENT_VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ARTICLE_DETAIL_VIEW_TYPE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_articlecomment_article, parent, false)
            return ArticleViewHolder(view)
        }
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_articlecomment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ArticleViewHolder) holder.bind(position)
        else (holder as CommentViewHolder).bind(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItems(items: List<ArticleOrComment>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun updateArticleItem(item: Article) {
        this.items[0] = item
        notifyItemChanged(0)
    }

    fun updateCommentItem(item: ArticleComment) {
        var index = this.items.filterIsInstance<ArticleComment>().indexOfFirst { item.commentId == it.commentId }
        if (index >= 0) {
            this.items[++index] = item // skip Article
            notifyItemChanged(index)
        } else {
            this.items.add(1, item)
            notifyItemInserted(1)
        }
    }

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val item = items[position] as Article

            val source = item.sourceName
            val title = item.title.trim()
            val url = item.pictureUrl

            val pubDate = item.pubDate
            itemView.newsDateSource.text = String.format("%s %s", format.format(pubDate), source)

            itemView.imageViewThumb.setImageResource(0)
            if (url.isNullOrEmpty()) {
                itemView.imageViewThumb.visibility = View.GONE
            } else {
                itemView.imageViewThumb.visibility = View.VISIBLE
                Glide.with(context).load(url).into(itemView.imageViewThumb)
            }

            itemView.newsTitle.text = title

            itemView.textViewLike.text = item.usersLikeCount.toString()
            itemView.textViewDislike.text = item.usersDislikeCount.toString()
//            holder.itemView.textViewComment.text = item.usersCommentCount.toString()

            colorThemeResolver.setArticleActivityColor(item.isUserLiked, itemView.textViewLike, itemView.imageViewLike)
            colorThemeResolver.setArticleActivityColor(item.isUserDisliked, itemView.textViewDislike, itemView.imageViewDislike)
//            colorThemeResolver.setArticleActivityColor(item.isUserCommented, holder.itemView.textViewComment, holder.itemView.imageViewComment)

            itemView.layoutButtonLike.setOnClickListener {
                likeCallbackClickHandler?.onArticleLikeClicked(items[adapterPosition] as Article)
            }

            itemView.layoutButtonDislike.setOnClickListener {
                likeCallbackClickHandler?.onArticleDislikeClicked(items[adapterPosition] as Article)
            }

            itemView.layoutButtonShare.setOnClickListener {
                shareCallbackClickHandler?.onArticleShareClicked(items[adapterPosition] as Article)
            }
        }
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val item = items[position] as ArticleComment
            itemView.commenter.text = item.userName
            itemView.textViewComment.text = item.comment
            val pubDate = item.pubDate
            itemView.textViewCommentDate.text = format.format(pubDate)

            avatarShower.showWithCache(context, R.drawable.ic_avatar_empty, item.avatarUrl, item.authorization, itemView.imageViewCommentAvatar)

            itemView.commentTextViewLike.text = item.usersLikeCount.toString()
            itemView.commentTextViewDislike.text = item.usersDislikeCount.toString()

            colorThemeResolver.setArticleActivityColor(item.isUserLiked, itemView.commentTextViewLike, itemView.commentImageViewLike)
            colorThemeResolver.setArticleActivityColor(item.isUserDisliked,
                itemView.commentTextViewDislike, itemView.commentImageViewDislike)

            itemView.layoutCommentButtonLike.setOnClickListener {
                commentLikeCallbackClickHandler?.onArticleCommentLikeClicked(items[adapterPosition] as ArticleComment)
            }

            itemView.layoutCommentButtonDislike.setOnClickListener {
                commentLikeCallbackClickHandler?.onArticleCommentDislikeClicked(items[adapterPosition] as ArticleComment)
            }

            itemView.layoutCommentButtonShare.setOnClickListener {
                commentShareCallbackClickHandler?.onArticleCommentShareClicked(items[adapterPosition] as ArticleComment)
            }
        }
    }
}

