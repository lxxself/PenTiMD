package com.lxxself.pentimd.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lxxself.pentimd.model.ItemObj;
import com.socks.library.KLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.ConnectException;

import cz.msebera.android.httpclient.Header;
import io.realm.Realm;

/**
 * Created by lxxself on 2015/11/18.
 */
public class JSONUtil {
    public static void getList(Context context,JSONObject jsonObject, String type) throws JSONException {
        Realm realm = Realm.getInstance(context);
        JSONArray array = jsonObject.getJSONArray("data");
        realm.beginTransaction();
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            if (!object.getString("title").equals("AD") && !TextUtils.isEmpty(object.getString("title"))) {

                String title = object.getString("title");
                String link = object.getString("link").replace("http", "https");
                int id = Integer.parseInt(link.substring(link.indexOf("id=") + 3));

                ItemObj itemObj = new ItemObj();
                itemObj.setId(id);
                itemObj.setTitle(object.getString("title"));
                itemObj.setType(type);
                itemObj.setLink(link);
                itemObj.setMobileLink(link.replace("more", "readapp2"));
                itemObj.setPubDate(object.getString("pubDate"));
                itemObj.setAuthor(object.getString("author"));
                if (object.has("imgurl")) {
                    itemObj.setImgUrl(object.getString("imgurl").replace("square","medium"));
                }
                if (object.has("description")) {
                    String description = object.getString("description");
                    if (!object.getString("description").startsWith("https")) {
                        //无效果
                        itemObj.setDescription(description.replace("\n", ""));
                    }
                    if (type.equals("yitu")) {
                        getDescription(context,id,description);
                    }
                }
                realm.copyToRealmOrUpdate(itemObj);

//                    KLog.d("JSONUtil",realm.where(ItemObj.class).findAll().get(0).getTitle());
            }

        }
        realm.commitTransaction();
    }

    private static void getDescription(final Context context, final int id, String description) {
        description = description.replace("https", "http");
        RestClient.get(description, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                KLog.d("getDescription", new String(bytes));
                String temp = JsoupParse(new String(bytes));
                Realm realm = Realm.getInstance(context);
                realm.beginTransaction();
                realm.where(ItemObj.class).equalTo("id", id).findFirst().setDescription(temp);
                realm.commitTransaction();
                KLog.d("getDescription", temp);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                KLog.d("getDescription", "onFailure ");
            }
        });





    }

    private static String JsoupParse(String input) {

        Document doc = Jsoup.parse(input, "UTF-8");
        doc.select("img").remove();
        doc.select("script").remove();
        Element content = doc.body();
        String word = getChinese(content.toString());
        word = word.replace("移动", "").replace("（", "").replace("）", "");

        return word;
    }
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }
    public static String getChinese(String strName) {
        StringBuffer sb=new StringBuffer();
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
