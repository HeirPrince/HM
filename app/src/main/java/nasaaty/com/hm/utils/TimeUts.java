package nasaaty.com.hm.utils;

import android.content.Context;

import com.github.thunder413.datetimeutils.DateTimeUnits;
import com.github.thunder413.datetimeutils.DateTimeUtils;

import java.util.Date;

public class TimeUts {

	public static String getTimeStamp() {
		return String.valueOf(System.currentTimeMillis() / 1000);
	}

	public static String getTmAgo(Context context, String timeStamp){
		Date date = DateTimeUtils.formatDate(Long.parseLong(timeStamp), DateTimeUnits.SECONDS);
		return DateTimeUtils.getTimeAgo(context, date);
	}
}
