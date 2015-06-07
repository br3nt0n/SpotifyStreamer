package com.bc.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by brenton on 7/06/15.
 */
public class SearchArtist implements Parcelable {
    private String name;
    private String id;
    private String imageUrl;

    public SearchArtist(String name, String id){
        this.name = name;
        this.id = id;
    }

    public SearchArtist(String name, String id, String imageUrl){
        this.name = name;
        this.id = id;
        this.imageUrl = imageUrl;
    }

    public String getName(){
        return this.name;
    }

    public String getId(){
        return this.id;
    }

    public String getImageUrl(){
        return this.imageUrl;
    }

    private SearchArtist(Parcel in) {
        name = in.readString();
        id = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<SearchArtist> CREATOR = new Creator<SearchArtist>() {
        @Override
        public SearchArtist createFromParcel(Parcel in) {
            return new SearchArtist(in);
        }

        @Override
        public SearchArtist[] newArray(int size) {
            return new SearchArtist[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(imageUrl);
    }
}
