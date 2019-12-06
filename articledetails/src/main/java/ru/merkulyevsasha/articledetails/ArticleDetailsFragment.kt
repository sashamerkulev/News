package ru.merkulyevsasha.articledetails

import android.graphics.Bitmap
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_articledetails.progressbar
import kotlinx.android.synthetic.main.fragment_articledetails.webview
import kotlinx.android.synthetic.main.merge_articles_buttons.imageViewComment
import kotlinx.android.synthetic.main.merge_articles_buttons.imageViewDislike
import kotlinx.android.synthetic.main.merge_articles_buttons.imageViewLike
import kotlinx.android.synthetic.main.merge_articles_buttons.layoutButtonComment
import kotlinx.android.synthetic.main.merge_articles_buttons.layoutButtonDislike
import kotlinx.android.synthetic.main.merge_articles_buttons.layoutButtonLike
import kotlinx.android.synthetic.main.merge_articles_buttons.layoutButtonShare
import kotlinx.android.synthetic.main.merge_articles_buttons.textViewComment
import kotlinx.android.synthetic.main.merge_articles_buttons.textViewDislike
import kotlinx.android.synthetic.main.merge_articles_buttons.textViewLike
import ru.merkulyevsasha.core.RequireServiceLocator
import ru.merkulyevsasha.core.ServiceLocator
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.coreandroid.common.ColorThemeResolver

class ArticleDetailsFragment : Fragment(), ArticleDetailsView, RequireServiceLocator {
    companion object {
        private const val ARTICLE_ID = "ARTICLE_ID"
        @JvmStatic
        val TAG: String = ArticleDetailsFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(articleId: Int): Fragment {
            val fragment = ArticleDetailsFragment()
            val args = Bundle()
            args.putInt(ARTICLE_ID, articleId)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var serviceLocator: ServiceLocator
    private var presenter: ArticleDetailsPresenterImpl? = null
    private lateinit var colorThemeResolver: ColorThemeResolver

    private var articleId = 0

    override fun setServiceLocator(serviceLocator: ServiceLocator) {
        this.serviceLocator = serviceLocator
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_articledetails, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        colorThemeResolver = ColorThemeResolver(TypedValue(), requireContext().theme)

        layoutButtonLike.setOnClickListener {
            presenter?.onArticleLikeClicked(articleId)
        }
        layoutButtonComment.setOnClickListener {
            presenter?.onCommentClicked(articleId)
        }
        layoutButtonDislike.setOnClickListener {
            presenter?.onArticleDislikeClicked(articleId)
        }
        layoutButtonShare.setOnClickListener {
            presenter?.onShareClicked(articleId)
        }

        val bundle = savedInstanceState ?: arguments ?: return
        articleId = bundle.getInt(ARTICLE_ID, 0)
        presenter = serviceLocator.get(ArticleDetailsPresenterImpl::class.java)
        presenter?.bindView(this)
        presenter?.onFirstLoad(articleId)
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

    override fun onDestroy() {
        serviceLocator.release(ArticleDetailsPresenterImpl::class.java)
        presenter?.onDestroy()
        presenter = null
        super.onDestroy()
    }

    override fun showProgress() {
        progressbar?.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressbar?.visibility = View.GONE
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
        Toast.makeText(requireContext(), getString(R.string.article_details_loading_error_message), Toast.LENGTH_LONG).show()
    }

    private fun saveFragmentState(state: Bundle) {
        state.putInt(ARTICLE_ID, articleId)
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