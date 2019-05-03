package ru.merkulyevsasha.domain

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.merkulyevsasha.core.domain.ArticlesInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.ArticlesApiRepository
import ru.merkulyevsasha.core.repositories.DatabaseRepository
import ru.merkulyevsasha.domain.mappers.SourceNameMapper
import java.util.*

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

    override fun getFavoriteArticles(): Single<List<Article>> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun likeArticle(article: Article): Single<Article> {
        return articlesApiRepository.likeArticle(article.articleId)
            .doOnSuccess {
                databaseRepository.updateLikeArticle(it)
            }
            .map { sourceNameMapper.map(it) }
            .subscribeOn(Schedulers.io())
    }

    override fun dislikeArticle(article: Article): Single<Article> {
        return articlesApiRepository.dislikeArticle(article.articleId)
            .doOnSuccess {
                databaseRepository.updateDislikeArticle(it)
            }
            .map { sourceNameMapper.map(it) }
            .subscribeOn(Schedulers.io())
    }
}