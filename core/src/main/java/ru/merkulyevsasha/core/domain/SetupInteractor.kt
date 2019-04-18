package ru.merkulyevsasha.core.domain

import io.reactivex.Completable

interface SetupInteractor {
    fun registerSetup(setupId: String, getFirebaseId: () -> String): Completable
}