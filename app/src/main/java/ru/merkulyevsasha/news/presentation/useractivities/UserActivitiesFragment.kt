package ru.merkulyevsasha.news.presentation.useractivities

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_useractivities.recyclerView
import kotlinx.android.synthetic.main.fragment_useractivities.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_useractivities.toolbar
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.news.NewsApp
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.presentation.common.ColorThemeResolver
import ru.merkulyevsasha.news.presentation.common.MainActivityRouter
import ru.merkulyevsasha.news.presentation.common.ToolbarCombinator
import ru.merkulyevsasha.news.presentation.common.newsadapter.NewsViewAdapter

class UserActivitiesFragment : Fragment(), UserActivitiesView {

    companion object {
        @JvmStatic
        val TAG = UserActivitiesFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): Fragment {
            return UserActivitiesFragment()
        }
    }

    private var presenter: UserActivitiesPresenterImpl? = null
    private var combinator: ToolbarCombinator? = null

    private lateinit var adapter: NewsViewAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var colorThemeResolver: ColorThemeResolver

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ToolbarCombinator) {
            combinator = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_useractivities, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        colorThemeResolver = ColorThemeResolver(TypedValue(), requireContext().theme)

        combinator?.combine(toolbar)

        swipeRefreshLayout.setOnRefreshListener { presenter?.onRefresh() }
        initSwipeRefreshColorScheme()

        val serviceLocator = (requireActivity().application as NewsApp).getServiceLocator()
        val interactor = serviceLocator.get(ArticlesInteractor::class.java)
        presenter = UserActivitiesPresenterImpl(interactor)
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
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(colorThemeResolver.getThemeAttrColor(R.attr.colorAccent))
        swipeRefreshLayout.setColorSchemeColors(colorThemeResolver.getThemeAttrColor(R.attr.colorControlNormal))
    }
}