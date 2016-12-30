package ru.merkulyevsasha.news.loaders;

import android.content.Context;
import android.text.Html;
import android.util.Xml;

import com.google.firebase.crash.FirebaseCrash;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import ru.merkulyevsasha.news.R;
import ru.merkulyevsasha.news.db.DatabaseHelper;
import ru.merkulyevsasha.news.models.ItemNews;


public class HttpReader {


    private final int mNavId;
    private final DatabaseHelper mHelper;

    private final Map<Integer, String> mLinks = new HashMap<Integer, String>();

    public HttpReader(Context context, int navId) {

        mHelper = DatabaseHelper.getInstance(context);

        mNavId = navId;

        mLinks.put(R.id.nav_lenta, "http://lenta.ru/rss");
        mLinks.put(R.id.nav_rbc, "http://static.feed.rbc.ru/rbc/internal/rss.rbc.ru/rbc.ru/mainnews.rss");
        mLinks.put(R.id.nav_wot, "http://worldoftanks.ru/ru/rss/news/");
        //mLinks.put(R.id.nav_topwar, "http://topwar.ru/rss.xml");
        mLinks.put(R.id.nav_interfax, "http://www.interfax.ru/rss.asp");

        mLinks.put(R.id.nav_vesti, "http://www.vesti.ru/vesti.rss");
        mLinks.put(R.id.nav_rt, "http://russian.rt.com/rss/");
//        mLinks.put(R.id.nav_inosmi, "http://inosmi.ru/export/rss2/index.xml");
//        mLinks.put(R.id.nav_ria, "http://ria.ru/export/rss2/world/index.xml");
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

    }

    private boolean tryParseDateFormat(String date, String formatDate, ItemNews item){
        try{
            SimpleDateFormat format = new SimpleDateFormat(formatDate, Locale.ENGLISH);
            item.PubDate = format.parse(date);
            return true;
        }
        catch(ParseException e){
            FirebaseCrash.report(e);
            e.printStackTrace();
            return false;
        }
    }

    private List<ItemNews> parseXML(XmlPullParser parser, int navId) throws XmlPullParserException, IOException {
        List<ItemNews> items = new ArrayList<ItemNews>();
        int eventType = parser.getEventType();
        ItemNews item = null;

        while (eventType != XmlPullParser.END_DOCUMENT){
            String name;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equals("item")){
                        item = new ItemNews();
                        item.SourceNavId = navId;
                    } else if (item != null){
                        switch (name) {
                            case "title":
                                item.Title = parser.nextText();
                                break;
                            case "description":
                                String description = parser.nextText();
                                if (description != null) {
                                    description = Html.fromHtml(description).toString();
                                }
                                item.Description = description;
                                break;
                            case "link":
                                item.Link = parser.nextText();
                                break;
                            case "pubDate":
                                String pubDate = parser.nextText();
                                if (!tryParseDateFormat(pubDate, "E, dd MMM yyyy HH:mm:ss z", item))
                                    tryParseDateFormat(pubDate, "dd MMM yyyy HH:mm:ss z", item);
                                break;
                            case "category":
                                item.Category = parser.nextText();
                                break;
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("item") && item != null && item.PubDate != null){
                        String description = item.Description == null ? "" :item.Description.toLowerCase();
                        String title = item.Title == null ? "" :item.Title.toLowerCase();
                        String category = item.Category == null ? "" :item.Category.toLowerCase();
                        item.Search =  title + category + description;
                        items.add(item);
                    }
            }
            eventType = parser.next();
        }

        return items;

    }

    private List<ItemNews> GetHttpData(HttpClient httpclient, int navId, String url)
    {
        List<ItemNews> result = new ArrayList<ItemNews>();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response;
        try {
            response = httpclient.execute(httpget);
            if(response.getStatusLine().getStatusCode() == 200){
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String jsonText = EntityUtils.toString(entity, HTTP.UTF_8);
                    XmlPullParser parser = Xml.newPullParser();

                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

                    InputStream stream = new ByteArrayInputStream(jsonText.getBytes("UTF-8"));

                    parser.setInput(stream, "UTF-8");

                    result=  parseXML(parser, navId);
                }
            }
        } catch (Exception e) {
            FirebaseCrash.report(e);
            e.printStackTrace();
        }
        return result;
    }


    public void load(){

        HttpClient httpclient = new DefaultHttpClient();
        List<ItemNews> result = new ArrayList<ItemNews>();

        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
            public boolean verify(String string,SSLSession ssls) {
                return true;
            }
        });

        if (mNavId == R.id.nav_all){
            for (Map.Entry<Integer, String> entry : mLinks.entrySet()) {
                Integer key = entry.getKey();
                String url = entry.getValue();
                List<ItemNews> items = GetHttpData(httpclient, key, url);
                if (items != null) {
                    result.addAll(items);
                }
            }
            mHelper.deleteAll();
            mHelper.addListNews(result);
        } else {
            result = GetHttpData(httpclient, mNavId, mLinks.get(mNavId));
            mHelper.delete(mNavId);
            mHelper.addListNews(result);
        }

    }


}
