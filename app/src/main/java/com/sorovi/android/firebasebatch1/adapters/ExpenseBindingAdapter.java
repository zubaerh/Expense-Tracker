package com.sorovi.android.firebasebatch1.adapters;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpenseBindingAdapter {
    @BindingAdapter(value = "app:setDate")
    public static void setData(TextView textView, long date){
        final String dateString = new SimpleDateFormat("hh:mm a EEE, MMM dd,yyyy").format(new Date(date));
        textView.setText("At  "+dateString);
    }
}
