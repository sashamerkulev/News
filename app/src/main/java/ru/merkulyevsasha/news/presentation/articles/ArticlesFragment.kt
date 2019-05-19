package ru.merkulyevsasha.news.presentation.articles

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_articles.appbarLayout
import kotlinx.android.synthetic.main.fragment_articles.recyclerView
import kotlinx.android.synthetic.main.fragment_articles.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_articles.toolbar
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.news.NewsApp
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.presentation.common.AppbarScrollExpander
import ru.merkulyevsasha.news.presentation.common.ColorThemeResolver
import ru.merkulyevsasha.news.presentation.common.MainActivityRouter
import ru.merkulyevsasha.news.presentation.common.ToolbarCombinator
import ru.merkulyevsasha.news.presentation.common.newsadapter.NewsViewAdapter

class ArticlesFragment : Fragment(), ArticlesView {

    companion object {

        const val KEY_POSITION = "key_position"
        const val KEY_EXPANDED = "key_expanded"

        @JvmStatic
        val TAG: String = ArticlesFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): Fragment {
            val fragment = ArticlesFragment()
            fragment.arguments = Bundle()
            return fragment
        }
    }

    private var presenter: ArticlesPresenterImpl? = null
    private var combinator: ToolbarCombinator? = null

    private lateinit var adapter: NewsViewAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var colorThemeResolver: ColorThemeResolver

    private lateinit var appbarScrollExpander: AppbarScrollExpander
    private var expanded = true
    private var position = 0

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

        val savedState = savedInstanceState ?: arguments
        savedState?.apply {
            position = this.getInt(KEY_POSITION, 0)
            expanded = this.getBoolean(KEY_EXPANDED, true)
        }

        colorThemeResolver = ColorThemeResolver(TypedValue(), requireContext().theme)

        combinator?.combine(toolbar)

        appbarScrollExpander = AppbarScrollExpander(recyclerView, appbarLayout)
        appbarScrollExpander.expanded = expanded

        swipeRefreshLayout.setOnRefreshListener { presenter?.onRefresh() }
        initSwipeRefreshColorScheme()

        val serviceLocator = (requireActivity().application as NewsApp).getServiceLocator()
        val interactor = serviceLocator.get(ArticlesInteractor::class.java)
        presenter = ArticlesPresenterImpl(interactor)
        presenter?.bindView(this)

        layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = NewsViewAdapter(
            requireContext(),
            presenter,
            colorThemeResolver,
            ArrayList()
        )
        recyclerView.adapter = adapter

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        saveFragmentState(outState)
    }

    override fun onDestroyView() {
        presenter?.onDestroy()
        saveFragmentState(arguments ?: Bundle())
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
        layoutManager.scrollToPosition(position)
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

    private fun saveFragmentState(state: Bundle) {
        position = layoutManager.findFirstVisibleItemPosition()
        state.putInt(KEY_POSITION, position)
        state.putBoolean(KEY_EXPANDED, expanded)
    }

    private fun initSwipeRefreshColorScheme() {
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(colorThemeResolver.getThemeAttrColor(R.attr.colorAccent))
        swipeRefreshLayout.setColorSchemeColors(colorThemeResolver.getThemeAttrColor(R.attr.colorControlNormal))
    }
}