package ru.merkulyevsasha

import ru.merkulyevsasha.core.ServiceLocator

interface RequireServiceLocator {
    fun setServiceLocator(serviceLocator: ServiceLocator)
}