package ru.merkulyevsasha.core

interface ResourceProvider {
    fun getString(id: Int): String
}