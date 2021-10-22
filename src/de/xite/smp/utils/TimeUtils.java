package de.xite.smp.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
	// Credits: CoreProtect - https://github.com/PlayPro/CoreProtect/blob/a61df070e66e803fe7eb74ea4c21d5a08cf30667/src/main/java/net/coreprotect/utility/Util.java#L199
	public static String getTimeSince(int logTime, int currentTime, boolean component) {
		StringBuilder message = new StringBuilder();
		double timeSince = currentTime - (logTime + 0.00);
		
		// minutes
		timeSince = timeSince / 60;
		if(timeSince < 60.0) {
			message.append(new DecimalFormat("0.00").format(timeSince) + "/m");
		}
		
		// hours
		if(message.length() == 0) {
			timeSince = timeSince / 60;
            if (timeSince < 24.0) {
            	message.append(new DecimalFormat("0.00").format(timeSince) + "/h");
            }
		}

		// days
		if(message.length() == 0) {
			timeSince = timeSince / 24;
			message.append(new DecimalFormat("0.00").format(timeSince) + "/d");
		}
		
		if(component) {
			Date logDate = new Date(logTime * 1000L);
			String formattedTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").format(logDate);
			return formattedTimestamp + "|" + message.toString();
		}
		
		return message.toString();
	}
}
