package com.bsouffle.mygreatbooks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Benoit on 26/04/2014.
 */
public final class SaveImageHelper {

    private static final String TAG = SearchBookContentNetworkHelper.class.getSimpleName();

    public static Bitmap saveImageUrlToBitmapFormat(String url) {
        Bitmap bitmapImg = null;
        try {
            InputStream inputStream = new URL(url).openStream();
            BufferedInputStream thumbBuff = new BufferedInputStream(inputStream);
            bitmapImg = BitmapFactory.decodeStream(thumbBuff);
            inputStream.close();
            thumbBuff.close();
        } catch(Exception e) {
            Log.v(TAG, e.getMessage());
        }

        return bitmapImg;
    }
}
