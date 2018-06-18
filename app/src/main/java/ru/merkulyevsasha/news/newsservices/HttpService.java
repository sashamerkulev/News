package ru.merkulyevsasha.news.newsservices;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.firebase.crash.FirebaseCrash;

import java.util.Map;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import ru.merkulyevsasha.news.R;
import ru.merkulyevsasha.news.data.utils.NewsConstants;
import ru.merkulyevsasha.news.domain.NewsInteractor;
import ru.merkulyevsasha.news.helpers.BroadcastHelper;

import static ru.merkulyevsasha.news.presentation.main.MainActivity.KEY_NAV_ID;

public class HttpService extends IntentService {

    @Inject BroadcastHelper broadcastHelper;
    @Inject NewsConstants newsConstants;
    @Inject NewsInteractor newsInteractor;

    public HttpService(String name) {
        super(name);
    }

    public HttpService() {
        this("HttpService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) return;
        AndroidInjection.inject(this);
        final int mNavId = intent.getIntExtra(KEY_NAV_ID, -1);
        if (mNavId > 0) {
            if (mNavId == R.id.nav_all) {
                for (Map.Entry<Integer, String> entry : newsConstants.getLinks().entrySet()) {
                    try {
//                        if (newsInteractor.readNewsAndSaveToDb(entry.getKey(), entry.getValue())){
//                            broadcastHelper.sendUpdateBroadcast();
//                        }
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
            } else {
                newsInteractor.readNewsAndSaveToDb(mNavId, newsConstants.getLinkByNavId(mNavId));
            }
        }
        broadcastHelper.sendFinishBroadcast();
    }
}
