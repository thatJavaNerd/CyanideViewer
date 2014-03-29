package net.dean.cyanideviewer.api;

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import net.dean.cyanideviewer.CyanideUtils;
import net.dean.cyanideviewer.CyanideViewer;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Represents an Cyanide and Happiness comic.
 */
public class Comic implements Parcelable {
	/** The ID of the comic. */
	private long id;

	/** The URL of the comic's image */
	private URL url;

	/** Whether this comic is a favorite of the user's */
	private boolean isFavorite;

	/**
	 * Instantiates a new Comic assuming the comic is not a favorite
	 * @param id The ID of the comic
	 * @param url The URL of the comic's image
	 */
	public Comic(long id, URL url) {
		this(id, url, false);
	}

	/**
	 * Instantiates a new Comic
	 * @param id The ID of the comic
	 * @param url The URL of the comic's image
	 * @param isFavorite If this comic is one of the user's favorites
	 */
	public Comic(long id, URL url, boolean isFavorite) {
		this.url = url;
		this.id = id;
		this.isFavorite = isFavorite;
	}

	/**
	 * Instanties a new Comic with a Parcel.
	 * @param in A Parcel created by {@link #CREATOR}
	 */
	public Comic(Parcel in) {
		this.id = in.readLong();
		this.url = CyanideUtils.newUrl(in.readString());
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

	/** Gets whether or not this comic is a favorite */
	public boolean isFavorite() {
		return isFavorite;
	}

	/**
	 * Sets whether or not this comic is a favorite of the user's
	 * @param isFavorite If the comic is a favorite
	 */
	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
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
	 * @param downloadButton
	 */
	public void download(final ImageButton downloadButton) {
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected void onPreExecute() {
				Toast.makeText(CyanideViewer.getContext(), "Download starting", Toast.LENGTH_SHORT).show();
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					InputStream input = getUrl().openStream();

					// "/sdcard/CyanideViewer"
					if (!(CyanideApi.IMAGE_DIR.mkdirs() || CyanideApi.IMAGE_DIR.isDirectory())) {
						// The image dir is not a directory or there was an error creating the folder
						Log.e(CyanideViewer.TAG, "Error while creating " + CyanideApi.IMAGE_DIR.getAbsolutePath()
								+ ". Is it not a directory?");
					}
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

			@Override
			protected void onPostExecute(Boolean success) {
				String toastText;
				if (success) {
					toastText = "Comic downloaded";
				} else {
					toastText = "Comic failed to download!";
				}

				Toast.makeText(CyanideViewer.getContext(), toastText, Toast.LENGTH_SHORT).show();
				downloadButton.setEnabled(false);
			}
		}.execute();
	}
}