package ru.merkulyevsasha.news.newsjobs;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by sasha_merkulev on 10.07.2017.
 */

public class NewsJobCreator implements JobCreator {
    @Override
    public Job create(String tag) {
        return new NewsJob();
    }
}
