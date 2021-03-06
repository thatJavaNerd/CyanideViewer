package net.dean.cyanideviewer;

/**
 * A collection of constant values that pertains to all parts of the application.
 */
public final class Constants {
	public static final String KEY_DOWNLOAD_LOCATION = "pref_download_location";

	private Constants() {
		// Prevent instances
	}

	/** The tag used by all Log.x methods in this app */
	public static final String TAG = "CyanideViewer";
	public static final String TAG_API = TAG + "|API";
	public static final String TAG_DB = TAG + "|DB";
	/** Returned when the user picked a comic */
	public static final int RESULT_OK = 0;
	/** Returned when the user did not pick a comic */
	public static final int RESULT_NONE = 1;
	/** The ID of the notification created by this app.*/
	public static final int NOTIF_DELETE_COMIC = 0;
	/** The ID of the notification */
	public static final int NOTIF_DOWNLOAD_COMIC = 1;
}
