package com.maxml.exexchange.db;

import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.maxml.exexchange.entity.Currency;
import com.maxml.exexchange.util.CalendarFormatter;
import com.maxml.exexchange.util.ExchangeConstants;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Maxml on 14.05.2016.
 */
public class ServerApi {

    private static final String baseUrl = "http://api.fixer.io/latest/";

    private IFunctional api;
    private Handler handler;

    public ServerApi(Handler handler) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(IFunctional.class);

        this.handler = handler;
    }

    public void getPlot(final String startDate, final String endDate, final String base, final String symbols) {
        if (CalendarFormatter.getDifferenceBetween(startDate, endDate) <= 0) {
            getInfo(startDate, base, symbols);
            return;
        }

        Calendar buff = CalendarFormatter.getCalendar(endDate);
        while (buff.getTimeInMillis() > CalendarFormatter.getCalendar(startDate).getTimeInMillis()) {
            getInfo(CalendarFormatter.dateToString(buff), base, symbols);
            delOneDay(buff);
        }
    }

    private Calendar addOneDay(Calendar date) {
        int day = date.get(Calendar.DAY_OF_MONTH);
        date.set(Calendar.DAY_OF_MONTH, day + 1);
        return date;
    }

    private Calendar delOneDay(Calendar date) {
        int day = date.get(Calendar.DAY_OF_MONTH);
        date.set(Calendar.DAY_OF_MONTH, day - 1);
        return date;
    }

    public void getInfo(final String date, final String base, final String symbols) {
        Call<JsonElement> call = api.getInfo(date, base, symbols);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Gson gson = new GsonBuilder().create();

//                String base = element.getAsJsonObject().get("base").getAsString();
                JsonObject buff = response.body().getAsJsonObject().get("rates").getAsJsonObject();
                if (buff != null && !"{}".equals(buff.toString())) {
                    double rate = buff.get(symbols).getAsDouble();
//                String date = element.getAsJsonObject().get("date").getAsString();

                    Currency currency = new Currency(base, date, symbols, rate);
                    sendCurrency(currency);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                sendErorMessage("Failure");
            }
        });
    }

    private void sendMessage(int result) {
        Message msg = handler.obtainMessage();
        msg.what = result;
        handler.sendMessage(msg);
    }

    private void sendCurrency(Currency currency) {
        Message msg = handler.obtainMessage();
        msg.what = ExchangeConstants.SUCCESS_RESULT;
        msg.obj = currency;

        handler.sendMessage(msg);
    }

    private void sendErorMessage(String message) {
        Message msg = handler.obtainMessage();
        msg.what = ExchangeConstants.ERROR_RESULT;
        msg.obj = message;

        handler.sendMessage(msg);
    }

    public interface IFunctional {

        @GET("{date}")
        Call<JsonElement> getInfo(@Path("date") String date, @Query("base") String base, @Query("symbols") String symbols);
    }
}