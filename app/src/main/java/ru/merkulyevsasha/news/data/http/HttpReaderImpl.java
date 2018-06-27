package ru.merkulyevsasha.news.data.http;

import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Xml;

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

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.merkulyevsasha.news.models.Article;

public class HttpReaderImpl implements HttpReader {

    private final OkHttpClient okHttpClient;

    @Inject
    public HttpReaderImpl(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    private boolean tryParseDateFormat(String date, String formatDate, Article item){
        try{
            SimpleDateFormat format = new SimpleDateFormat(formatDate, Locale.ENGLISH);
            item.setPubDate(format.parse(date));
            return true;
        }
        catch(ParseException e){
            e.printStackTrace();
            return false;
        }
    }

    private List<Article> parseXML(XmlPullParser parser, int navId) throws XmlPullParserException, IOException {
        List<Article> items = new ArrayList<>();
        int eventType = parser.getEventType();
        Article item = null;

        while (eventType != XmlPullParser.END_DOCUMENT){
            String name;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equals("item")){
                        item = new Article();
                        item.setSourceNavId (navId);
                    } else if (item != null){
                        switch (name) {
                            case "title":
                                item.setTitle(parser.nextText());
                                break;
                            case "description":
                                String description = parser.nextText();
                                if (description != null) {
                                    description = Html.fromHtml(description).toString();
                                }
                                item.setDescription(description);
                                break;
                            case "link":
                                if (item.getLink() == null || item.getLink().isEmpty()) {
                                    item.setLink(parser.nextText());
                                }
                                break;
                            case "pdalink":
                                item.setLink(parser.nextText());
                                break;
                            case "enclosure":
                                try {
                                    for( int i = 0; i < parser.getAttributeCount(); i++) {
                                        String url = parser.getAttributeValue(i);
                                        if (url != null && url.trim().endsWith("jpg"))
                                            item.setPictureUrl(url.trim());
                                        //System.out.println(url);
                                    }
                                } catch(Exception e){
                                    e.printStackTrace();
                                }
                                break;
                            case "media:thumbnail":
                                try {
                                    for( int i = 0; i < parser.getAttributeCount(); i++) {
                                        String url = parser.getAttributeValue(i);
                                        if (url != null && url.trim().endsWith("jpg"))
                                            item.setPictureUrl(url.trim());
                                        //System.out.println(url);
                                    }
                                } catch(Exception e){
                                    e.printStackTrace();
                                }
                                break;
                            case "pubDate":
                                String pubDate = parser.nextText();
                                if (!tryParseDateFormat(pubDate, "E, dd MMM yyyy HH:mm:ss z", item))
                                    if (!tryParseDateFormat(pubDate, "dd MMM yyyy HH:mm:ss z", item))
                                        tryParseDateFormat(pubDate, "d MMM yyyy HH:mm:ss z", item);
                                break;
                            case "category":
                                item.setCategory(parser.nextText());
                                break;
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("item") && item != null && item.getPubDate() != null){
                        String description = item.getDescription() == null ? "" :item.getDescription().toLowerCase();
                        String title = item.getTitle() == null ? "" :item.getTitle().toLowerCase();
                        String category = item.getCategory() == null ? "" :item.getCategory().toLowerCase();
                        item.setSearch(title + category + description);
                        items.add(item);
                    }
            }
            eventType = parser.next();
        }

        return items;

    }

    @NonNull
    @Override
    public List<Article> getHttpData(int navId, @NonNull String url)
    {
        List<Article> result = new ArrayList<>();

        try {

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            String jsonText = response.body().string();
            XmlPullParser parser = Xml.newPullParser();

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

            InputStream stream = new ByteArrayInputStream(jsonText.getBytes("UTF-8"));

            parser.setInput(stream, "UTF-8");

            result = parseXML(parser, navId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
