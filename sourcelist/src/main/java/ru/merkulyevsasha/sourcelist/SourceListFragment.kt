package ru.merkulyevsasha.sourcelist

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
import kotlinx.android.synthetic.main.fragment_sourcelist.adView
import kotlinx.android.synthetic.main.fragment_sourcelist.recyclerView
import kotlinx.android.synthetic.main.fragment_sourcelist.swipeRefreshLayout
import kotlinx.android.synthetic.main.row_sourcelist.view.sourceName
import ru.merkulyevsasha.core.RequireServiceLocator
import ru.merkulyevsasha.core.ServiceLocator
import ru.merkulyevsasha.core.models.RssSource
import ru.merkulyevsasha.coreandroid.common.AdViewHelper
import ru.merkulyevsasha.coreandroid.common.AppbarScrollExpander
import ru.merkulyevsasha.coreandroid.common.ColorThemeResolver
import ru.merkulyevsasha.coreandroid.common.ShowActionBarListener
import ru.merkulyevsasha.coreandroid.common.ToolbarCombinator

class SourceListFragment : Fragment(), SourceListView, RequireServiceLocator {

    companion object {
        private const val KEY_POSITION = "key_position"
        private const val KEY_EXPANDED = "key_expanded"

        @JvmStatic
        val TAG: String = "SourceListFragment"

        @JvmStatic
        fun newInstance(): Fragment {
            val fragment = SourceListFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var toolbar: Toolbar
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var appbarLayout: AppBarLayout
    private lateinit var appbarScrollExpander: AppbarScrollExpander

    private lateinit var serviceLocator: ServiceLocator
    private var presenter: SourceListPresenterImpl? = null
    private lateinit var colorThemeResolver: ColorThemeResolver
    private var combinator: ToolbarCombinator? = null

    private lateinit var adapter: SourceListAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private var expanded = true
    private var position = 0

    override fun setServiceLocator(serviceLocator: ServiceLocator) {
        this.serviceLocator = serviceLocator
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ToolbarCombinator) {
            combinator = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_sourcelist, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        colorThemeResolver = ColorThemeResolver(TypedValue(), requireContext().theme)

        toolbar = view.findViewById(R.id.toolbar)
        collapsingToolbarLayout = view.findViewById(R.id.collapsinngToolbarLayout)
        appbarLayout = view.findViewById(R.id.appbarLayout)

        toolbar.setTitle(R.string.fragment_sourcelist_title)
        toolbar.setTitleTextColor(colorThemeResolver.getThemeAttrColor(R.attr.actionBarTextColor))
        collapsingToolbarLayout.isTitleEnabled = false
        combinator?.bindToolbar(toolbar)

        appbarScrollExpander = AppbarScrollExpander(recyclerView, object : ShowActionBarListener {
            override fun onShowActionBar(show: Boolean) {
                appbarLayout.setExpanded(show)
            }
        })

        swipeRefreshLayout.setOnRefreshListener { presenter?.onRefresh() }
        colorThemeResolver.initSwipeRefreshColorScheme(swipeRefreshLayout)

        AdViewHelper.loadBannerAd(adView, BuildConfig.DEBUG_MODE)

        presenter = serviceLocator.get(SourceListPresenterImpl::class.java)
        presenter?.bindView(this)

        initRecyclerView()

        presenter?.onFirstLoad()
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
        saveFragmentState(arguments ?: Bundle())
        super.onDestroyView()
    }

    override fun onDestroy() {
        adView?.destroy()
        combinator?.unbindToolbar()
        serviceLocator.release(SourceListPresenterImpl::class.java)
        presenter?.onDestroy()
        presenter = null
        super.onDestroy()
    }

    override fun showError() {
        Toast.makeText(requireContext(), getString(R.string.source_list_loading_error_message), Toast.LENGTH_LONG).show()
    }

    override fun showProgress() {
        swipeRefreshLayout?.isRefreshing = true
    }

    override fun hideProgress() {
        swipeRefreshLayout?.isRefreshing = false
    }

    override fun showItems(items: List<RssSource>) {
        adapter.setItems(items)
        layoutManager.scrollToPosition(position)
    }

    private fun initRecyclerView() {
        layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = SourceListAdapter(
            requireContext(),
            colorThemeResolver,
            ArrayList()
        )
        recyclerView.adapter = adapter
    }

    private fun saveFragmentState(state: Bundle) {
        position = layoutManager.findFirstVisibleItemPosition()
        state.putInt(KEY_POSITION, position)
        state.putBoolean(KEY_EXPANDED, expanded)
    }

    inner class SourceListAdapter(
        private val context: Context,
        private val colorThemeResolver: ColorThemeResolver,
        private val items: MutableList<RssSource>
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_sourcelist, parent, false)
            return SourceListViewHolder(view)
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as SourceListViewHolder).bind(position)
        }

        fun setItems(items: List<RssSource>) {
            this.items.clear()
            this.items.addAll(items)
            this.notifyDataSetChanged()
        }

        inner class SourceListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(position: Int) {
                val item = items[position]
                itemView.sourceName.text = item.sourceName
                itemView.setOnClickListener {
                    presenter?.onSourceClicked(item.sourceId, item.sourceName)
                }
            }
        }
    }

}