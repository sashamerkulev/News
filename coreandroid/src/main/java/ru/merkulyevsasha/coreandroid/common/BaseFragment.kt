package ru.merkulyevsasha.coreandroid.common

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import ru.merkulyevsasha.coreandroid.base.BaseViewModel

class BaseFragment<T : BaseViewModel>(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {

    lateinit var appbarScrollExpander: AppbarScrollExpander
    lateinit var colorThemeResolver: ColorThemeResolver
    lateinit var combinator: ToolbarCombinator

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ToolbarCombinator) {
            combinator = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        colorThemeResolver = ColorThemeResolver(TypedValue(), requireContext().theme)
    }

}