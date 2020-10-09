package ru.merkulyevsasha.coreandroid.common

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import ru.merkulyevsasha.coreandroid.base.BaseViewModel
import javax.inject.Inject

abstract class BaseFragment<T : BaseViewModel>(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {

    lateinit var colorThemeResolver: ColorThemeResolver
    lateinit var combinator: ToolbarCombinator

    @Inject
    lateinit var model: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ToolbarCombinator) {
            combinator = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        colorThemeResolver = ColorThemeResolver(TypedValue(), requireContext().theme)
        observeOnMessagesChanged()
    }

    override fun onDestroy() {
        if (::combinator.isInitialized) {
            combinator.unbindToolbar()
        }
        super.onDestroy()
    }

    private fun observeOnMessagesChanged() {
        observe(model.messages) {
            showMessage(it)
        }
    }

    private fun showMessage(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
    }

}