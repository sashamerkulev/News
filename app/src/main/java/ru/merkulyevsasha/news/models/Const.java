package ru.merkulyevsasha.news.models;


import java.util.HashMap;
import java.util.Map;

import ru.merkulyevsasha.news.R;

public final class Const {

    private final Map<Integer, String> mLinks = new HashMap<>();
    private final Map<Integer, Integer> mSources = new HashMap<>();

    public Map<Integer, String> getLinks(){
        return mLinks;
    }

    public Const(){

        mLinks.put(R.id.nav_lenta, "http://lenta.ru/rss");
        mLinks.put(R.id.nav_rbc, "http://static.feed.rbc.ru/rbc/internal/rss.rbc.ru/rbc.ru/mainnews.rss");
        mLinks.put(R.id.nav_wot, "http://worldoftanks.ru/ru/rss/news/");
        //mLinks.put(R.id.nav_topwar, "http://topwar.ru/rss.xml");
        mLinks.put(R.id.nav_interfax, "http://www.interfax.ru/rss.asp");

        mLinks.put(R.id.nav_vesti, "http://www.vesti.ru/vesti.rss");
        mLinks.put(R.id.nav_rt, "http://russian.rt.com/rss/");
//        mLinks.put(R.id.nav_inosmi, "http://inosmi.ru/export/rss2/index.xml");
//        mLinks.put(R.id.nav_ria, "http://ria.ru/export/rss2/world/index.xml");
        mLinks.put(R.id.nav_planetanovosti, "http://www.planetanovosti.com/news/rss/");
        mLinks.put(R.id.nav_ramblernews, "https://news.rambler.ru/rss/world/");
        mLinks.put(R.id.nav_newsrucom, "http://rss.newsru.com/world/");
        mLinks.put(R.id.nav_mixednews, "http://mixednews.ru/feed/");

        mLinks.put(R.id.nav_rg, "http://rg.ru/xml/index.xml");
        mLinks.put(R.id.nav_ng, "http://www.ng.ru/rss/");
        mLinks.put(R.id.nav_kp, "http://www.kp.ru/rss/allsections.xml");
        mLinks.put(R.id.nav_km, "http://www.km.ru/rss/main");
        mLinks.put(R.id.nav_aftershock, "http://feeds.feedburner.com/aftershock/news");

        mLinks.put(R.id.nav_odnako, "http://otredakcii.odnako.org/rss/");
        mLinks.put(R.id.nav_aif, "http://www.aif.ru/rss/all.php");
        mLinks.put(R.id.nav_bbcrusshian, "http://feeds.bbci.co.uk/russian/rss.xml");
        mLinks.put(R.id.nav_tass, "http://tass.ru/rss/v2.xml");
        mLinks.put(R.id.nav_nauka, "http://www.nkj.ru/rss/");

        mLinks.put(R.id.nav_politikaru, "http://polytika.ru/feed");
        mLinks.put(R.id.nav_mk, "http://www.mk.ru/rss/index.xml");
        mLinks.put(R.id.nav_cnews, "http://www.cnews.ru/inc/rss/news.xml");
        mLinks.put(R.id.nav_mailru, "https://news.mail.ru/rss/91/");
        mLinks.put(R.id.nav_sportexpress, "http://www.sport-express.ru/services/materials/news/se/");

        mSources.put(R.id.nav_all, R.string.news_all);
        mSources.put(R.id.nav_lenta, R.string.news_lenta);
        mSources.put(R.id.nav_rbc, R.string.news_rbc);
        mSources.put(R.id.nav_wot, R.string.wot);
        //mSources.put(R.id.nav_topwar, R.string.news_topwar);
        mSources.put(R.id.nav_interfax, R.string.news_interfax);

        mSources.put(R.id.nav_vesti, R.string.news_vesti);
        mSources.put(R.id.nav_rt, R.string.news_rt);
//        mSources.put(R.id.nav_inosmi, R.string.news_inosmi);
//        mSources.put(R.id.nav_ria, R.string.news_rianovosti);
        mSources.put(R.id.nav_ramblernews, R.string.news_rambler);
        mSources.put(R.id.nav_planetanovosti, R.string.news_planetanews);
        mSources.put(R.id.nav_newsrucom, R.string.news_newsrucom);

        mSources.put(R.id.nav_mixednews, R.string.news_mixednews);

        mSources.put(R.id.nav_rg, R.string.news_rg);
        mSources.put(R.id.nav_ng, R.string.news_ng);
        mSources.put(R.id.nav_kp, R.string.news_kp);
        mSources.put(R.id.nav_km, R.string.news_km);
        mSources.put(R.id.nav_aftershock, R.string.news_aftershock);

        mSources.put(R.id.nav_odnako, R.string.news_odnako);
        mSources.put(R.id.nav_aif, R.string.news_aif);
        mSources.put(R.id.nav_bbcrusshian, R.string.news_bbc_russian);
        mSources.put(R.id.nav_tass, R.string.news_tass);
        mSources.put(R.id.nav_nauka, R.string.news_nauka);

        mSources.put(R.id.nav_politikaru, R.string.news_politikaru);
        mSources.put(R.id.nav_mk, R.string.news_mk);
        mSources.put(R.id.nav_cnews, R.string.news_cnews);
        mSources.put(R.id.nav_mailru, R.string.news_mailru);
        mSources.put(R.id.nav_sportexpress, R.string.news_sportexpress);

    }

    public String getLinkByNavId(int navId){

        return mLinks.containsKey(navId) ? mLinks.get(navId) : "";
    }

    public int getTitleByNavId(int navId){

        return mSources.containsKey(navId) ? mSources.get(navId) : -1;
    }

}
