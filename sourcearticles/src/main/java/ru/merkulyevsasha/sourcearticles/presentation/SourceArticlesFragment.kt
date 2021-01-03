package ru.merkulyevsasha.sourcearticles.presentation

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.include_appbar_layout.appbarLayout
import kotlinx.android.synthetic.main.include_appbar_layout.collapsingToolbarLayout
import kotlinx.android.synthetic.main.include_appbar_layout.toolbar
import kotlinx.android.synthetic.main.merge_articles_layout.adView
import kotlinx.android.synthetic.main.merge_articles_layout.buttonUp
import kotlinx.android.synthetic.main.merge_articles_layout.recyclerView
import kotlinx.android.synthetic.main.merge_articles_layout.swipeRefreshLayout
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.coreandroid.common.AdViewHelper
import ru.merkulyevsasha.coreandroid.common.AppbarScrollExpander
import ru.merkulyevsasha.coreandroid.common.BaseFragment
import ru.merkulyevsasha.coreandroid.common.ShowActionBarListener
import ru.merkulyevsasha.coreandroid.common.newsadapter.NewsViewAdapter
import ru.merkulyevsasha.coreandroid.common.observe
import ru.merkulyevsasha.sourcearticles.BuildConfig
import ru.merkulyevsasha.sourcearticles.R

@AndroidEntryPoint
class SourceArticlesFragment : BaseFragment<SourceArticlesViewModel>(R.layout.fragment_sourcename) {

    companion object {
        private const val MAX_POSITION = 5
        private const val KEY_POSITION = "key_position"
        private const val KEY_EXPANDED = "key_expanded"
        private const val KEY_SEARCH_TEXT = "key_search_text"
        private const val SOURCE_ID = "SOURCE_ID"
        private const val SOURCE_NAME = "SOURCE_NAME"

        @JvmStatic
        val TAG: String = "SourceArticlesFragment"

        @JvmStatic
        fun newInstance(sourceId: String, sourceName: String): Fragment {
            val fragment = SourceArticlesFragment()
            val args = Bundle()
            args.putString(SOURCE_ID, sourceId)
            args.putString(SOURCE_NAME, sourceName)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var adapter: NewsViewAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var appbarScrollExpander: AppbarScrollExpander
    private var expanded = true
    private var position = 0

    private lateinit var searchItem: MenuItem
    private lateinit var searchView: SearchView
    private var searchText: String? = null
    private var sourceId: String? = null
    private var sourceName: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val savedState = savedInstanceState ?: arguments
        savedState?.apply {
            position = this.getInt(KEY_POSITION, 0)
            expanded = this.getBoolean(KEY_EXPANDED, true)
            searchText = this.getString(KEY_SEARCH_TEXT, searchText)
            sourceId = this.getString(SOURCE_ID, sourceId)
            sourceName = this.getString(SOURCE_NAME, sourceName)
        }
        initRecyclerView()
        toolbar.setTitle(sourceName)
        toolbar.setTitleTextColor(colorThemeResolver.getThemeAttrColor(R.attr.actionBarTextColor))
        collapsingToolbarLayout.isTitleEnabled = false
        combinator.bindToolbar(toolbar)
        appbarScrollExpander = AppbarScrollExpander(recyclerView, object : ShowActionBarListener {
            override fun onShowActionBar(show: Boolean) {
                appbarLayout.setExpanded(show)
            }
        })
        AdViewHelper.loadBannerAd(adView, BuildConfig.DEBUG_MODE)
        swipeRefreshLayout.setOnRefreshListener { model.onRefresh(sourceId!!) }
        colorThemeResolver.initSwipeRefreshColorScheme(swipeRefreshLayout)
        initBottomUp()
        if (searchText.isNullOrEmpty()) {
            model.onFirstLoad(sourceId!!)
        }
        observeOnProgressChanged()
        observeOnItemsChanged()
        observeOnItemChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.articles_menu, menu)
        searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        val searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
        searchEditText.setTextColor(colorThemeResolver.getThemeAttrColor(R.attr.colorControlNormal))
        if (!searchText.isNullOrEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(searchText, false)
            searchView.clearFocus()
            model.onSearch(sourceId!!, searchText)
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchText = query
                model.onSearch(sourceId!!, query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    searchText = ""
                    model.onSearch(sourceId!!, newText)
                }
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_refresh) {
            model.onRefresh(sourceId!!)
            return true
        } else if (item.itemId == R.id.action_search) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        adView?.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adView?.resume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveFragmentState(outState)
    }

    override fun onDestroyView() {
        saveFragmentState(arguments ?: Bundle())
        super.onDestroyView()
    }

    override fun onDestroy() {
        adView?.destroy()
        super.onDestroy()
    }

    private fun observeOnProgressChanged() {
        observe(model.progress) { progress ->
            swipeRefreshLayout.isRefreshing = progress
        }
    }

    private fun observeOnItemsChanged() {
        observe(model.items) { items ->
            showItems(items)
        }
    }

    private fun observeOnItemChanged() {
        observe(model.item) { item ->
            updateItem(item)
        }
    }

    private fun showItems(items: List<Article>) {
        adapter.setItems(items)
        layoutManager.scrollToPosition(position)
    }

    private fun updateItem(item: Article) {
        adapter.updateItem(item)
    }

    private fun initRecyclerView() {
        layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = NewsViewAdapter(
            requireContext(),
            model,
            null,
            model,
            model,
            model,
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

    private fun saveFragmentState(state: Bundle) {
        position = layoutManager.findFirstVisibleItemPosition()
        state.putInt(KEY_POSITION, position)
        state.putBoolean(KEY_EXPANDED, expanded)
        state.putString(KEY_SEARCH_TEXT, searchText)
        state.putString(SOURCE_NAME, sourceName)
        state.putString(SOURCE_ID, sourceId)
    }
}