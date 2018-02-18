package com.jj.cryptotrack.utils.formatters;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import java.util.Locale;

public class YAxisPriceFormatter implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float money, AxisBase axis) {
        String moneyString = String.format(Locale.ENGLISH, "%.2f", money);
        return "$" + moneyString;
    }
}
