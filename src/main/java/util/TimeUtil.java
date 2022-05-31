package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author sunlifan
 * @date 2022/5/31
 * parse time to timestamp
 */
public class TimeUtil {
        /**
         * Fri, 12 May 2006 18:53:33 GMT
         */
        public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
        public static long getTimestamp(String time) throws ParseException {
            return simpleDateFormat.parse(time).getTime();
        }

        public static String toTimeString(long timestamp){
            return simpleDateFormat.format(new Date(timestamp));
        }
}
