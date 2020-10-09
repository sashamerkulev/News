package ru.merkulyevsasha.articlecomments.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_articlecomments.adView
import kotlinx.android.synthetic.main.fragment_articlecomments.editTextComment
import kotlinx.android.synthetic.main.fragment_articlecomments.layoutAddCommentButton
import kotlinx.android.synthetic.main.fragment_articlecomments.recyclerView
import kotlinx.android.synthetic.main.fragment_articlecomments.swipeRefreshLayout
import ru.merkulyevsasha.articlecomments.BuildConfig
import ru.merkulyevsasha.articlecomments.R
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComment
import ru.merkulyevsasha.core.models.ArticleOrComment
import ru.merkulyevsasha.coreandroid.common.AdViewHelper
import ru.merkulyevsasha.coreandroid.common.BaseFragment
import ru.merkulyevsasha.coreandroid.common.KbUtils.Companion.hideKeyboard
import ru.merkulyevsasha.coreandroid.common.observe
import java.util.*

@AndroidEntryPoint
class ArticleCommentsFragment : BaseFragment<ArticleCommentsViewModel>(R.layout.fragment_articlecomments) {

    companion object {
        private const val KEY_POSITION = "key_position"
        private const val ARTICLE_ID = "ARTICLE_ID"

        @JvmStatic
        val TAG: String = "ArticleCommentsFragment"

        @JvmStatic
        fun newInstance(articleId: Int): Fragment {
            val fragment = ArticleCommentsFragment()
            val args = Bundle()
            args.putInt(ARTICLE_ID, articleId)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var adapter: CommentsViewAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private var articleId = 0
    private var position = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = savedInstanceState ?: arguments ?: return
        articleId = bundle.getInt(ARTICLE_ID, 0)
        position = bundle.getInt(KEY_POSITION, 0)
        AdViewHelper.loadBannerAd(adView, BuildConfig.DEBUG_MODE)
        initRecyclerView()
        if (savedInstanceState == null) {
            model.onFirstLoad(articleId)
        }
        layoutAddCommentButton.setOnClickListener {
            model.onAddCommentClicked(articleId, editTextComment.text.toString())
        }
        colorThemeResolver.initSwipeRefreshColorScheme(swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            model.onRefresh(articleId)
        }
        observeOnProgressChanged()
        observeOnAddArticleCommentsChanged()
        observeOnUpdateArticleCommentsChanged()
        observeOnUpdateArticleChanged()
    }

    override fun onPause() {
        adView?.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adView?.resume()
    }

    override fun onDestroy() {
        adView?.destroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveFragmentState(outState)
    }

    private fun observeOnProgressChanged() {
        observe(model.progress) {
            swipeRefreshLayout?.isRefreshing = it
        }
    }

    private fun observeOnAddArticleCommentsChanged() {
        observe(model.items) {
            showComments(it)
        }
    }

    private fun observeOnUpdateArticleCommentsChanged() {
        observe(model.updateArticleComment) {
            updateCommentItem(it)
        }
    }

    private fun observeOnUpdateArticleChanged() {
        observe(model.article) {
            updateItem(it)
        }
    }

    private fun showComments(items: List<ArticleOrComment>) {
        adapter.setItems(items)
    }

    private fun updateItem(item: Article) {
        adapter.updateArticleItem(item)
    }

    private fun updateCommentItem(item: ArticleComment) {
        editTextComment.setText("")
        hideKeyboard(requireActivity())
        adapter.updateCommentItem(item)
    }

    private fun saveFragmentState(state: Bundle) {
        position = layoutManager.findFirstVisibleItemPosition()
        state.putInt(KEY_POSITION, position)
        state.putInt(ARTICLE_ID, articleId)
    }

    private fun initRecyclerView() {
        layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = CommentsViewAdapter(
            requireContext(),
            model,
            model,
            model,
            model,
            colorThemeResolver,
            ArrayList()
        )
        recyclerView.adapter = adapter
    }
}