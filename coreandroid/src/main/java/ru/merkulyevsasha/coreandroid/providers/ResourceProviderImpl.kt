package ru.merkulyevsasha.coreandroid.providers

import android.content.Context
import ru.merkulyevsasha.core.ResourceProvider

class ResourceProviderImpl(private val context: Context) : ResourceProvider {
    override fun getString(id: Int): String {
        return context.getString(id)
    }
}