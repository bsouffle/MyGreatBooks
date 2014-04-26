package com.bsouffle.mygreatbooks;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;


public class SearchBookContentActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book_content);

        Button button = (Button) findViewById(R.id.scanButton);
        button.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_book_content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.scanButton) {
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            new GetBookInfos().execute(scanningResult.getContents());
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private final class GetBookInfos extends AsyncTask<String, Object, JSONObject> {
        private String isbnToSearch;

        @Override
        protected JSONObject doInBackground(String... strings) {
            isbnToSearch = strings[0];
            return SearchBookContentNetworkHelper.getBookSearchJsonResult(isbnToSearch);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            SearchBookContentsResult book =
                    SearchBookContentNetworkHelper.getSearchBookContentResultFromJson(result, isbnToSearch);

            if (book != null) {
                if (book.getImageUrl() != null) {
                    new GetBookThumb().execute(book.getImageUrl());
                }

                ((TextView) findViewById(R.id.bookTitle)).setText(book.getTitle());
                ((TextView) findViewById(R.id.bookAuthor)).setText(book.getAuthor());
                ((TextView) findViewById(R.id.bookDescription)).setText(book.getDescription());
                ((TextView) findViewById(R.id.bookPages)).setText(String.valueOf(book.getPageCount()));
                ((TextView) findViewById(R.id.bookRating)).setText(String.valueOf(book.getAverageRating()));
            }
        }
    }

    private final class GetBookThumb extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            return SaveImageHelper.saveImageUrlToBitmapFormat(strings[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            ((ImageView) findViewById(R.id.bookImage)).setImageBitmap(result);
        }
    }
}
