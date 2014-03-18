package net.dean.cyanideviewer.app.api;

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import net.dean.cyanideviewer.app.CyanideViewer;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Represents an Cyanide and Happiness comic.
 */
public class Comic implements Parcelable {
    /** The ID of the comic. */
    private long id;

    /** The URL of the comic's image */
    private URL url;

    /**
     * Instantiates a new Comic
     * @param id The ID of the comic
     * @param url The URL of the comic's image
     */
    public Comic(long id, URL url) {
        this.url = url;
        this.id = id;
    }

    /**
     * Instanties a new Comic with a Parcel.
     * @param in A Parcel created by {@link #CREATOR}
     */
    public Comic(Parcel in) {
        in.readBundle();
        this.id = in.readLong();
        String urlString = in.readString();
        try {
            this.url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(CyanideViewer.TAG, "Malformed URL while creating Parcelable Comic: " + urlString, e);
        }

    }

    /** Gets the URL */
    public URL getUrl() {
        return url;
    }

    /**
     * Sets the URL
     * @param url The new URL
     */
    public void setUrl(URL url) {
        this.url = url;
    }

    /** Gets the URL */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID
     * @param id The new ID
     */
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        // I don't know what I'm doing here
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(url.toExternalForm());
    }

    /** The Creator used to create Comics using Parcels */
    public static final Creator CREATOR = new Creator() {

        @Override
        public Object createFromParcel(Parcel source) {
            return new Comic(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new Comic[size];
        }
    };

    @Override
    public String toString() {
        return "Comic{" +
                "id=" + id +
                ", url='" + url.toExternalForm() + '\'' +
                '}';
    }

    /**
     * Tries to download this comic to the local file system. Will download to
     * "/sdard/Cyanide Viewer/$id.$extension
     * @return Whether or not the download succeeded.
     */
    public boolean download() {
        try {
            return new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    try {
                        InputStream input = getUrl().openStream();

                        // "/sdcard/CyanideViewer"
                        CyanideApi.IMAGE_DIR.mkdirs();

                        String urlString = getUrl().toExternalForm();
                        String ext = urlString.substring(urlString.lastIndexOf('.'));
                        File dest = new File(CyanideApi.IMAGE_DIR, getId() + ext);
                        if (!dest.exists()) {
                            // Only download it if the file doesn't already exist
                            FileUtils.copyInputStreamToFile(input, dest);
                        }

                        Log.i(CyanideViewer.TAG, "Downloaded comic #" + id + " to " + dest.getAbsolutePath());
                        return true;
                    } catch (IOException e) {
                        Log.e(CyanideViewer.TAG, "Failed to download #" + id, e);
                        return false;
                    }
                }
            }.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }
}
