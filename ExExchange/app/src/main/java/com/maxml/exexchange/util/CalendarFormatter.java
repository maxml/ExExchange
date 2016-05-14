package com.maxml.exexchange.util;

import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Maxml on 14.05.2016.
 */
public class CalendarFormatter {

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    public static Calendar getCalendarFromDatePicker(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar;
    }

    public static long getDifferenceBetween(String start, String finish) {
        if (start == null || finish == null) {
            return -1;
        }

        Calendar startCalendar = getCalendar(start);
        Calendar finishCalendar = getCalendar(finish);

        return finishCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();
    }

    public static GregorianCalendar getCalendar(String date) {
        try {
            Date parsedDateTime = DATE_FORMATTER.parse(date);
            GregorianCalendar parsedCal = new GregorianCalendar();
            parsedCal.setTime(parsedDateTime);
            return parsedCal;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String dateToString(Calendar calendar) {
        return DATE_FORMATTER.format(calendar.getTime());
    }

}
