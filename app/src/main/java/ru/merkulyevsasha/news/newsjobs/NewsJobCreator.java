package ru.merkulyevsasha.news.newsjobs;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

import javax.inject.Inject;

import ru.merkulyevsasha.news.domain.NewsInteractor;

public class NewsJobCreator implements JobCreator {

    private final NewsInteractor newsInteractor;

    @Inject
    NewsJobCreator(NewsInteractor newsInteractor){
        this.newsInteractor = newsInteractor;
    }

    @Override
    public Job create(@NonNull String tag) {
        return new NewsJob(newsInteractor);
    }
}
