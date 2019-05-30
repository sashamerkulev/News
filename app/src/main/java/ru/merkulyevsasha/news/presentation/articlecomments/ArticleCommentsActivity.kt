package ru.merkulyevsasha.news.presentation.articlecomments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_articlecomments.progressbar
import ru.merkulyevsasha.RequireServiceLocator
import ru.merkulyevsasha.ServiceLocator
import ru.merkulyevsasha.core.domain.ArticleCommentsInteractor
import ru.merkulyevsasha.news.R
import ru.merkulyevsasha.news.presentation.common.ColorThemeResolver

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

    private var articleId = 0

    override fun setServiceLocator(serviceLocator: ServiceLocator) {
        this.serviceLocator = serviceLocator
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Normal)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_articlecomments)

        colorThemeResolver = ColorThemeResolver(TypedValue(), theme)

//        layoutButtonLike.setOnClickListener {
//            presenter?.onLikeClicked(articleId)
//        }
//        layoutButtonComment.setOnClickListener {
//            presenter?.onCommentClicked(articleId)
//        }
//        layoutButtonDislike.setOnClickListener {
//            presenter?.onDislikeClicked(articleId)
//        }
//        layoutButtonShare.setOnClickListener {
//            presenter?.onShareClicked(articleId)
//        }

        articleId = intent.getIntExtra(ArticleCommentsActivity.ARTICLE_ID, 0)
        if (articleId > 0) {
            val interactor = serviceLocator.get(ArticleCommentsInteractor::class.java)
            presenter = ArticleCommentsPresenterImpl(interactor)
            presenter?.bindView(this)
            //presenter?.onFirstLoad(articleId)
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
        serviceLocator.release(ArticleCommentsInteractor::class.java)
        super.onDestroy()
    }

    override fun showProgress() {
        progressbar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressbar.visibility = View.GONE
    }

    override fun showError() {
        Toast.makeText(this, "Ooops!", Toast.LENGTH_LONG).show()
    }
}