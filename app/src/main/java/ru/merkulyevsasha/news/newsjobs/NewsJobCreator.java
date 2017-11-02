package ru.merkulyevsasha.news.newsjobs;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;


import javax.inject.Inject;

import ru.merkulyevsasha.news.data.utils.NewsConstants;
import ru.merkulyevsasha.news.domain.NewsInteractor;

/**
 * Created by sasha_merkulev on 10.07.2017.
 */

public class NewsJobCreator implements JobCreator {

    private final NewsConstants newsConstants;
    private final NewsInteractor newsInteractor;

    @Inject
    public NewsJobCreator(NewsConstants newsConstants, NewsInteractor newsInteractor){
        this.newsConstants = newsConstants;
        this.newsInteractor = newsInteractor;
    }

    @Override
    public Job create(String tag) {
        return new NewsJob(newsConstants, newsInteractor);
    }
}
