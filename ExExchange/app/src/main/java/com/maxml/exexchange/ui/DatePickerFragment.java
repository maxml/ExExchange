package com.maxml.exexchange.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.maxml.exexchange.util.CalendarFormatter;

import java.util.Calendar;

/**
 * Created by Maxml on 14.05.2016.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private int viewType;
    private OnDateChangedEventListener listener;

    public DatePickerFragment() {
    }

    public void setListener(int view, OnDateChangedEventListener listener) {
        this.viewType = view;
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
//        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);

        Calendar calendar = CalendarFormatter.getCalendarFromDatePicker(view);
//        Calendar finish = CalendarFormatter.getCalendarFromDatePicker(finishPicker);

        //        final Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);


//        Toast.makeText(getApplicationContext(), start.toString(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(), finish.toString(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(), CalendarFormatter.getDifferenceBetween(start, finish).toString(), Toast.LENGTH_SHORT).show();


        listener.onDateChanged(viewType, calendar);
    }

    public interface OnDateChangedEventListener {
        void onDateChanged(int view, Calendar calendar);
    }
}
