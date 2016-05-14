package com.maxml.exexchange.entity;

import com.jjoe64.graphview.series.DataPoint;
import com.maxml.exexchange.util.CalendarFormatter;

/**
 * Created by Maxml on 14.05.2016.
 */
public class Currency {
    private String base;
    private String date;
    private String to;
    private double rate;

    public Currency(String base, String date, String to, double rate) {
        this.base = base;
        this.date = date;
        this.to = to;
        this.rate = rate;
    }

    public DataPoint getDataPoint() {
        return new DataPoint(CalendarFormatter.getCalendar(date).getTime(), rate);
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "base='" + base + '\'' +
                ", date='" + date + '\'' +
                ", to='" + to + '\'' +
                ", rate=" + rate +
                '}';
    }
}
