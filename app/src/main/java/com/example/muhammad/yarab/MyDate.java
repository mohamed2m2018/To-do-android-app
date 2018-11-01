package com.example.muhammad.yarab;

/**
 * Created by muhammad on 16/07/18.
 */

import android.widget.TextView;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.lang.System;
public class MyDate {




    static Calendar cal = Calendar.getInstance();

    public String ShowDate (int number){
        if(number==1)
            cal.add(Calendar.DATE, 1);
        if(number==-1)
            cal.add(Calendar.DATE, -1);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy");



   return  (dateFormat.format(cal.getTime()));
    }

}
