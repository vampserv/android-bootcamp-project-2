package com.edward.googleimagesearch.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.edward.googleimagesearch.R;
import com.edward.googleimagesearch.models.SearchFilter;

public class EditSettingsActivity extends ActionBarActivity {

    Spinner spImageSize;
    Spinner spImageColor;
    Spinner spImageType;
    EditText etSiteFilter;

    SearchFilter searchFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_settings);

        // get SearchFilter model
        searchFilter = getIntent().getParcelableExtra("SearchFilter");

        // initialize views
        spImageSize = (Spinner) findViewById(R.id.spImageSize);
        spImageColor = (Spinner) findViewById(R.id.spImageColor);
        spImageType = (Spinner) findViewById(R.id.spImageType);
        etSiteFilter = (EditText) findViewById(R.id.etSiteFilter);

        ArrayAdapter<CharSequence> aImageSize = ArrayAdapter.createFromResource(this, R.array.image_size_spinner_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> aImageColor = ArrayAdapter.createFromResource(this, R.array.image_color_spinner_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> aImageType = ArrayAdapter.createFromResource(this, R.array.image_type_spinner_array, android.R.layout.simple_spinner_item);

        aImageSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aImageColor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aImageType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spImageSize.setAdapter(aImageSize);
        spImageColor.setAdapter(aImageColor);
        spImageType.setAdapter(aImageType);

        // set defaults
        if(searchFilter.imgsz != null && !searchFilter.imgsz.isEmpty()) {
            int imageSizePos = aImageSize.getPosition(searchFilter.imgsz);
            spImageSize.setSelection(imageSizePos);
        }

        if(searchFilter.imgcolor != null && !searchFilter.imgcolor.isEmpty()) {
            int imageColorPos = aImageColor.getPosition(searchFilter.imgcolor);
            spImageColor.setSelection(imageColorPos);
        }

        if(searchFilter.imgtype != null && !searchFilter.imgtype.isEmpty()) {
            int imageTypePos = aImageType.getPosition(searchFilter.imgtype);
            spImageType.setSelection(imageTypePos);
        }

        if(searchFilter.as_sitesearch != null && !searchFilter.as_sitesearch.isEmpty()) {
            etSiteFilter.setText(searchFilter.as_sitesearch);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_settings, menu);
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

        return super.onOptionsItemSelected(item);
    }

    public void onSaveSettings(View view) {

        searchFilter.imgsz = spImageSize.getSelectedItem().toString();
        searchFilter.imgcolor = spImageColor.getSelectedItem().toString();
        searchFilter.imgtype = spImageType.getSelectedItem().toString();
        searchFilter.as_sitesearch = etSiteFilter.getText().toString();

        Intent data = new Intent();
        data.putExtra("SearchFilter", searchFilter);
        setResult(RESULT_OK, data);
        this.finish();
    }

}
