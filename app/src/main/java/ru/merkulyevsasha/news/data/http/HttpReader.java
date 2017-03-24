package ru.merkulyevsasha.news.data.http;

import android.text.Html;
import android.util.Xml;

import com.google.firebase.crash.FirebaseCrash;

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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.merkulyevsasha.news.pojos.ItemNews;

public class HttpReader {

    private final OkHttpClient mClient;

    public HttpReader() {
        mClient = new OkHttpClient();
    }

    private boolean tryParseDateFormat(String date, String formatDate, ItemNews item){
        try{
            SimpleDateFormat format = new SimpleDateFormat(formatDate, Locale.ENGLISH);
            item.PubDate = format.parse(date);
            return true;
        }
        catch(ParseException e){
            e.printStackTrace();
            return false;
        }
    }

    private List<ItemNews> parseXML(XmlPullParser parser, int navId) throws XmlPullParserException, IOException {
        List<ItemNews> items = new ArrayList<>();
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

    public List<ItemNews> GetHttpData(int navId, String url)
    {
        List<ItemNews> result = new ArrayList<>();

        try {

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = mClient.newCall(request).execute();
            String jsonText = response.body().string();
            XmlPullParser parser = Xml.newPullParser();

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

            InputStream stream = new ByteArrayInputStream(jsonText.getBytes("UTF-8"));

            parser.setInput(stream, "UTF-8");

            result = parseXML(parser, navId);
        } catch (Exception e) {
            FirebaseCrash.report(e);
            e.printStackTrace();
        }
        return result;
    }

}
