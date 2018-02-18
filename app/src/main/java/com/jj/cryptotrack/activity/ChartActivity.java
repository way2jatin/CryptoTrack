package com.jj.cryptotrack.activity;

import static com.jj.cryptotrack.utils.AppConstant.COIN_MARKETCAP_CHART_URL_ALL_DATA;
import static com.jj.cryptotrack.utils.AppConstant.COIN_MARKETCAP_CHART_URL_WINDOW;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.jj.cryptotrack.R;
import com.jj.cryptotrack.model.CMCChartData;
import com.jj.cryptotrack.model.CMCCoin;
import com.jj.cryptotrack.utils.APIHelper;
import com.jj.cryptotrack.utils.APIResponseListener;
import com.jj.cryptotrack.utils.Log;
import com.jj.cryptotrack.utils.formatters.MonthSlashDayDateFormatter;
import com.jj.cryptotrack.utils.formatters.TimeDateFormatter;
import com.jj.cryptotrack.utils.formatters.YAxisPriceFormatter;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChartActivity extends BaseAnimationActivity implements OnChartValueSelectedListener {
    private Toolbar mToolbar;

    private int percentageColor;
    private LineChart lineChart;
    private String cryptoID;
    private int chartFillColor;
    private int chartBorderColor;

    public static String CURRENT_CHART_URL;
    private String currentTimeWindow = "";
    private IAxisValueFormatter XAxisFormatter;
    public final IAxisValueFormatter monthSlashDayXAxisFormatter = new MonthSlashDayDateFormatter();
    public final TimeDateFormatter dayCommaTimeDateFormatter = new TimeDateFormatter();
    public final MonthSlashDayDateFormatter monthSlashYearFormatter = new MonthSlashDayDateFormatter();
    DecimalFormat rawNumberFormat = new DecimalFormat("#,###");
    private SingleSelectToggleGroup buttonGroup;

    public static final String ARG_SYMBOL = "symbol";
    public static final String ARG_ID = "ID";
    public static final String COIN_OBJECT = "COIN_OBJECT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_details_tabs);
        mToolbar = findViewById(R.id.toolbar_currency_details);
        setSupportActionBar(mToolbar);
        String symbol = getIntent().getStringExtra(ChartActivity.ARG_SYMBOL);
        getSupportActionBar().setTitle(symbol);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cryptoID = getIntent().getStringExtra(ARG_ID);

        lineChart = findViewById(R.id.chart);
        lineChart.setNoDataText(getString(R.string.noChartDataString));
        lineChart.setNoDataTextColor(R.color.darkRed);
        lineChart.setOnChartValueSelectedListener(this);
        setDayChecked(Calendar.getInstance());
        buttonGroup = findViewById(R.id.chart_interval_button_grp);
        buttonGroup.check(R.id.dayButton);
        currentTimeWindow = getString(R.string.oneDay);
        buttonGroup.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {
                Calendar.getInstance();
                switch (checkedId) {
                    case R.id.dayButton:
                        setDayChecked(Calendar.getInstance());
                        onRefresh();
                        break;
                    case R.id.weekButton:
                        setWeekChecked(Calendar.getInstance());
                        onRefresh();
                        break;
                    case R.id.monthButton:
                        setMonthChecked(Calendar.getInstance());
                        onRefresh();
                        break;
                    case R.id.threeMonthButton:
                        setThreeMonthChecked(Calendar.getInstance());
                        onRefresh();
                        break;
                    case R.id.yearButton:
                        setYearChecked(Calendar.getInstance());
                        onRefresh();
                        break;
                    case R.id.allTimeButton:
                        setAllTimeChecked();
                        onRefresh();
                        break;
                }
            }
        });
        CMCCoin coinObject = getIntent().getParcelableExtra(COIN_OBJECT);
        setTable(coinObject);
        getCMCChart();

    }

    @Override
    public void onValueSelected(final Entry e, final Highlight h) {
        TextView currentPrice = findViewById(R.id.current_price);
        TextView dateTextView = findViewById(R.id.graphFragmentDateTextView);
        currentPrice.setText(String.format(getString(R.string.price_format_no_word), e.getY()));
        dateTextView.setText(getFormattedFullDate(e.getX()));
    }

    @Override
    public void onNothingSelected() {

    }

    public String getFormattedFullDate(float unixSeconds) {
        Date date = new Date((long)unixSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        return sdf.format(date);
    }


    public void setColors(float percentChange) {
        if (percentChange >= 0) {
            chartFillColor = ResourcesCompat.getColor(getResources(), R.color.materialLightGreen, null);
            chartBorderColor = ResourcesCompat.getColor(getResources(), R.color.darkGreen, null);
            percentageColor = ResourcesCompat
                    .getColor(getResources(), R.color.percentPositiveGreen, null);
        }
        else {
            chartFillColor = ResourcesCompat.getColor(getResources(), R.color.materialLightRed, null);
            chartBorderColor = ResourcesCompat.getColor(getResources(), R.color.darkRed, null);
            percentageColor = ResourcesCompat.getColor(getResources(), R.color.percentNegativeRed, null);
        }
    }

    public LineDataSet setUpLineDataSet(List<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "Price");
        dataSet.setColor(chartBorderColor);
        dataSet.setFillColor(chartFillColor);
        dataSet.setDrawHighlightIndicators(true);
        dataSet.setDrawFilled(true);
        dataSet.setDrawCircles(true);
        dataSet.setCircleColor(chartBorderColor);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawValues(false);
        dataSet.setCircleRadius(1);
        dataSet.setHighlightLineWidth(2);
        dataSet.setHighlightEnabled(true);
        dataSet.setDrawHighlightIndicators(true);
        dataSet.setHighLightColor(chartBorderColor); // color for highlight indicator
        return dataSet;
    }

    public void getCMCChart() {
        APIHelper apiHelper = new APIHelper(this);
        apiHelper.callJsonWsGet(String.format(ChartActivity.CURRENT_CHART_URL, cryptoID),null,chartListener,true);
    }

    private APIResponseListener chartListener = new APIResponseListener() {
        @Override
        public void handleResponse(final String response) {

            try {
                final TextView percentChangeText = findViewById(R.id.percent_change);
                TextView currentPriceTextView = findViewById(R.id.current_price);
                lineChart.setEnabled(true);
                ObjectMapper mapper = new ObjectMapper();
                CMCChartData cmcChartData = mapper.readValue(response, CMCChartData.class);


                List<Entry> closePrices = new ArrayList<>();
                for (List<Float> priceTimeUnit : cmcChartData.getPriceUSD()) {
                    closePrices.add(new Entry(priceTimeUnit.get(0), priceTimeUnit.get(1)));
                }
                if (closePrices.size() == 0) {
                    lineChart.setData(null);
                    lineChart.setEnabled(false);
                    lineChart.invalidate();
                    percentChangeText.setText("");
                    currentPriceTextView.setText("");
                    return;
                }
                float currPrice = closePrices.get(closePrices.size() - 1).getY();
                currentPriceTextView.setText(String.format(getString(R.string.price_format_no_word), currPrice));
                currentPriceTextView.setTextColor(Color.BLACK);
                float firstPrice = closePrices.get(0).getY();
                // Handle edge case where we dont have data for the interval on the chart. E.g. user selects
                // 3 month window, but we only have data for last month
                for (Entry e: closePrices) {
                    if (firstPrice != 0) {
                        break;
                    } else {
                        firstPrice = e.getY();
                    }
                }
                float difference = (currPrice - firstPrice);
                float percentChange = (difference / firstPrice) * 100;
                if (percentChange < 0) {
                    percentChangeText.setText(String.format(getString(R.string.negative_variable_pct_change_with_dollars_format), currentTimeWindow, percentChange, Math
                            .abs(difference)));
                } else {
                    percentChangeText.setText(String.format(getString(R.string.positive_variable_pct_change_with_dollars_format), currentTimeWindow, percentChange, Math
                            .abs(difference)));
                }
                setColors(percentChange);
                percentChangeText.setTextColor(percentageColor);
                LineDataSet dataSet = setUpLineDataSet(closePrices);
                LineData lineData = new LineData(dataSet);
                lineChart.setDoubleTapToZoomEnabled(false);
                lineChart.setScaleEnabled(false);
                lineChart.getDescription().setEnabled(false);
                lineChart.setData(lineData);
                lineChart.setContentDescription("");
                lineChart.animateX(800);
                lineChart.getLegend().setEnabled(false);
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setAvoidFirstLastClipping(true);
                lineChart.getAxisLeft().setEnabled(true);
                lineChart.getAxisLeft().setDrawGridLines(false);
                lineChart.getXAxis().setDrawGridLines(false);
                lineChart.getAxisLeft().setValueFormatter(new YAxisPriceFormatter());
                lineChart.getAxisRight().setEnabled(false);
                xAxis.setDrawAxisLine(true);
                xAxis.setValueFormatter(XAxisFormatter);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
                lineChart.invalidate();
            }
            catch (Exception e){
                Log.e("ERROR", "Server Error: " + e.getMessage(),e);
            }
        }

        @Override
        public void handleError(final String response) {
            Log.d("ERROR","Error due to " + response);
        }
    };

    public void setDayChecked(Calendar cal) {
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();
        cal.clear();
        CURRENT_CHART_URL = String.format(COIN_MARKETCAP_CHART_URL_WINDOW, cryptoID, startTime, endTime);
        currentTimeWindow = String.format(getString(R.string.oneDay));
        XAxisFormatter = dayCommaTimeDateFormatter;
    }

    public void setWeekChecked(Calendar cal) {
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -7);
        long startTime = cal.getTimeInMillis();
        cal.clear();
        CURRENT_CHART_URL = String.format(COIN_MARKETCAP_CHART_URL_WINDOW, cryptoID, startTime, endTime);
        currentTimeWindow = String.format(getString(R.string.Week));
        XAxisFormatter = monthSlashDayXAxisFormatter;
    }

    public void setMonthChecked(Calendar cal) {
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.MONTH, -1);
        long startTime = cal.getTimeInMillis();
        cal.clear();
        CURRENT_CHART_URL = String.format(COIN_MARKETCAP_CHART_URL_WINDOW, cryptoID, startTime, endTime);
        currentTimeWindow = String.format(getString(R.string.Month));
        XAxisFormatter = monthSlashDayXAxisFormatter;
    }

    public void setThreeMonthChecked(Calendar cal) {
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.MONTH, -3);
        long startTime = cal.getTimeInMillis();
        cal.clear();
        CURRENT_CHART_URL = String.format(COIN_MARKETCAP_CHART_URL_WINDOW, cryptoID, startTime, endTime);
        currentTimeWindow = String.format(getString(R.string.threeMonth));
        XAxisFormatter = monthSlashDayXAxisFormatter;
    }

    public void setYearChecked(Calendar cal) {
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.YEAR, -1);
        long startTime = cal.getTimeInMillis();
        cal.clear();
        CURRENT_CHART_URL = String.format(COIN_MARKETCAP_CHART_URL_WINDOW, cryptoID, startTime, endTime);
        currentTimeWindow = String.format(getString(R.string.Year));
        XAxisFormatter = monthSlashYearFormatter;
    }

    public void setAllTimeChecked() {
        currentTimeWindow = String.format(getString(R.string.AllTime));
        CURRENT_CHART_URL = String.format(COIN_MARKETCAP_CHART_URL_ALL_DATA, cryptoID);
        XAxisFormatter = monthSlashYearFormatter;
    }

    public void setTable(CMCCoin coinObject) {
        String usdFormat = getString(R.string.usd_format);
        String negativePctFormat = getString(R.string.negative_pct_format);
        String positivePctFormat = getString(R.string.positive_pct_format);
        int negativeRedColor = getResources().getColor(R.color.percentNegativeRed);
        int positiveGreenColor = getResources().getColor(R.color.percentPositiveGreen);
        TextView nameTextView;
        nameTextView = findViewById(R.id.tableNameDataTextView);
        if (coinObject.getName() == null) {
            nameTextView.setText("N/A");
        } else {
            nameTextView.setText(coinObject.getName());
        }

        TextView priceUSDTextView = findViewById(R.id.tablePriceUSDDataTextView);
        if (coinObject.getPrice_usd() == null) {
            priceUSDTextView.setText("N/A");
        } else {
            priceUSDTextView.setText(String.format(usdFormat, Double.parseDouble(coinObject.getPrice_usd())));
        }

        TextView priceBTCTextView = findViewById(R.id.tablePriceBTCDataTextView);
        if (coinObject.getPrice_btc() == null) {
            priceBTCTextView.setText("N/A");
        } else {
            priceBTCTextView.setText(String.format(getString(R.string.btc_format), coinObject.getPrice_btc()));
        }

        TextView volumeTextView = findViewById(R.id.tableVolUSDDataTextView);
        if (coinObject.getVolume_usd_24h() == null) {
            volumeTextView.setText("N/A");
        } else {
            volumeTextView.setText(String.format(usdFormat, Double.parseDouble(coinObject.getVolume_usd_24h())));
        }

        TextView mktCapTextView = findViewById(R.id.tableMktCapDataTextView);
        if (coinObject.getMarket_cap_usd() == null) {
            mktCapTextView.setText("N/A");
        } else {
            mktCapTextView.setText(String.format(usdFormat, Double.parseDouble(coinObject.getMarket_cap_usd())));
        }

        TextView availSupplyTextView = findViewById(R.id.tableAvailableSupplyDataTextView);
        if (coinObject.getAvailable_supply() == null) {
            availSupplyTextView.setText("N/A");
        } else {
            availSupplyTextView.setText(rawNumberFormat.format(Double.parseDouble(coinObject.getAvailable_supply())));
        }

        TextView totalSupplyTextView = (TextView) findViewById(R.id.tableTotalSupplyDataTextView);
        if (coinObject.getTotal_supply() == null) {
            totalSupplyTextView.setText("N/A");
        } else {
            totalSupplyTextView.setText(rawNumberFormat.format(Double.parseDouble(coinObject.getTotal_supply())));
        }

        TextView maxSupplyTextView = findViewById(R.id.tableMaxSupplyDataTextView);
        if (coinObject.getMax_supply() == null) {
            maxSupplyTextView.setText("N/A");
        } else {
            maxSupplyTextView.setText(rawNumberFormat.format(Double.parseDouble(coinObject.getMax_supply())));
        }

        TextView oneHrChangeTextView = findViewById(R.id.table1hrChangeDataTextView);
        if (coinObject.getPercent_change_1h() == null) {
            oneHrChangeTextView.setText("N/A");
        } else {
            double amount = Double.parseDouble(coinObject.getPercent_change_1h());
            if (amount >= 0) {
                oneHrChangeTextView.setText(String.format(positivePctFormat, amount));
                oneHrChangeTextView.setTextColor(positiveGreenColor);
            } else {
                oneHrChangeTextView.setText(String.format(negativePctFormat, amount));
                oneHrChangeTextView.setTextColor(negativeRedColor);
            }
        }

        TextView dayChangeTextView = findViewById(R.id.table24hrChangeDataTextView);
        if (coinObject.getPercent_change_24h() == null) {
            dayChangeTextView.setText("N/A");
        } else {
            double amount = Double.parseDouble(coinObject.getPercent_change_24h());
            if (amount >= 0) {
                dayChangeTextView.setText(String.format(positivePctFormat, amount));
                dayChangeTextView.setTextColor(positiveGreenColor);
            } else {
                dayChangeTextView.setText(String.format(negativePctFormat, amount));
                dayChangeTextView.setTextColor(negativeRedColor);
            }
        }

        TextView weekChangeTextView = findViewById(R.id.tableWeekChangeDataTextView);
        if (coinObject.getPercent_change_7d() == null) {
            weekChangeTextView.setText("N/A");
        } else {
            double amount = Double.parseDouble(coinObject.getPercent_change_7d());
            if (amount >= 0) {
                weekChangeTextView.setText(String.format(positivePctFormat, amount));
                weekChangeTextView.setTextColor(positiveGreenColor);
            } else {
                weekChangeTextView.setText(String.format(negativePctFormat, amount));
                weekChangeTextView.setTextColor(negativeRedColor);
            }
        }

    }

    public void onRefresh() {
        getCMCChart();
    }

}
