package ru.merkulyevsasha.news.presentation.articledetails

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_articledetails.imageViewComment
import kotlinx.android.synthetic.main.activity_articledetails.imageViewDislike
import kotlinx.android.synthetic.main.activity_articledetails.imageViewLike
import kotlinx.android.synthetic.main.activity_articledetails.layoutButtonComment
import kotlinx.android.synthetic.main.activity_articledetails.layoutButtonDislike
import kotlinx.android.synthetic.main.activity_articledetails.layoutButtonLike
import kotlinx.android.synthetic.main.activity_articledetails.layoutButtonShare
import kotlinx.android.synthetic.main.activity_articledetails.progressbar
import kotlinx.android.synthetic.main.activity_articledetails.textViewComment
import kotlinx.android.synthetic.main.activity_articledetails.textViewDislike
import kotlinx.android.synthetic.main.activity_articledetails.textViewLike
import kotlinx.android.synthetic.main.activity_articledetails.webview
import ru.merkulyevsasha.ServiceLocator
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.news.NewsApp
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.presentation.common.ColorThemeResolver

class ArticleDetailsActivity : AppCompatActivity(), ArticleDetailsView {

    companion object {
        private const val ARTICLE_ID = "ARTICLE_ID"
        @JvmStatic
        fun show(context: Context, articleId: Int) {
            val intent = Intent(context, ArticleDetailsActivity::class.java)
            intent.putExtra(ARTICLE_ID, articleId)
            context.startActivity(intent)
        }
    }

    private lateinit var serviceLocator: ServiceLocator
    private var presenter: ArticleDetailsPresenterImpl? = null
    private lateinit var colorThemeResolver: ColorThemeResolver

    private var articleId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Normal)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_articledetails)

        colorThemeResolver = ColorThemeResolver(TypedValue(), theme)

        layoutButtonLike.setOnClickListener {
            presenter?.onLikeClicked(articleId)
        }
        layoutButtonComment.setOnClickListener {
            presenter?.onCommentClicked(articleId)
        }
        layoutButtonDislike.setOnClickListener {
            presenter?.onDislikeClicked(articleId)
        }
        layoutButtonShare.setOnClickListener {
            presenter?.onShareClicked(articleId)
        }

        serviceLocator = (application as NewsApp).getServiceLocator()
        articleId = intent.getIntExtra(ARTICLE_ID, 0)
        if (articleId > 0) {
            val interactor = serviceLocator.get(ArticlesInteractor::class.java)
            presenter = ArticleDetailsPresenterImpl(interactor, serviceLocator.getApplicationRouter())
            presenter?.bindView(this)
            presenter?.onFirstLoad(articleId)
        } else {
            finish()
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
        serviceLocator.release(ArticlesInteractor::class.java)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun showProgress() {
        progressbar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressbar.visibility = View.GONE
    }

    override fun showItem(item: Article) {
        webview.webViewClient = ArticleDetailsViewClient()
        webview.loadUrl(item.link)

        updateItem(item)
    }

    override fun updateItem(item: Article) {
        textViewLike.text = item.usersLikeCount.toString()
        textViewDislike.text = item.usersDislikeCount.toString()
        textViewComment.text = item.usersCommentCount.toString()

        colorThemeResolver.setArticleActivityColor(item.isUserLiked, textViewLike, imageViewLike)
        colorThemeResolver.setArticleActivityColor(item.isUserDisliked, textViewDislike, imageViewDislike)
        colorThemeResolver.setArticleActivityColor(item.isUserCommented, textViewComment, imageViewComment)
    }

    override fun showError() {
        Toast.makeText(this, "Ooops!", Toast.LENGTH_LONG).show()
    }

    private inner class ArticleDetailsViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            hideProgress()
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            showProgress()
            super.onPageStarted(view, url, favicon)
        }
    }
}