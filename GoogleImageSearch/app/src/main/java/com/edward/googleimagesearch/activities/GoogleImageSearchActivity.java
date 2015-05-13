package com.edward.googleimagesearch.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.edward.googleimagesearch.R;
import com.edward.googleimagesearch.adapters.EndlessScrollListener;
import com.edward.googleimagesearch.adapters.ImageResultsAdapter;
import com.edward.googleimagesearch.models.ImageResult;
import com.edward.googleimagesearch.models.SearchFilter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class GoogleImageSearchActivity extends ActionBarActivity {

    private EditText etSearchText;
    private GridView gvImages;
    private ArrayList<ImageResult> imageResults;
    private ImageResultsAdapter aImageResults;

    public SearchFilter searchFilter;

    private final int EDIT_SETTINGS_REQUEST_CODE = 100;
    private int resultsPerPage = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_image_search);
        setupViews();
        imageResults = new ArrayList<ImageResult>();
        aImageResults = new ImageResultsAdapter(this, imageResults);
        gvImages.setAdapter(aImageResults);

        gvImages.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                searchImages(page);
            }
        });

        searchFilter = new SearchFilter();
    }

    private void setupViews() {
        etSearchText = (EditText) findViewById(R.id.etSearchText);
        gvImages = (GridView) findViewById(R.id.gvImages);
        gvImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // create an intent
                Intent i = new Intent(GoogleImageSearchActivity.this, ImageDisplayActivity.class);
                // get image result
                ImageResult result = imageResults.get(position);
                // pass image result into intent
                i.putExtra("ImageResult", (ImageResult) aImageResults.getItem(position));
                // launch new activity
                startActivity(i);
            }
        });
    }

    public void onImageSearch(View v) {
        imageResults.clear();
        searchImages(0);
    }

    public void searchImages(int page) {
        String query = etSearchText.getText().toString();

        AsyncHttpClient client = new AsyncHttpClient();

        String url = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + query + "&rsz=" + resultsPerPage + "&start=" + (page * resultsPerPage);

        if(searchFilter.imgsz != null && !searchFilter.imgsz.isEmpty()) {
            url += "&imgsz=" + searchFilter.imgsz;
        }

        if(searchFilter.imgcolor != null && !searchFilter.imgcolor.isEmpty()) {
            url += "&imgcolor=" + searchFilter.imgcolor;
        }

        if(searchFilter.imgtype != null && !searchFilter.imgtype.isEmpty()) {
            url += "&imgtype=" + searchFilter.imgtype;
        }

        if(searchFilter.as_sitesearch != null && !searchFilter.as_sitesearch.isEmpty()) {
            url += "&as_sitesearch=" + searchFilter.as_sitesearch;
        }

        Toast.makeText(this, url, Toast.LENGTH_LONG).show();

        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray imageResultsJSON = null;
                try {
                    imageResultsJSON = response.getJSONObject("responseData").getJSONArray("results");
                    aImageResults.addAll(ImageResult.fromJSONArray(imageResultsJSON));
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("INFO", "Failed");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_google_image_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.miSettings) {
            launchEditSettingsActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void launchEditSettingsActivity() {
        Intent i = new Intent(GoogleImageSearchActivity.this, EditSettingsActivity.class);
        i.putExtra("SearchFilter", searchFilter);
        startActivityForResult(i, EDIT_SETTINGS_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == EDIT_SETTINGS_REQUEST_CODE) {
            searchFilter = data.getParcelableExtra("SearchFilter");
            Toast.makeText(this, "Your settings have been saved!", Toast.LENGTH_SHORT).show();
        }
    }
}
