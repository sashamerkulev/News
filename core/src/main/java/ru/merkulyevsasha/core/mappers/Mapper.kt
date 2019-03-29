package ru.merkulyevsasha.core.mappers

interface Mapper<TIn, TOut> {
    fun map(item: TIn): TOut
}