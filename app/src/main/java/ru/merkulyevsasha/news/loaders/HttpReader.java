package ru.merkulyevsasha.news.loaders;

import android.content.Context;
import android.text.Html;
import android.util.Log;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import ru.merkulyevsasha.news.R;
import ru.merkulyevsasha.news.db.DatabaseHelper;
import ru.merkulyevsasha.news.models.Const;
import ru.merkulyevsasha.news.models.ItemNews;


public class HttpReader {


    private final int mNavId;
    private final DatabaseHelper mHelper;
    private final Const mConst;

    public HttpReader(Context context, int navId) {

        mHelper = DatabaseHelper.getInstance(DatabaseHelper.getDbPath(context));
        mConst = new Const();

        mNavId = navId;
    }

    private boolean tryParseDateFormat(String date, String formatDate, ItemNews item){
        try{
            SimpleDateFormat format = new SimpleDateFormat(formatDate, Locale.ENGLISH);
            item.PubDate = format.parse(date);
            return true;
        }
        catch(ParseException e){
            //FirebaseCrash.report(e);
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
                                    if (!tryParseDateFormat(pubDate, "dd MMM yyyy HH:mm:ss z", item))
                                        tryParseDateFormat(pubDate, "d MMM yyyy HH:mm:ss z", item);
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
            Log.d("GetHttpData", url);
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
            for (Map.Entry<Integer, String> entry : mConst.getLinks().entrySet()) {
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
            result = GetHttpData(httpclient, mNavId, mConst.getLinkByNavId(mNavId));
            mHelper.delete(mNavId);
            mHelper.addListNews(result);
        }

    }


}
