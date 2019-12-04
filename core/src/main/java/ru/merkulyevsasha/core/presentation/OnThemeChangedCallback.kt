package ru.merkulyevsasha.core.presentation

import ru.merkulyevsasha.core.models.ThemeEnum

interface OnThemeChangedCallback {
    fun onThemeChanged(theme: ThemeEnum)
}
