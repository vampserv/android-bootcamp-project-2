package com.edward.googleimagesearch.models;

import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by edwardyang on 5/13/15.
 */
public class ImageResult implements Parcelable {

    public String fullUrl;
    public String thumbUrl;
    public String title;
    public String width;
    public String height;

    public ImageResult(JSONObject json) {
        try {
            this.fullUrl = json.getString("url");
            this.thumbUrl = json.getString("tbUrl");
            this.title = json.getString("title");
            this.width = json.getString("width");
            this.height = json.getString("height");
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    private ImageResult(Parcel in) {
        this.fullUrl = in.readString();
        this.title = in.readString();
    }

    public static ArrayList<ImageResult> fromJSONArray(JSONArray array) {
        ArrayList<ImageResult> results = new ArrayList<ImageResult>();
        for (int i = 0; i < array.length(); i++ ) {
            try {
                results.add(new ImageResult(array.getJSONObject(i)));
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fullUrl);
        dest.writeString(title);
    }

    public static final Parcelable.Creator<ImageResult> CREATOR = new Parcelable.Creator<ImageResult>() {
        @Override
        public ImageResult createFromParcel(Parcel in) {
            return new ImageResult(in);
        }

        @Override
        public ImageResult[] newArray(int size) {
            return new ImageResult[size];
        }
    };

}
