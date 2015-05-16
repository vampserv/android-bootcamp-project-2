package com.edward.googleimagesearch.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.edward.googleimagesearch.R;
import com.edward.googleimagesearch.adapters.EndlessScrollListener;
import com.edward.googleimagesearch.adapters.ImageResultsAdapter;
import com.edward.googleimagesearch.fragments.EditSettingsDialog;
import com.edward.googleimagesearch.models.ImageResult;
import com.edward.googleimagesearch.models.SearchFilter;
import com.etsy.android.grid.StaggeredGridView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class GoogleImageSearchActivity extends ActionBarActivity {

    private EditText etSearchText;
    private StaggeredGridView gvImages;
    private ArrayList<ImageResult> imageResults;
    private ImageResultsAdapter aImageResults;
    MenuItem miActionProgressItem;

    public SearchFilter searchFilter;
    public String searchQuery;

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

        // endless scroll for gridview
        gvImages.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                searchImages(page);
            }
        });

        searchFilter = new SearchFilter();
    }

    private void setupViews() {
//        etSearchText = (EditText) findViewById(R.id.etSearchText);
        gvImages = (StaggeredGridView) findViewById(R.id.gvImages);
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

    public void searchImages(int page) {

        if(!isNetworkAvailable()) {
            Toast.makeText(this, "No internet connection, please try again later", Toast.LENGTH_SHORT).show();
            return;
        }

        miActionProgressItem.setVisible(true);

//        String query = etSearchText.getText().toString();
        String url = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + searchQuery + "&rsz=" + resultsPerPage + "&start=" + (page * resultsPerPage);

        // add optional query params
        if(searchFilter.imgsz != null && searchFilter.imgsz != "any") {
            url += "&imgsz=" + searchFilter.imgsz;
        }

        if(searchFilter.imgcolor != null && searchFilter.imgcolor != "any") {
            url += "&imgcolor=" + searchFilter.imgcolor;
        }

        if(searchFilter.imgtype != null && searchFilter.imgtype != "any") {
            url += "&imgtype=" + searchFilter.imgtype;
        }

        if(searchFilter.as_sitesearch != null && !searchFilter.as_sitesearch.isEmpty()) {
            url += "&as_sitesearch=" + searchFilter.as_sitesearch;
        }

        AsyncHttpClient client = new AsyncHttpClient();

        // get JSON response
        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray imageResultsJSON = null;
                try {
                    imageResultsJSON = response.getJSONObject("responseData").getJSONArray("results");
                    aImageResults.addAll(ImageResult.fromJSONArray(imageResultsJSON));
                } catch (JSONException e) {
                    Toast.makeText(GoogleImageSearchActivity.this, "Error parsing results, please try again later", Toast.LENGTH_SHORT).show();
                }
                miActionProgressItem.setVisible(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(GoogleImageSearchActivity.this, "Error retrieving results, please try again later", Toast.LENGTH_SHORT).show();
                miActionProgressItem.setVisible(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_google_image_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // search images
                searchQuery = query;
                imageResults.clear();
                searchImages(0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);

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
//            launchEditSettingsActivity();
            launchEditSettingsDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    // check network connectivity
    private Boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        return super.onPrepareOptionsMenu(menu);
    }

    // launch edit settings intent to get an intent
    private void launchEditSettingsActivity() {
        Intent i = new Intent(GoogleImageSearchActivity.this, EditSettingsActivity.class);
        i.putExtra("SearchFilter", searchFilter);
        startActivityForResult(i, EDIT_SETTINGS_REQUEST_CODE);
    }

    // launch edit settings in a dialog
    private void launchEditSettingsDialog() {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        EditSettingsDialog esd = EditSettingsDialog.newInstance(this, new EditSettingsDialogFragmentListener(){
            public void updateSettings(SearchFilter filter){
                searchFilter = filter;
            }
        }, searchFilter);
        esd.show(fm, "fragment_edit_settings");
    }

    public interface EditSettingsDialogFragmentListener {
        public void updateSettings(SearchFilter filter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == EDIT_SETTINGS_REQUEST_CODE) {
            // get the modified settings from intent as a parcelable
            searchFilter = data.getParcelableExtra("SearchFilter");
            Toast.makeText(this, "Your settings have been saved!", Toast.LENGTH_SHORT).show();
        }
    }
}
