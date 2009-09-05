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
}
