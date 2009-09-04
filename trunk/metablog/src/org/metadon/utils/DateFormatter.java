/*
 * DateFormatter.java
 *
 * Created on 17. November 2007, 14:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.utils;

import java.util.Date;
import java.util.Calendar;
import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes
 */
public class DateFormatter {
    
    public static String[] getUserFriendlyDate(long ts) {
        // format and return current date and time
        Date date = new Date(ts);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        String year = null;
        String month = null;
        String day = null;

        String hour = null;
        String minute = null;
        String second = null;

        int iyear = calendar.get(Calendar.YEAR);
        int imonth = calendar.get(Calendar.MONTH)+1;
        int iday = calendar.get(Calendar.DAY_OF_MONTH);
        month = Integer.toString(imonth);
        day = Integer.toString(iday);
        if(month.length() == 1) {month = "0"+month;}
        if(day.length() == 1) {day = "0"+day;}
        String dateString = new String(iyear + "/" + month + "/" + day);

        int ihour = calendar.get(Calendar.HOUR_OF_DAY);
        int iminute = calendar.get(Calendar.MINUTE);
        int isecond = calendar.get(Calendar.SECOND);
        hour = Integer.toString(ihour);
        minute = Integer.toString(iminute);
        second = Integer.toString(isecond);
        if(hour.length() == 1) {hour = "0"+hour;}
        if(minute.length() == 1) {minute = "0"+minute;}
        if(second.length() == 1) {second = "0"+second;}
        String timeString = new String(hour + ":" + minute + ":" + second);

        return new String[] {dateString, timeString};
    }
    
    // return user friendly time interval string (days, hours, minutes, seconds)
    public static String getUserFriendlyInterval(long i) {
        long interval = i;
        
        if(interval != 0) {
            long remainMs;
            String result = "";
            
            // time categories in milliseconds
            int secMs   = 1000;
            int minMs   = secMs*60;
            int hourMs  = minMs*60;
            int dayMs   = hourMs*24;

            int days = (int)interval/dayMs;
            remainMs = interval%dayMs;
            //#debug
//#             System.out.println("days: "+days);

            int hours = (int)remainMs/hourMs;
            remainMs = remainMs%hourMs;
            //#debug
//#             System.out.println("hours: "+hours);

            int min = (int)remainMs/minMs;
            remainMs = remainMs%minMs;
            //#debug
//#             System.out.println("minutes: "+min);

            int sec = (int)remainMs/secMs;
            //#debug
//#             System.out.println("seconds: "+sec);

            if(days > 0) {
                result = days+Locale.get("text.days")+" ";
            }
            if(hours > 0 || days > 0) {
                result += hours+Locale.get("text.hours")+" ";
            }
            if(min > 0 || hours > 0) {
                result += min+Locale.get("text.min")+" ";
            }
            if(sec > 0 || min > 0) {
                result += sec+Locale.get("text.sec");
            }
            return result;
        }
        return "0 "+Locale.get("text.sec");
    }
    
}
