package ru.merkulyevsasha.news.presentation.articles

import android.content.Context
import android.content.res.Resources
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_articles.recyclerView
import kotlinx.android.synthetic.main.fragment_articles.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_articles.toolbar
import kotlinx.android.synthetic.main.row_news_item.view.imageViewComment
import kotlinx.android.synthetic.main.row_news_item.view.imageViewDislike
import kotlinx.android.synthetic.main.row_news_item.view.imageViewLike
import kotlinx.android.synthetic.main.row_news_item.view.imageViewThumb
import kotlinx.android.synthetic.main.row_news_item.view.layoutButtonComment
import kotlinx.android.synthetic.main.row_news_item.view.layoutButtonDislike
import kotlinx.android.synthetic.main.row_news_item.view.layoutButtonLike
import kotlinx.android.synthetic.main.row_news_item.view.newsDateSource
import kotlinx.android.synthetic.main.row_news_item.view.newsDescription
import kotlinx.android.synthetic.main.row_news_item.view.newsTitle
import kotlinx.android.synthetic.main.row_news_item.view.textViewComment
import kotlinx.android.synthetic.main.row_news_item.view.textViewDislike
import kotlinx.android.synthetic.main.row_news_item.view.textViewLike
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.news.NewsApp
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.presentation.common.MainActivityRouter
import ru.merkulyevsasha.news.presentation.common.ToolbarCombinator
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ArticlesFragment : Fragment(), ArticlesView {

    companion object {
        @JvmStatic
        val TAG: String = ArticlesFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): Fragment {
            return ArticlesFragment()
        }
    }

    private var presenter: ArticlesPresenterImpl? = null
    private var combinator: ToolbarCombinator? = null

    private lateinit var adapter: NewsViewAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var typedValue : TypedValue
    private lateinit var currentTheme: Resources.Theme

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ToolbarCombinator) {
            combinator = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_articles, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        typedValue = TypedValue()
        currentTheme = requireContext().theme

        combinator?.combine(toolbar)

        swipeRefreshLayout.setOnRefreshListener { presenter?.onRefresh() }
        initSwipeRefreshColorScheme()

        layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = NewsViewAdapter(
            requireContext(),
            ArrayList()
        )
        recyclerView.adapter = adapter

        val serviceLocator = (requireActivity().application as NewsApp).getServiceLocator()
        val interactor = serviceLocator.get(ArticlesInteractor::class.java)
        presenter = ArticlesPresenterImpl(interactor)
        presenter?.bindView(this)
        presenter?.onFirstLoadArticles()
    }

    override fun onPause() {
        presenter?.unbindView()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        presenter?.bindView(this)
    }

    override fun onDestroyView() {
        presenter?.onDestroy()
        super.onDestroyView()
    }

    override fun showError() {
    }

    override fun showProgress() {
        swipeRefreshLayout.isRefreshing = true
    }

    override fun hideProgress() {
        swipeRefreshLayout.isRefreshing = false
    }

    override fun showItems(items: List<Article>) {
        adapter.setItems(items)
    }

    override fun updateItems(items: List<Article>) {
        adapter.updateItems(items)
    }

    override fun updateItem(item: Article) {
        adapter.updateItem(item)
    }

    override fun showArticleDetails(articleId: Int) {
        if (requireActivity() is MainActivityRouter) {
            (requireActivity() as MainActivityRouter).showArticleDetails(articleId)
        }
    }

    private fun initSwipeRefreshColorScheme() {
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getThemeAttrColor(R.attr.colorAccent))
        swipeRefreshLayout.setColorSchemeColors(getThemeAttrColor(R.attr.colorControlNormal))
    }

    private fun getThemeAttrColor(attrColor: Int): Int {
        currentTheme.resolveAttribute(attrColor, typedValue, true)
        return typedValue.data
    }

    private inner class NewsViewAdapter constructor(
        private val context: Context,
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

            if (title == description || description.isNullOrEmpty()) {
                holder.itemView.newsDescription.visibility = View.GONE
            } else {
                holder.itemView.newsDescription.visibility = View.VISIBLE
                holder.itemView.newsDescription.text = description.trim { it <= ' ' }
            }
            holder.itemView.newsTitle.text = title
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

            setAccentColorIf(item.isUserLiked, holder.itemView.textViewLike, holder.itemView.imageViewLike)
            setAccentColorIf(item.isUserDisliked, holder.itemView.textViewDislike, holder.itemView.imageViewDislike)
            setAccentColorIf(item.isUserCommented, holder.itemView.textViewComment, holder.itemView.imageViewComment)

            holder.itemView.setOnClickListener {
                val newItem = items[holder.adapterPosition]
                presenter?.onArticleCliked(newItem)
            }

            holder.itemView.layoutButtonLike.setOnClickListener {
                val newItem = items[holder.adapterPosition]
                presenter?.onLikeClicked(newItem)
            }

            holder.itemView.layoutButtonComment.setOnClickListener {
                val newItem = items[holder.adapterPosition]
                presenter?.onCommentClicked(newItem.articleId)
            }

            holder.itemView.layoutButtonDislike.setOnClickListener {
                val newItem = items[holder.adapterPosition]
                presenter?.onDislikeClicked(newItem)
            }

        }

        override fun getItemCount(): Int {
            return items.size
        }

        fun setItems(items: List<Article>) {
            this.items.clear()
            this.items.addAll(items)
            this.notifyDataSetChanged()
            //if (position > 0) layoutManager.scrollToPosition(position)
        }

        fun updateItems(items: List<Article>) {
            this.items.addAll(0, items) // TODO update existing items in the collection
            this.notifyDataSetChanged()
        }

        fun updateItem(item: Article) {
            val index = items.indexOfFirst { it.articleId == item.articleId }
            items[index] = item
            //this.notifyItemChanged(index)
            this.notifyDataSetChanged()
        }

        private fun setAccentColorIf(expression: Boolean, textView: TextView, imageView: ImageView) {
            if (expression) {
                val color = getThemeAttrColor(R.attr.black)
                textView.setTextColor(color)
                imageView.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            } else {
                val color = getThemeAttrColor(R.attr.separator)
                textView.setTextColor(color)
                imageView.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            }

        }

    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}