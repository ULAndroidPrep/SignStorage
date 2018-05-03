package edu.rosehulman.photobucket;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

/**
 * Created by Matt Boutell on 12/18/2015.
 * Rose-Hulman Institute of Technology.
 * Covered by MIT license.
 */
public class Pic implements Parcelable {


    public static final Creator<Pic> CREATOR = new Creator<Pic>() {
        @Override
        public Pic createFromParcel(Parcel in) {
            return new Pic(in);
        }

        @Override
        public Pic[] newArray(int size) {
            return new Pic[size];
        }
    };

    private String key;
    private String caption;
    private String imageUrl;

    // CONSIDER: should I add image to Parcel?
    private Bitmap image;

    protected Pic(Parcel in) {
        key = in.readString();
        caption = in.readString();
        imageUrl = in.readString();
        image = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public Pic(String caption, String imageUrl) {
        this.caption = caption;
        this.imageUrl = imageUrl;
    }

    public Pic() {
        // need empty constructor for Firebase
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(caption);
        dest.writeString(imageUrl);
        dest.writeParcelable(image, flags);
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Exclude
    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setValues(Pic newPic) {
        setCaption(newPic.getCaption());
        setImageUrl(newPic.getImageUrl());
    }
}
