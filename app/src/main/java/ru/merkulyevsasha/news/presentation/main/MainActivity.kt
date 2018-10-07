package ru.merkulyevsasha.news.presentation.main

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.*
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.row_news_item.view.*
import ru.merkulyevsasha.apprate.AppRateRequester
import ru.merkulyevsasha.news.BuildConfig
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.data.utils.NewsConstants
import ru.merkulyevsasha.news.helpers.BroadcastHelper
import ru.merkulyevsasha.news.models.Article
import ru.merkulyevsasha.news.newsjobs.NewsJob
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainView, NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    @Inject
    lateinit var newsConsts: NewsConstants
    @Inject
    lateinit var pres: MainPresenter

//    @BindView(R.id.appbar_layout) internal var appbarLayout: AppBarLayout? = null
//    @BindView(R.id.collapsinng_toolbar_layout) internal var collapsToolbar: CollapsingToolbarLayout? = null
//    @BindView(R.id.refreshLayout) internal var refreshLayout: SwipeRefreshLayout? = null
//    @BindView(R.id.drawer_layout) internal var drawer: DrawerLayout? = null
//    @BindView(R.id.nav_view) internal var navigationView: NavigationView? = null
//    @BindView(R.id.recyclerView) internal var recyclerView: RecyclerView? = null
//    @BindView(R.id.toolbar) internal var toolbar: Toolbar? = null
//    @BindView(R.id.adView) internal var adView: AdView? = null
//    @BindView(R.id.content_main) internal var root: View? = null
//    @BindView(R.id.button_up) internal var buttonUp: View? = null

    private lateinit var adapter: NewsViewAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var searchItem: MenuItem
    private lateinit var searchView: SearchView

    private lateinit var appbarScrollExpander: AppbarScrollExpander
    private lateinit var broadcastReceiver: BroadcastReceiver

    private var expanded = true
    private var position: Int = 0

    private var navId: Int = 0
    private var searchText: String = ""

    private var lastVisibleItemPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        toolbar.setOnClickListener { _ -> pres.onPrepareToSearch() }

        collapsinng_toolbar_layout.isTitleEnabled = false

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = NewsViewAdapter(
                this,
                ArrayList(),
                object : OnNewsItemClickListener {
                    override fun onItemClick(item: Article) {
                        pres.onItemClicked(item)
                    }
                }
        )
        recyclerView.adapter = adapter

        refreshLayout.setOnRefreshListener { pres.onRefresh(navId) }
        refreshLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.colorAccent))
        refreshLayout.setColorSchemeResources(R.color.white)

        appbarScrollExpander = AppbarScrollExpander(recyclerView, appbar_layout)
        appbarScrollExpander.expanded = expanded

        navId = R.id.nav_all
        title = newsConsts.getSourceNameTitle(navId)

        AppRateRequester.Run(this, BuildConfig.APPLICATION_ID)

        val adRequest = if (BuildConfig.DEBUG_MODE)
            AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("349C53FFD0654BDC5FF7D3D9254FC8E6").build()
        else
            AdRequest.Builder().addTestDevice("349C53FFD0654BDC5FF7D3D9254FC8E6").build()
        adView.loadAd(adRequest)

        pres.bindView(this)
        pres.onCreateView()

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val finished = intent.getBooleanExtra(BroadcastHelper.KEY_FINISH_NAME, false)
                val updated = intent.getBooleanExtra(BroadcastHelper.KEY_UPDATE_NAME, false)

                pres.onReceived(navId, updated, finished)
            }
        }

        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (lastVisibleItemPosition >= layoutManager.findFirstVisibleItemPosition()) {
                    lastVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    if (lastVisibleItemPosition > 5 && !refreshLayout.isRefreshing) {
                        button_up.visibility = View.VISIBLE
                    } else {
                        button_up.visibility = View.GONE
                    }
                } else {
                    lastVisibleItemPosition = layoutManager.findFirstVisibleItemPosition() - 1
                    button_up.visibility = View.GONE
                }
            }
        })

        button_up.setOnClickListener { _ ->
            layoutManager.scrollToPosition(0)
            lastVisibleItemPosition = 0
            button_up.visibility = View.GONE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(KEY_NAV_ID, navId)
        outState.putInt(KEY_POSITION, layoutManager.findFirstVisibleItemPosition())
        outState.putBoolean(KEY_REFRESHING, refreshLayout.isRefreshing)
        outState.putBoolean(KEY_EXPANDED, appbarScrollExpander.expanded)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val isRefreshing = savedInstanceState.getBoolean(KEY_REFRESHING, false)
        position = savedInstanceState.getInt(KEY_POSITION, -1)
        navId = savedInstanceState.getInt(KEY_NAV_ID, R.id.nav_all)
        expanded = savedInstanceState.getBoolean(KEY_EXPANDED, true)
        refreshLayout.isRefreshing = isRefreshing
    }

    public override fun onPause() {
        position = layoutManager.findFirstVisibleItemPosition()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        if (adView != null) {
            adView.pause()
        }
        pres.unbindView()
        super.onPause()
    }

    public override fun onResume() {
        super.onResume()
        pres.bindView(this)
        pres.onResume(refreshLayout.isRefreshing, navId, searchText)
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter(BroadcastHelper.ACTION_LOADING))
        if (adView != null) {
            adView.resume()
        }
        appbar_layout.setExpanded(expanded)
        if (position > 0) layoutManager.scrollToPosition(position)
    }

    public override fun onDestroy() {
        if (adView != null) {
            adView.destroy()
        }
        pres.onDestroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        if (!searchText.isEmpty()) {
            pres.onPrepareToSearch()
        }

        searchView.setOnQueryTextListener(this)

        val refreshItem = menu.findItem(R.id.action_refresh)
        refreshItem.setOnMenuItemClickListener { _ ->
            if (!refreshLayout.isRefreshing) {
                pres.onRefresh(navId)
            }
            false
        }
        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        if (query.length < 3) {
            Snackbar.make(content_main, R.string.search_validation_message, Snackbar.LENGTH_LONG).show()
            return false
        }
        searchText = query
        pres.onSearch(searchText)
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        if (newText.isEmpty()) {
            searchText = ""
            pres.onCancelSearch(navId)
        }
        return false
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer_layout.closeDrawer(GravityCompat.START)

        title = item.title
        navId = item.itemId

        pres.onSelectSource(navId)

        return true
    }

    override fun prepareToSearch() {
        searchItem.expandActionView()
        searchView.setQuery(searchText, false)
    }

    override fun showDetailScreen(item: Article) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary)).build()
        customTabsIntent.launchUrl(this, Uri.parse(item.link))
    }

    override fun showProgress() {
        refreshLayout.isRefreshing = true
    }

    override fun hideProgress() {
        refreshLayout.isRefreshing = false
    }

    override fun showItems(result: List<Article>) {
        adapter.setItems(result)
    }

    override fun showNoSearchResultMessage() {
        Snackbar.make(content_main, R.string.search_nofound_message, Snackbar.LENGTH_LONG).show()
    }

    override fun showMessageError() {
        Snackbar.make(content_main, R.string.something_went_wrong_message, Snackbar.LENGTH_LONG).show()
    }

    override fun scheduleJob() {
        NewsJob.scheduleJob()
    }

    private interface OnNewsItemClickListener {
        fun onItemClick(item: Article)
    }

    private inner class NewsViewAdapter constructor(
            private val context: Context,
            private val items: MutableList<Article>,
            private val onNewsItemClickListener: OnNewsItemClickListener
    ) : RecyclerView.Adapter<ItemViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_news_item, parent, false)
            return ItemViewHolder(view)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

            val item = items[position]

            val sourceNavId = item.sourceNavId
            val source = newsConsts.getSourceNameTitle(sourceNavId)
            val title = item.title.trim { it <= ' ' }
            val description = item.description
            val url = item.pictureUrl

            val pubDate = item.pubDate
            @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("dd/MM/yyyy HH:mm")
            holder.itemView.news_date_source.text = String.format("%s %s", format.format(pubDate), source)

            if (title == description || description == null || description.isEmpty()) {
                holder.itemView.news_description.visibility = View.GONE
            } else {
                holder.itemView.news_description.visibility = View.VISIBLE
                holder.itemView.news_description.text = description.trim { it <= ' ' }
            }
            holder.itemView.news_title.text = title
            holder.itemView.imageview_thumb.setImageResource(0)
            if (url == null) {
                holder.itemView.imageview_thumb.visibility = View.GONE
            } else {
                holder.itemView.imageview_thumb.visibility = View.VISIBLE
                Glide.with(context).load(url).into(holder.itemView.imageview_thumb)
            }

            holder.itemView.setOnClickListener { _ -> onNewsItemClickListener.onItemClick(item) }

        }

        override fun getItemCount(): Int {
            return items.size
        }

        internal fun setItems(items: List<Article>) {
            this.items.clear()
            this.items.addAll(items)
            this.notifyDataSetChanged()
        }

    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        private const val KEY_REFRESHING = "ru.merkulyevsasha.news.key_refreshing"
        private const val KEY_NAV_ID = "ru.merkulyevsasha.news.key_navId"
        private const val KEY_POSITION = "ru.merkulyevsasha.news.key_position"
        private const val KEY_EXPANDED = "ru.merkulyevsasha.news.key_expanded"
    }
}


