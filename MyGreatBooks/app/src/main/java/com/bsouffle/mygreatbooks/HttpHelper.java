package com.bsouffle.mygreatbooks;

import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Utility methods for retrieving content over HTTP using the more-supported {@code java.net} classes
 * in Android.
 */
public final class HttpHelper {

    private static final String TAG = HttpHelper.class.getSimpleName();

    private HttpHelper() {
    }

    /**
     * Downloads the entire resource instead of part.
     *
     * @param uri  URI to retrieve
     * @param type expected text-like MIME type of that content
     * @return content as a {@code String}
     * @throws IOException if the content can't be retrieved because of a bad URI, network problem, etc.
     * @see #downloadViaHttp(String, HttpHelper.ContentType, int)
     */
    public static CharSequence downloadViaHttp(String uri, ContentType type) throws IOException {
        return downloadViaHttp(uri, type, Integer.MAX_VALUE);
    }

    /**
     * @param uri      URI to retrieve
     * @param type     expected text-like MIME type of that content
     * @param maxChars approximate maximum characters to read from the source
     * @return content as a {@code String}
     * @throws IOException if the content can't be retrieved because of a bad URI, network problem, etc.
     */
    public static CharSequence downloadViaHttp(String uri, ContentType type, int maxChars) throws IOException {
        String contentTypes;
        switch (type) {
            case HTML:
                contentTypes = "application/xhtml+xml,text/html,text/*,*/*";
                break;
            case JSON:
                contentTypes = "application/json,text/*,*/*";
                break;
            case XML:
                contentTypes = "application/xml,text/*,*/*";
                break;
            case TEXT:
            default:
                contentTypes = "text/*,*/*";
        }
        return downloadViaHttp(uri, contentTypes, maxChars);
    }

    private static CharSequence downloadViaHttp(String uri, String contentTypes, int maxChars) throws IOException {
        int redirects = 0;
        while (redirects < 5) {
            URL url = new URL(uri);
            HttpURLConnection connection = safelyOpenConnection(url);
            connection.setInstanceFollowRedirects(true); // Won't work HTTP -> HTTPS or vice versa
            connection.setRequestProperty("Accept", contentTypes);
            connection.setRequestProperty("Accept-Charset", "utf-8,*");
            connection.setRequestProperty("User-Agent", "ZXing (Android)");
            try {
                int responseCode = safelyConnect(connection);
                switch (responseCode) {
                    case HttpURLConnection.HTTP_OK:
                        return consume(connection, maxChars);
                    case HttpURLConnection.HTTP_MOVED_TEMP:
                        String location = connection.getHeaderField("Location");
                        if (location != null) {
                            uri = location;
                            redirects++;
                            continue;
                        }
                        throw new IOException("No Location");
                    default:
                        throw new IOException("Bad HTTP response: " + responseCode);
                }
            } finally {
                connection.disconnect();
            }
        }
        throw new IOException("Too many redirects");
    }

    private static String getEncoding(URLConnection connection) {
        String contentTypeHeader = connection.getHeaderField("Content-Type");
        if (contentTypeHeader != null) {
            int charsetStart = contentTypeHeader.indexOf("charset=");
            if (charsetStart >= 0) {
                return contentTypeHeader.substring(charsetStart + "charset=".length());
            }
        }
        return "UTF-8";
    }

    private static CharSequence consume(URLConnection connection, int maxChars) throws IOException {
        String encoding = getEncoding(connection);
        StringBuilder out = new StringBuilder();
        Reader in = null;
        try {
            in = new InputStreamReader(connection.getInputStream(), encoding);
            char[] buffer = new char[1024];
            int charsRead;
            while (out.length() < maxChars && (charsRead = in.read(buffer)) > 0) {
                out.append(buffer, 0, charsRead);
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException | NullPointerException ioe) {
                    // continue
                }
            }
        }
        return out;
    }

    private static HttpURLConnection safelyOpenConnection(URL url) throws IOException {
        URLConnection conn;
        try {
            conn = url.openConnection();
        } catch (NullPointerException npe) {
            // Another strange bug in Android?
            Log.w(TAG, "Bad URI? " + url);
            throw new IOException(npe);
        }
        if (!(conn instanceof HttpURLConnection)) {
            throw new IOException();
        }
        return (HttpURLConnection) conn;
    }

    private static int safelyConnect(HttpURLConnection connection) throws IOException {
        try {
            connection.connect();
        } catch (NullPointerException | IllegalArgumentException | IndexOutOfBoundsException | SecurityException e) {
            // this is an Android bug: http://code.google.com/p/android/issues/detail?id=16895
            throw new IOException(e);
        }
        try {
            return connection.getResponseCode();
        } catch (NullPointerException | StringIndexOutOfBoundsException | IllegalArgumentException e) {
            // this is maybe this Android bug: http://code.google.com/p/android/issues/detail?id=15554
            throw new IOException(e);
        }
    }

    public enum ContentType {
        /**
         * HTML-like content type, including HTML, XHTML, etc.
         */
        HTML,
        /**
         * JSON content
         */
        JSON,
        /**
         * XML
         */
        XML,
        /**
         * Plain text content
         */
        TEXT,
    }

}