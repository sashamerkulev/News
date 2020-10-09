package ru.merkulyevsasha.articledetails.presentation

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
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
import ru.merkulyevsasha.articledetails.R
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.coreandroid.common.BaseFragment
import ru.merkulyevsasha.coreandroid.common.observe

@AndroidEntryPoint
class ArticleDetailsFragment : BaseFragment<ArticleDetailsViewModel>(R.layout.fragment_articledetails) {
    companion object {
        private const val ARGS_ARTICLE_ID = "ARTICLE_ID"

        val TAG: String = "ArticleDetailsFragment"

        fun newInstance(articleId: Int): Fragment {
            val fragment = ArticleDetailsFragment()
            val args = Bundle()
            args.putInt(ARGS_ARTICLE_ID, articleId)
            fragment.arguments = args
            return fragment
        }
    }

    private var articleId = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = savedInstanceState ?: arguments ?: return
        articleId = bundle.getInt(ARGS_ARTICLE_ID, 0)
        if (savedInstanceState == null) {
            model.onFirstLoad(articleId)
        }
        initLikeClickListeners()
        observeOnProgressChanged()
        observeOnAddItemChanged()
        observeOnUpdateItemChanged()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveFragmentState(outState)
    }

    private fun initLikeClickListeners() {
        layoutButtonLike.setOnClickListener {
            model.onArticleLikeClicked(articleId)
        }
        layoutButtonComment.setOnClickListener {
            model.onCommentClicked(articleId)
        }
        layoutButtonDislike.setOnClickListener {
            model.onArticleDislikeClicked(articleId)
        }
        layoutButtonShare.setOnClickListener {
            model.onShareClicked(articleId)
        }
    }

    private fun observeOnProgressChanged() {
        observe(model.progress) { progress ->
            if (progress) showProgress()
            else hideProgress()
        }
    }

    private fun observeOnAddItemChanged() {
        observe(model.addItem) { item ->
            showItem(item)
        }
    }

    private fun observeOnUpdateItemChanged() {
        observe(model.updateItem) { item ->
            updateItem(item)
        }
    }

    private fun showProgress() {
        progressbar?.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        progressbar?.visibility = View.GONE
    }

    private fun showItem(item: Article) {
        webview.webViewClient = ArticleDetailsViewClient()
        webview.loadUrl(item.link)

        updateItem(item)
    }

    private fun updateItem(item: Article) {
        textViewLike.text = item.usersLikeCount.toString()
        textViewDislike.text = item.usersDislikeCount.toString()
        textViewComment.text = item.usersCommentCount.toString()

        colorThemeResolver.setArticleActivityColor(item.isUserLiked, textViewLike, imageViewLike)
        colorThemeResolver.setArticleActivityColor(item.isUserDisliked, textViewDislike, imageViewDislike)
        colorThemeResolver.setArticleActivityColor(item.isUserCommented, textViewComment, imageViewComment)
    }

    private fun saveFragmentState(state: Bundle) {
        state.putInt(ARGS_ARTICLE_ID, articleId)
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