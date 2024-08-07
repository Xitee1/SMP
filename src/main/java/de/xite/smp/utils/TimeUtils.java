package de.xite.smp.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
	public static String convertPlayTimeFromSecondsToString(long playtime) {
		long hours = playtime / 3600;
		long minutes = (playtime % 3600) / 60;
		long seconds = playtime % 60;
		
		String s = "";
		
		if(hours == 1) {
			s += hours + " Stunde, ";
		}else
			s += hours + " Stunden, ";
		if(minutes == 1) {
			s += minutes + " Minute, ";
		}else
			s += minutes + " Minuten, ";
		if(seconds == 1) {
			s += seconds + " Sekunde";
		}else
			s += seconds + " Sekunden";
		
		return s;
	}
	
	
	// Credits: CoreProtect - https://github.com/PlayPro/CoreProtect/blob/master/src/main/java/net/coreprotect/utility/Util.java#L233C1-L268C6
	public static String getTimeSince(long resultTime, long currentTime, boolean component) {
		StringBuilder message = new StringBuilder();
		double timeSince = currentTime - (resultTime + 0.00);
		if (timeSince < 0.00) {
			timeSince = 0.00;
		}

		// minutes
		timeSince = timeSince / 60;
		if (timeSince < 60.0) {
			message.append(new DecimalFormat("0.00").format(timeSince) + "/m");
		}

		// hours
		if (message.length() == 0) {
			timeSince = timeSince / 60;
			if (timeSince < 24.0) {
				message.append(new DecimalFormat("0.00").format(timeSince) + "/h");
			}
		}

		// days
		if (message.length() == 0) {
			timeSince = timeSince / 24;
			message.append(new DecimalFormat("0.00").format(timeSince) + "/d");
		}

		if (component) {
			Date logDate = new Date(resultTime * 1000L);
			String formattedTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").format(logDate);

			return formattedTimestamp + "|" + message;
		}

		return message.toString();
	}
}
