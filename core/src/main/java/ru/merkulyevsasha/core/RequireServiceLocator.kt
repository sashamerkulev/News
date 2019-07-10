package ru.merkulyevsasha.core

import ru.merkulyevsasha.core.ServiceLocator

interface RequireServiceLocator {
    fun setServiceLocator(serviceLocator: ServiceLocator)
}