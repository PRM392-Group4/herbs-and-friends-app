package com.group4.herbs_and_friends_app.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class DisplayFormat {

    // Format from money/price of type long to String for display
    // Eg: 200000 -> 200.000 ₫
    public static String toMoneyDisplayString(long money) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(money) + " ₫";
    }
}
