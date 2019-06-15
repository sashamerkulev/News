package ru.merkulyevsasha.news.presentation.articles

import android.content.Context
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_articles.buttonUp
import kotlinx.android.synthetic.main.fragment_articles.recyclerView
import kotlinx.android.synthetic.main.fragment_articles.swipeRefreshLayout
import ru.merkulyevsasha.RequireServiceLocator
import ru.merkulyevsasha.core.ServiceLocator
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.routers.MainActivityRouter
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.presentation.common.AppbarScrollExpander
import ru.merkulyevsasha.news.presentation.common.ColorThemeResolver
import ru.merkulyevsasha.news.presentation.common.ShowActionBarListener
import ru.merkulyevsasha.news.presentation.common.ToolbarCombinator
import ru.merkulyevsasha.news.presentation.common.newsadapter.NewsViewAdapter

class ArticlesFragment : Fragment(), ArticlesView, RequireServiceLocator {

    companion object {

        const val MAX_POSITION = 5
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

    private lateinit var toolbar: Toolbar
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var appbarLayout: AppBarLayout

    private lateinit var serviceLocator: ServiceLocator
    private var presenter: ArticlesPresenterImpl? = null
    private var combinator: ToolbarCombinator? = null

    private lateinit var adapter: NewsViewAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var colorThemeResolver: ColorThemeResolver

    private lateinit var appbarScrollExpander: AppbarScrollExpander
    private var expanded = true
    private var position = 0

    override fun setServiceLocator(serviceLocator: ServiceLocator) {
        this.serviceLocator = serviceLocator
    }

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

        toolbar = view.findViewById(R.id.toolbar)
        collapsingToolbarLayout = view.findViewById(R.id.collapsinngToolbarLayout)
        appbarLayout = view.findViewById(R.id.appbarLayout)

        toolbar.setTitle(R.string.fragment_articles_title)
        toolbar.setTitleTextColor(colorThemeResolver.getThemeAttrColor(R.attr.actionBarTextColor))
        collapsingToolbarLayout.isTitleEnabled = false;
        combinator?.bindToolbar(toolbar)

        appbarScrollExpander = AppbarScrollExpander(recyclerView, object : ShowActionBarListener {
            override fun onShowActionBar(show: Boolean) {
                appbarLayout.setExpanded(show)
            }
        })

        swipeRefreshLayout.setOnRefreshListener { presenter?.onRefresh() }
        initSwipeRefreshColorScheme()

        val interactor = serviceLocator.get(ArticlesInteractor::class.java)
        presenter = ArticlesPresenterImpl(interactor, serviceLocator.get(MainActivityRouter::class.java))
        presenter?.bindView(this)

        initRecyclerView()
        initBottomUp()

        presenter?.onFirstLoad()
    }

    private fun initBottomUp() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (position >= layoutManager.findFirstVisibleItemPosition()) {
                    position = layoutManager.findFirstVisibleItemPosition()
                    if (position > MAX_POSITION && !swipeRefreshLayout.isRefreshing) {
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
        buttonUp.setOnClickListener {
            layoutManager.scrollToPosition(0)
            position = 0
            buttonUp.visibility = View.GONE
            appbarLayout.setExpanded(true)
        }
    }

    private fun initRecyclerView() {
        layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = NewsViewAdapter(
            requireContext(),
            presenter,
            presenter,
            presenter,
            presenter,
            colorThemeResolver,
            ArrayList()
        )
        recyclerView.adapter = adapter
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
        combinator?.unbindToolbar()
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

    private fun saveFragmentState(state: Bundle) {
        position = layoutManager.findFirstVisibleItemPosition()
        state.putInt(KEY_POSITION, position)
        state.putBoolean(KEY_EXPANDED, expanded)
    }

    private fun initSwipeRefreshColorScheme() {
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(colorThemeResolver.getThemeAttrColor(ru.merkulyevsasha.news.R.attr.colorAccent))
        swipeRefreshLayout.setColorSchemeColors(colorThemeResolver.getThemeAttrColor(ru.merkulyevsasha.news.R.attr.colorControlNormal))
    }
}