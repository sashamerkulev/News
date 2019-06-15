package ru.merkulyevsasha.news.presentation.splash

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.merkulyevsasha.RequireServiceLocator
import ru.merkulyevsasha.core.ServiceLocator
import ru.merkulyevsasha.core.domain.SetupInteractor
import ru.merkulyevsasha.core.routers.MainActivityRouter
import ru.merkulyevsasha.news.R
import java.util.*

class SplashFragment : Fragment(), SplashView, RequireServiceLocator {

    companion object {
        @JvmStatic
        val TAG: String = SplashFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): Fragment {
            val fragment = SplashFragment()
            fragment.arguments = Bundle()
            return fragment
        }
    }

    private var presenter: SplashPresenterImpl? = null
    private lateinit var serviceLocator: ServiceLocator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_splash, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val setupInteractor = serviceLocator.get(SetupInteractor::class.java)
        presenter = SplashPresenterImpl(setupInteractor)
    }

    override fun onResume() {
        super.onResume()
        presenter?.bindView(this)
        presenter?.onSetup(getFirebaseId = { UUID.randomUUID().toString() })
    }

    override fun onPause() {
        presenter?.unbindView()
        super.onPause()
    }

    override fun onDestroyView() {
//        combinator?.unbindToolbar()
        presenter?.onDestroy()
        super.onDestroyView()
    }

    override fun setServiceLocator(serviceLocator: ServiceLocator) {
        this.serviceLocator = serviceLocator
    }

    override fun showMainScreen() {
        serviceLocator.get(MainActivityRouter::class.java).showMain()
    }

    override fun showFatalError() {
    }

}