package ru.merkulyevsasha.domain

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.ArticlesApiRepository
import ru.merkulyevsasha.core.repositories.DatabaseRepository
import ru.merkulyevsasha.domain.mappers.SourceNameMapper
import java.util.Date

class ArticlesInteractorImpl(
    private val articlesApiRepository: ArticlesApiRepository,
    private val keyValueStorage: KeyValueStorage,
    private val databaseRepository: DatabaseRepository,
    private val sourceNameMapper: SourceNameMapper
) : ArticlesInteractor {

    override fun getArticles(): Single<List<Article>> {
        return databaseRepository.getArticles()
            .flatMap { items ->
                if (items.isEmpty()) refreshAndGetArticles()
                else Single.just(items.map { sourceNameMapper.map(it) })
            }
            .subscribeOn(Schedulers.io())
    }

    override fun refreshAndGetArticles(): Single<List<Article>> {
        return Single.fromCallable { keyValueStorage.getLastArticleReadDate() ?: Date(0) }
            .flatMap {
                articlesApiRepository.getArticles(it)
                    .doOnSuccess { items ->
                        if (items.isNotEmpty()) {
                            databaseRepository.addOrUpdateArticles(items)
                            keyValueStorage.setLastArticleReadDate(Date())
                        }
                    }
            }
            .flattenAsFlowable { it }
            .map { sourceNameMapper.map(it) }
            .toList()
            .subscribeOn(Schedulers.io())
    }

    override fun getUserActivityArticles(): Single<List<Article>> {
        return articlesApiRepository.getUserActivityArticles()
            .flattenAsFlowable { it }
            .map { sourceNameMapper.map(it) }
            .toList()
            .subscribeOn(Schedulers.io())
    }

    override fun likeArticle(articleId: Int): Single<Article> {
        return articlesApiRepository.likeArticle(articleId)
            .doOnSuccess {
                databaseRepository.updateArticle(it)
            }
            .map { sourceNameMapper.map(it) }
            .subscribeOn(Schedulers.io())
    }

    override fun dislikeArticle(articleId: Int): Single<Article> {
        return articlesApiRepository.dislikeArticle(articleId)
            .doOnSuccess {
                databaseRepository.updateArticle(it)
            }
            .map { sourceNameMapper.map(it) }
            .subscribeOn(Schedulers.io())
    }

    override fun getArticle(articleId: Int): Single<Article> {
        return articlesApiRepository.getArticle(articleId)
            .doOnSuccess {
                databaseRepository.updateArticle(it)
            }
            .map { sourceNameMapper.map(it) }
            .subscribeOn(Schedulers.io())
    }
}