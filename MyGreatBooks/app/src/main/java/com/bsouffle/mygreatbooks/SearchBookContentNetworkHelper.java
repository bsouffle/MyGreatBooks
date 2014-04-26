package com.bsouffle.mygreatbooks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Benoit on 26/04/2014.
 */
public class SearchBookContentNetworkHelper {

    private static final String TAG = SearchBookContentNetworkHelper.class.getSimpleName();

    public static JSONObject getBookSearchJsonResult(String isbn) {
        try {
            if (true) { // Check if valid ISBN
                String uri = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn;
                CharSequence content = HttpHelper.downloadViaHttp(uri, HttpHelper.ContentType.JSON);
                return new JSONObject(content.toString());
            }

        } catch (IOException | JSONException e) {
            Log.w(TAG, "Error accessing book search", e);
        }

        return null; // TODO: Throw exception
    }

    public static SearchBookContentsResult getSearchBookContentResultFromJson(JSONObject json, String isbn) {
        try {
            Log.v(TAG, json.toString());
            int count = json.getInt("totalItems");
            if (count > 0) {
                return parseResult(json.getJSONArray("items").getJSONObject(0), isbn);
            }
        } catch (JSONException e) {
            Log.w(TAG, "Bad JSON from book search", e);
        }

        return null; // TODO: Throw Exception
    }

    // Available fields: page_id, page_number, snippet_text
    private static SearchBookContentsResult parseResult(JSONObject json, String isbn) {
        SearchBookContentsResult searchBookContentsResult = null;
        try {
            JSONObject volumeInfo = json.getJSONObject("volumeInfo");
            if (volumeInfo != null) {
                searchBookContentsResult = new SearchBookContentsResult(
                        isbn,
                        volumeInfo.getString("title"),
                        volumeInfo.getJSONArray("authors").getString(0),
                        volumeInfo.getString("publisher"),
                        volumeInfo.getString("description"),
                        volumeInfo.optInt("pageCount"),
                        volumeInfo.optDouble("averageRating"),
                        volumeInfo .optInt("ratingsCount"));

                JSONObject imgObject = json.getJSONObject("imageLinks");
                if (imgObject != null) {
                    searchBookContentsResult.setImageUrl(imgObject.optString("thumbnail", null));
                }
            }
        } catch (JSONException e) {
            Log.w(TAG, e);
        }

        return searchBookContentsResult;
    }

}
