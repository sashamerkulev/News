package ru.merkulyevsasha.news.presentation.articles

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_articles.recyclerView
import kotlinx.android.synthetic.main.fragment_articles.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_articles.toolbar
import kotlinx.android.synthetic.main.row_news_item.view.imageViewThumb
import kotlinx.android.synthetic.main.row_news_item.view.newsDateSource
import kotlinx.android.synthetic.main.row_news_item.view.newsDescription
import kotlinx.android.synthetic.main.row_news_item.view.newsTitle
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.news.NewsApp
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.presentation.common.ToolbarCombinator
import java.text.SimpleDateFormat

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
        combinator?.combine(toolbar)

        swipeRefreshLayout.setOnRefreshListener { presenter?.onRefresh() }

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

    override fun showItems(items: List<Article>) {
        adapter.setItems(items)
    }

    override fun showError() {
    }


    private inner class NewsViewAdapter constructor(
        private val context: Context,
        private val items: MutableList<Article>
    ) : RecyclerView.Adapter<ItemViewHolder>() {

        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("dd/MM/yyyy HH:mm")

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

            holder.itemView.setOnClickListener {
                val newItem = items[holder.adapterPosition]
                presenter?.onArticleCliked(newItem)
            }

        }

        override fun getItemCount(): Int {
            return items.size
        }

        internal fun setItems(items: List<Article>) {
            this.items.clear()
            this.items.addAll(items)
            this.notifyDataSetChanged()
            //if (position > 0) layoutManager.scrollToPosition(position)
        }

    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}