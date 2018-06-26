package ru.merkulyevsasha.news.data.prefs;

import io.reactivex.Single;

public interface NewsSharedPreferences {
    Single<Boolean> getFirstRunFlag();

    void setFirstRunFlag();
}
