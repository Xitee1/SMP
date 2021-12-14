package de.xite.smp.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
	public static String convertPlayTimeFromSecondsToString(int playtime) {
		int hours = playtime / 3600;
		int minutes = (playtime % 3600) / 60;
		int seconds = playtime % 60;
		
		String s = "";
		if(seconds == 0 || seconds == 1) {
			s += seconds + " Sekunde, ";
		}else
			s += seconds + " Sekunden, ";
		
		if(minutes == 0 || minutes == 1) {
			s += minutes + " Minute, ";
		}else
			s += minutes + " Minuten, ";
		
		if(hours == 0 || hours == 1) {
			s += hours + " Stunde";
		}else
			s += hours + " Stunden";
		
		return s;
	}
	
	
	// Credits: CoreProtect - https://github.com/PlayPro/CoreProtect/blob/master/src/main/java/net/coreprotect/utility/Util.java#L199
	public static String getTimeSince(long logTime, long currentTime, boolean component) {
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
