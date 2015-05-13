package com.edward.googleimagesearch.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by edwardyang on 5/13/15.
 */
public class SearchFilter implements Parcelable {

    public String imgsz;
    public String imgcolor;
    public String imgtype;
    public String as_sitesearch;

    public SearchFilter() {
        
    }

    private SearchFilter(Parcel in) {
        this.imgsz = in.readString();
        this.imgcolor = in.readString();
        this.imgtype = in.readString();
        this.as_sitesearch = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imgsz);
        dest.writeString(imgcolor);
        dest.writeString(imgtype);
        dest.writeString(as_sitesearch);
    }

    public static final Parcelable.Creator<SearchFilter> CREATOR = new Parcelable.Creator<SearchFilter>() {
        @Override
        public SearchFilter createFromParcel(Parcel in) {
            return new SearchFilter(in);
        }

        @Override
        public SearchFilter[] newArray(int size) {
            return new SearchFilter[size];
        }
    };

}
