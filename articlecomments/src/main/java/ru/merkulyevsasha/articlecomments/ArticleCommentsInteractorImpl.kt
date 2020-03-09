package ru.merkulyevsasha.articlecomments

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import ru.merkulyevsasha.core.domain.ArticleCommentsInteractor
import ru.merkulyevsasha.core.models.Article
import ru.merkulyevsasha.core.models.ArticleComment
import ru.merkulyevsasha.core.preferences.KeyValueStorage
import ru.merkulyevsasha.core.repositories.ArticleCommentsApiRepository
import ru.merkulyevsasha.core.repositories.ArticlesApiRepository
import ru.merkulyevsasha.core.repositories.NewsDatabaseRepository
import java.util.*

class ArticleCommentsInteractorImpl(
    private val articlesApiRepository: ArticlesApiRepository,
    private val articleCommentsApiRepository: ArticleCommentsApiRepository,
    private val keyValueStorage: KeyValueStorage,
    private val databaseRepository: NewsDatabaseRepository
) : ArticleCommentsInteractor {

    private val _rssSourceNameMap = mutableMapOf<String, String>()
    private val rssSourceNameMap : Map<String, String>
        get() {
            if (_rssSourceNameMap.isEmpty()) {
                val map = databaseRepository.getRssSources().associateBy({ it.sourceId }, { it.sourceName })
                _rssSourceNameMap.putAll(map)
            }
            return _rssSourceNameMap
        }

    override fun getArticleComments(articleId: Int): Single<Pair<Article, List<ArticleComment>>> {
        return Single.zip(
            databaseRepository.getArticle(articleId),
            databaseRepository.getArticleComments(articleId),
            BiFunction { t1: Article, t2: List<ArticleComment> -> t1 to t2 }
        ).flatMap { pair ->
            if (pair.second.isEmpty()) refreshAndGetArticleComments(articleId)
            else Single.just(pair)
        }
            .subscribeOn(Schedulers.io())
    }

    override fun refreshAndGetArticleComments(articleId: Int): Single<Pair<Article, List<ArticleComment>>> {
        return Single.fromCallable { keyValueStorage.getLastArticleCommentReadDate() ?: Date(0) }
            .flatMap {
                Single.zip(
                    articlesApiRepository.getArticle(articleId, rssSourceNameMap)
                        .doOnSuccess { item ->
                            databaseRepository.updateArticle(item)
                        },
                    articleCommentsApiRepository.getArticleComments(articleId, it)
                        .doOnSuccess { items ->
                            if (items.isNotEmpty()) {
                                databaseRepository.addOrUpdateArticleComments(items)
                                keyValueStorage.setLastArticleCommentReadDate(Date())
                            }
                        }
                    ,
                    BiFunction { t1: Article, t2: List<ArticleComment> -> t1 to t2 }
                )
            }
            .subscribeOn(Schedulers.io())
    }

    override fun addArticleComment(articleId: Int, comment: String): Single<ArticleComment> {
        return articleCommentsApiRepository.addArticleComment(articleId, comment)
            .doOnSuccess {
                databaseRepository.updateArticleComment(it, 1)
            }
            .subscribeOn(Schedulers.io())
    }

    override fun likeArticleComment(commentId: Int): Single<ArticleComment> {
        return articleCommentsApiRepository.likeArticleComment(commentId)
            .subscribeOn(Schedulers.io())
    }

    override fun dislikeArticleComment(commentId: Int): Single<ArticleComment> {
        return articleCommentsApiRepository.dislikeArticleComment(commentId)
            .subscribeOn(Schedulers.io())
    }

    override fun deleteArticleComment(commentId: Int): Completable {
        return articleCommentsApiRepository.deleteArticleComment(commentId)
            .subscribeOn(Schedulers.io())
    }
}
