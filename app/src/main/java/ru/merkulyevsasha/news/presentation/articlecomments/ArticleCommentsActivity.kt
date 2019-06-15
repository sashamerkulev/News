package ru.merkulyevsasha.news.presentation.articlecomments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_articlecomments.editTextComment
import kotlinx.android.synthetic.main.activity_articlecomments.layoutAddCommentButton
import kotlinx.android.synthetic.main.activity_articlecomments.recyclerView
import kotlinx.android.synthetic.main.activity_articlecomments.swipeRefreshLayout
import ru.merkulyevsasha.RequireServiceLocator
import ru.merkulyevsasha.core.ServiceLocator
import ru.merkulyevsasha.core.domain.ArticleCommentsInteractor
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComment
import ru.merkulyevsasha.core.models.ArticleOrComment
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.presentation.articles.ArticlesFragment
import ru.merkulyevsasha.news.presentation.common.ColorThemeResolver
import java.util.*

class ArticleCommentsActivity : AppCompatActivity(), ArticleCommentsView, RequireServiceLocator {

    companion object {
        private const val ARTICLE_ID = "ARTICLE_ID"
        @JvmStatic
        fun show(context: Context, articleId: Int) {
            val intent = Intent(context, ArticleCommentsActivity::class.java)
            intent.putExtra(ARTICLE_ID, articleId)
            context.startActivity(intent)
        }
    }

    private lateinit var serviceLocator: ServiceLocator
    private var presenter: ArticleCommentsPresenterImpl? = null
    private lateinit var colorThemeResolver: ColorThemeResolver

    private lateinit var adapter: CommentsViewAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private var articleId = 0
    private var position = 0

    override fun setServiceLocator(serviceLocator: ServiceLocator) {
        this.serviceLocator = serviceLocator
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Normal)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_articlecomments)

        colorThemeResolver = ColorThemeResolver(TypedValue(), theme)

        if (savedInstanceState == null) {
            articleId = intent.getIntExtra(ARTICLE_ID, 0)
        } else {
            articleId = savedInstanceState.getInt(ARTICLE_ID, 0)
            position = savedInstanceState.getInt(ArticlesFragment.KEY_POSITION, 0)
        }

        if (articleId <= 0) {
            finish()
            return
        }

        val interactor = serviceLocator.get(ArticleCommentsInteractor::class.java)
        val articleInteractor = serviceLocator.get(ArticlesInteractor::class.java)
        presenter = ArticleCommentsPresenterImpl(interactor, articleInteractor, serviceLocator.get(KeyValueStorage::class.java))
        presenter?.bindView(this)

        initRecyclerView()

        presenter?.onFirstLoad(articleId)

        layoutAddCommentButton.setOnClickListener {
            presenter?.onAddCommentClicked(articleId, editTextComment.text.toString())
        }

        initSwipeRefreshColorScheme()
        swipeRefreshLayout.setOnRefreshListener {
            presenter?.onRefresh(articleId)
        }
    }

    override fun onPause() {
        presenter?.unbindView()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        presenter?.bindView(this)
    }

    override fun onDestroy() {
        presenter?.onDestroy()
        presenter = null
        serviceLocator.release(ArticleCommentsInteractor::class.java)
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        saveFragmentState(outState)
    }

    override fun showProgress() {
        swipeRefreshLayout.isRefreshing = true
    }

    override fun hideProgress() {
        swipeRefreshLayout.isRefreshing = false
    }

    override fun showError() {
        Toast.makeText(this, "Ooops!", Toast.LENGTH_LONG).show()
    }

    override fun showComments(items: List<ArticleOrComment>) {
        adapter.setItems(items)
    }

    override fun updateItem(item: Article) {
        adapter.updateArticleItem(item)
    }

    override fun updateCommentItem(item: ArticleComment) {
        adapter.updateCommentItem(item)
    }

    private fun initRecyclerView() {
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = CommentsViewAdapter(
            this,
            presenter,
            presenter,
            presenter,
            presenter,
            colorThemeResolver,
            ArrayList()
        )
        recyclerView.adapter = adapter
    }

    private fun initSwipeRefreshColorScheme() {
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(colorThemeResolver.getThemeAttrColor(R.attr.colorAccent))
        swipeRefreshLayout.setColorSchemeColors(colorThemeResolver.getThemeAttrColor(R.attr.colorControlNormal))
    }

    private fun saveFragmentState(state: Bundle) {
        position = layoutManager.findFirstVisibleItemPosition()
        state.putInt(ArticlesFragment.KEY_POSITION, position)
        state.putInt(ARTICLE_ID, articleId)
    }

}