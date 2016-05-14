package com.maxml.exexchange;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.maxml.exexchange.db.ServerApi;
import com.maxml.exexchange.entity.Currency;
import com.maxml.exexchange.ui.DatePickerFragment;
import com.maxml.exexchange.util.CalendarFormatter;
import com.maxml.exexchange.util.ExchangeConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        View.OnClickListener, DatePickerFragment.OnDateChangedEventListener {

    private Spinner fromCurrencySpinner;
    private Spinner toCurrencySpinner;

    private TextView startTimeView;
    private TextView finishTimeView;

    private GraphView graphView;
    private Set<Currency> currencySet = new HashSet<>();
    private LineGraphSeries<DataPoint> series;

    private ServerApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSpinners();
        initDatePickers();
        initPlot();

        api = new ServerApi(mainHandler);

        Collections.synchronizedSet(currencySet);
    }

    private void initDatePickers() {
        startTimeView = (TextView) findViewById(R.id.main_start_text);
        startTimeView.setText(CalendarFormatter.dateToString(Calendar.getInstance()));
        finishTimeView = (TextView) findViewById(R.id.main_finish_text);
        finishTimeView.setText(CalendarFormatter.dateToString(Calendar.getInstance()));

        findViewById(R.id.main_start_picker_button).setOnClickListener(this);
        findViewById(R.id.main_finish_picker_button).setOnClickListener(this);
    }

    private void initSpinners() {
        fromCurrencySpinner = (Spinner) findViewById(R.id.main_from_currency);
        toCurrencySpinner = (Spinner) findViewById(R.id.main_to_currency);

        ArrayAdapter<?> adapter =
                ArrayAdapter.createFromResource(this, R.array.exchanges, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fromCurrencySpinner.setAdapter(adapter);
        toCurrencySpinner.setAdapter(adapter);

        fromCurrencySpinner.setOnItemSelectedListener(this);
        toCurrencySpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String base = fromCurrencySpinner.getSelectedItem().toString();
        String symbols = toCurrencySpinner.getSelectedItem().toString();

        api.getInfo(startTimeView.getText().toString(), base, symbols);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();

        switch (v.getId()) {
            case R.id.main_start_picker_button:
                newFragment.setListener(ExchangeConstants.START_PICKER, this);
                newFragment.show(getSupportFragmentManager(), "start");
                break;
            case R.id.main_finish_picker_button:
                newFragment.setListener(ExchangeConstants.FINISH_PICKER, this);
                newFragment.show(getSupportFragmentManager(), "finish");
                break;
        }
    }

    @Override
    public void onDateChanged(int view, Calendar calendar) {
        switch (view) {
            case ExchangeConstants.START_PICKER:
                startTimeView.setText(CalendarFormatter.dateToString(calendar));
                break;
            case ExchangeConstants.FINISH_PICKER:
                finishTimeView.setText(CalendarFormatter.dateToString(calendar));
                break;
        }

//        String diff = "" + CalendarFormatter.getDifferenceBetween(startTimeView.getText().toString(),
//                finishTimeView.getText().toString());
//        Toast.makeText(this, diff, Toast.LENGTH_SHORT).show();
        String base = fromCurrencySpinner.getSelectedItem().toString();
        String symbols = toCurrencySpinner.getSelectedItem().toString();
        String startDate = startTimeView.getText().toString();
        String finfshDate = finishTimeView.getText().toString();

        currencySet.clear();
        api.getPlot(startDate, finfshDate, base, symbols);
    }

    private void initPlot() {
        graphView = (GraphView) findViewById(R.id.main_graph);

        series = new LineGraphSeries<DataPoint>(new DataPoint[]{});
        graphView.addSeries(series);

// set date label formatter
        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graphView.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

// set manual x bounds to have nice steps
//        graphView.getViewport().setMinX(CalendarFormatter.getCalendar(startTimeView.getText().toString()).getTime());
//        graphView.getViewport().setMaxX(d3.getTime());
//        graphView.getViewport().setXAxisBoundsManual(true);

        graphView.getGridLabelRenderer().setHorizontalLabelsVisible(true);
        graphView.getGridLabelRenderer().setVerticalLabelsVisible(true);

    }

    private void updatePlot() {
//        ArrayList<Currency> buff = new ArrayList();
//        buff.addAll(currencySet);

//        Collections.sort(buff, new Comparator<Currency>() {
//            @Override
//            public int compare(Currency o1, Currency o2) {
//
//                long anotherMs = CalendarFormatter.getCalendar(o1.getDate()).getTimeInMillis();
//                long thisMs = CalendarFormatter.getCalendar(o2.getDate()).getTimeInMillis();
//                return (int) (anotherMs - thisMs);
//            }
//        });

        graphView.removeAllSeries();
        series = new LineGraphSeries<DataPoint>(getPoints());
        graphView.addSeries(series);
    }

    private void reverse() {
        ArrayList<Currency> buff = new ArrayList<>();
        buff.addAll(currencySet);
        currencySet.clear();
        Collections.reverse(buff);
        currencySet.addAll(buff);
    }

    private DataPoint[] getPoints() {
        reverse();
        DataPoint[] points = new DataPoint[currencySet.size()];
        for (int i = 0; i < currencySet.size(); i++) {
            points[i] = ((Currency) currencySet.toArray()[i]).getDataPoint();
        }
        return points;
    }

    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case ExchangeConstants.SUCCESS_RESULT:
                    Currency currency = ((Currency) msg.obj);

                    currencySet.add(currency);
                    Toast.makeText(getApplicationContext(), currency.toString(),
                            Toast.LENGTH_SHORT).show();
                    updatePlot();
//                    series.appendData(currency.getDataPoint(), true, currencySet.size());

                    break;
                case ExchangeConstants.ERROR_RESULT:
                    break;
            }
        }
    };
}
