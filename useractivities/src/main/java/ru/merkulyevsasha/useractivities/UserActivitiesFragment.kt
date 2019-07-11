package ru.merkulyevsasha.useractivities

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.fragment_useractivities.adView
import kotlinx.android.synthetic.main.fragment_useractivities.buttonUp
import kotlinx.android.synthetic.main.fragment_useractivities.recyclerView
import kotlinx.android.synthetic.main.fragment_useractivities.swipeRefreshLayout
import ru.merkulyevsasha.core.NewsDistributor
import ru.merkulyevsasha.core.RequireServiceLocator
import ru.merkulyevsasha.core.ServiceLocator
import ru.merkulyevsasha.coreandroid.common.AdViewHelper
import ru.merkulyevsasha.coreandroid.common.AppbarScrollExpander
import ru.merkulyevsasha.coreandroid.common.ColorThemeResolver
import ru.merkulyevsasha.coreandroid.common.ShowActionBarListener
import ru.merkulyevsasha.coreandroid.common.ToolbarCombinator
import ru.merkulyevsasha.coreandroid.common.newsadapter.NewsViewAdapter
import ru.merkulyevsasha.core.domain.ArticleCommentsInteractor
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.routers.MainActivityRouter

class UserActivitiesFragment : Fragment(), UserActivitiesView, RequireServiceLocator {

    companion object {
        private const val MAX_POSITION = 5
        private const val KEY_POSITION = "key_position"
        private const val KEY_EXPANDED = "key_expanded"

        @JvmStatic
        val TAG = UserActivitiesFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): Fragment {
            val fragment = UserActivitiesFragment()
            fragment.arguments = Bundle()
            return fragment
        }
    }

    private lateinit var toolbar: Toolbar
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var appbarLayout: AppBarLayout

    private lateinit var serviceLocator: ServiceLocator
    private var presenter: UserActivitiesPresenterImpl? = null
    private var combinator: ru.merkulyevsasha.coreandroid.common.ToolbarCombinator? = null

    private lateinit var adapter: ru.merkulyevsasha.coreandroid.common.newsadapter.NewsViewAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var colorThemeResolver: ru.merkulyevsasha.coreandroid.common.ColorThemeResolver

    private lateinit var appbarScrollExpander: ru.merkulyevsasha.coreandroid.common.AppbarScrollExpander
    private var expanded = true
    private var position = 0

    override fun setServiceLocator(serviceLocator: ServiceLocator) {
        this.serviceLocator = serviceLocator
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ru.merkulyevsasha.coreandroid.common.ToolbarCombinator) {
            combinator = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_useractivities, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val savedState = savedInstanceState ?: arguments
        savedState?.apply {
            position = this.getInt(KEY_POSITION, 0)
            expanded = this.getBoolean(KEY_EXPANDED, true)
        }

        colorThemeResolver = ru.merkulyevsasha.coreandroid.common.ColorThemeResolver(TypedValue(), requireContext().theme)

        toolbar = view.findViewById(R.id.toolbar)
        collapsingToolbarLayout = view.findViewById(R.id.collapsinngToolbarLayout)
        appbarLayout = view.findViewById(R.id.appbarLayout)

        toolbar.setTitle(R.string.fragment_actions_title)
        toolbar.setTitleTextColor(colorThemeResolver.getThemeAttrColor(R.attr.actionBarTextColor))
        collapsingToolbarLayout.isTitleEnabled = false;
        combinator?.bindToolbar(toolbar)

        appbarScrollExpander = ru.merkulyevsasha.coreandroid.common.AppbarScrollExpander(recyclerView, object : ru.merkulyevsasha.coreandroid.common.ShowActionBarListener {
            override fun onShowActionBar(show: Boolean) {
                appbarLayout.setExpanded(show)
            }
        })
        ru.merkulyevsasha.coreandroid.common.AdViewHelper.loadBannerAd(adView)

        swipeRefreshLayout.setOnRefreshListener { presenter?.onRefresh() }
        initSwipeRefreshColorScheme()

        val interactor = serviceLocator.get(ArticlesInteractor::class.java)
        presenter = UserActivitiesPresenterImpl(interactor, serviceLocator.get(NewsDistributor::class.java),
            serviceLocator.get(MainActivityRouter::class.java))
        presenter?.bindView(this)

        initRecyclerView()
        initBottomUp()

        presenter?.onFirstLoad()
    }

    private fun initRecyclerView() {
        layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = ru.merkulyevsasha.coreandroid.common.newsadapter.NewsViewAdapter(
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

    override fun onPause() {
        adView?.pause()
        presenter?.unbindView()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adView?.resume()
        presenter?.bindView(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveFragmentState(outState)
    }

    override fun onDestroyView() {
        adView?.destroy()
        combinator?.unbindToolbar()
        presenter?.onDestroy()
        presenter = null
        serviceLocator.release(ArticleCommentsInteractor::class.java)
        saveFragmentState(arguments ?: Bundle())
        super.onDestroyView()
    }

    override fun showError() {
        Toast.makeText(requireContext(), getString(R.string.articles_activities_loading_error_message), Toast.LENGTH_LONG).show()
    }

    override fun showProgress() {
        swipeRefreshLayout?.isRefreshing = true
    }

    override fun hideProgress() {
        swipeRefreshLayout?.isRefreshing = false
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
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(colorThemeResolver.getThemeAttrColor(R.attr.colorAccent))
        swipeRefreshLayout.setColorSchemeColors(colorThemeResolver.getThemeAttrColor(R.attr.colorControlNormal))
    }
}