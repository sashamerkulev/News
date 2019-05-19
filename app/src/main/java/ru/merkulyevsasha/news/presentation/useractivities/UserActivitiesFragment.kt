package ru.merkulyevsasha.news.presentation.useractivities

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_useractivities.buttonUp
import kotlinx.android.synthetic.main.fragment_useractivities.recyclerView
import kotlinx.android.synthetic.main.fragment_useractivities.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_useractivities.toolbar
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.news.NewsApp
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.presentation.articles.ArticlesFragment
import ru.merkulyevsasha.news.presentation.common.AppbarScrollExpander
import ru.merkulyevsasha.news.presentation.common.ColorThemeResolver
import ru.merkulyevsasha.news.presentation.common.MainActivityRouter
import ru.merkulyevsasha.news.presentation.common.ShowActionBarListener
import ru.merkulyevsasha.news.presentation.common.ToolbarCombinator
import ru.merkulyevsasha.news.presentation.common.newsadapter.NewsViewAdapter

class UserActivitiesFragment : Fragment(), UserActivitiesView {

    companion object {
        @JvmStatic
        val TAG = UserActivitiesFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): Fragment {
            val fragment = UserActivitiesFragment()
            fragment.arguments = Bundle()
            return fragment
        }
    }

    private var presenter: UserActivitiesPresenterImpl? = null
    private var combinator: ToolbarCombinator? = null
    private var showActionBarListener: ShowActionBarListener? = null

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
        if (context is ShowActionBarListener) {
            showActionBarListener = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_useractivities, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val savedState = savedInstanceState ?: arguments
        savedState?.apply {
            position = this.getInt(ArticlesFragment.KEY_POSITION, 0)
            expanded = this.getBoolean(ArticlesFragment.KEY_EXPANDED, true)
        }

        colorThemeResolver = ColorThemeResolver(TypedValue(), requireContext().theme)

        toolbar.setTitle(R.string.fragment_actions_title)
        toolbar.setTitleTextColor(colorThemeResolver.getThemeAttrColor(R.attr.actionBarTextColor))
        combinator?.combine(toolbar)

        appbarScrollExpander = AppbarScrollExpander(recyclerView, showActionBarListener)

        swipeRefreshLayout.setOnRefreshListener { presenter?.onRefresh() }
        initSwipeRefreshColorScheme()

        val serviceLocator = (requireActivity().application as NewsApp).getServiceLocator()
        val interactor = serviceLocator.get(ArticlesInteractor::class.java)
        presenter = UserActivitiesPresenterImpl(interactor)
        presenter?.bindView(this)


        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (position >= layoutManager.findFirstVisibleItemPosition()) {
                    position = layoutManager.findFirstVisibleItemPosition()
                    if (position > 5 && !swipeRefreshLayout.isRefreshing) {
                        buttonUp.visibility = View.VISIBLE
                    } else {
                        buttonUp.visibility = View.GONE
                    }
                } else {
                    position = layoutManager.findFirstVisibleItemPosition() - 1
                    buttonUp.visibility = View.GONE
                }
            }
        })

        buttonUp.setOnClickListener { _ ->
            layoutManager.scrollToPosition(0)
            position = 0
            buttonUp.visibility = View.GONE
        }

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
        Toast.makeText(requireContext(), "Ooops!", Toast.LENGTH_LONG).show()
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
        state.putInt(ArticlesFragment.KEY_POSITION, position)
        state.putBoolean(ArticlesFragment.KEY_EXPANDED, expanded)
    }

    private fun initSwipeRefreshColorScheme() {
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(colorThemeResolver.getThemeAttrColor(R.attr.colorAccent))
        swipeRefreshLayout.setColorSchemeColors(colorThemeResolver.getThemeAttrColor(R.attr.colorControlNormal))
    }
}