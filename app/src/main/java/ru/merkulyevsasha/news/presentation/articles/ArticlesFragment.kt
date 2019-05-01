package ru.merkulyevsasha.news.presentation.articles

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_articles.toolbar
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.news.NewsApp
import ru.merkulyevsasha.news.presentation.common.ToolbarCombinator

class ArticlesFragment : Fragment(), ArticlesView {

    companion object {
        @JvmStatic
        val TAG = ArticlesFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): Fragment {
            return ArticlesFragment()
        }
    }

    private var presenter: ArticlesPresenterImpl? = null
    private var combinator: ToolbarCombinator? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ToolbarCombinator) {
            combinator = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(ru.merkulyevsasha.news.R.layout.fragment_articles, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        combinator?.combine(toolbar)

        val serviceLocator = (requireActivity().application as NewsApp).getServiceLocator()
        val interactor = serviceLocator.get(ArticlesInteractor::class.java)
        presenter = ArticlesPresenterImpl(interactor)
    }

    override fun onPause() {
        presenter?.unbindView()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        presenter?.bindView(this)
    }

}