package com.bsouffle.mygreatbooks;

/**
 * Created by Benoit on 26/04/2014.
 */
public final class Intents {
    private Intents() {
    }

    public static final class SearchBookContents {
        /**
         * Use Google Book Search to search the contents of the book provided.
         */
        public static final String ACTION = "com.google.zxing.client.android.SEARCH_BOOK_CONTENTS";

        /**
         * The book to search, identified by ISBN number.
         */
        public static final String ISBN = "ISBN";

        /**
         * An optional field which is the text to search for.
         */
        public static final String QUERY = "QUERY";

        private SearchBookContents() {
        }
    }
}
