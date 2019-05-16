package ru.merkulyevsasha.news.presentation.userinfo

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_useractivities.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_userainfo.toolbar
import ru.merkulyevsasha.core.domain.UsersInteractor
import ru.merkulyevsasha.news.NewsApp
import ru.merkulyevsasha.news.presentation.common.ToolbarCombinator

class UserInfoFragment : Fragment(), UserInfoView {

    companion object {
        @JvmStatic
        val TAG = UserInfoFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): Fragment {
            return UserInfoFragment()
        }
    }

    private var presenter: UserInfoPresenterImpl? = null
    private var combinator: ToolbarCombinator? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ToolbarCombinator) {
            combinator = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(ru.merkulyevsasha.news.R.layout.fragment_userainfo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        combinator?.combine(toolbar)

        val serviceLocator = (requireActivity().application as NewsApp).getServiceLocator()
        val interactor = serviceLocator.get(UsersInteractor::class.java)
        presenter = UserInfoPresenterImpl(interactor)
        presenter?.bindView(this)
        presenter?.onFirstLoad()
    }

    override fun onPause() {
        presenter?.unbindView()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        presenter?.bindView(this)
    }

    override fun showError() {
    }

    override fun showProgress() {
        swipeRefreshLayout.isRefreshing = true
    }

    override fun hideProgress() {
        swipeRefreshLayout.isRefreshing = false
    }
}