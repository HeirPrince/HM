package nasaaty.com.hm.model;

import android.arch.persistence.room.TypeConverter;
import android.net.Uri;

public class UriConverter {
	@TypeConverter
	public static Uri toUri(String string_uri){
		return string_uri == null ? null : Uri.parse(string_uri);
	}

	@TypeConverter
	public static String toString(Uri uri){
		return uri == null ? null : uri.toString();
	}
}
