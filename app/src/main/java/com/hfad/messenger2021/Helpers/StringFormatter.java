package com.hfad.messenger2021.Helpers;

import android.annotation.SuppressLint;
import android.content.res.Resources;

import com.hfad.messenger2021.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StringFormatter {
    public static String dateFormat(String date){
        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8, 10);
        String hours = date.substring(11, 13);
        String minutes = date.substring(14, 16);

        @SuppressLint("SimpleDateFormat") String currentDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
        String currentYear = currentDate.substring(0, 4);
        String currentMonth = currentDate.substring(5, 7);
        String currentDay = currentDate.substring(8, 10);

        if(Integer.parseInt(year) == Integer.parseInt(currentYear)){
            if(Integer.parseInt(month) == Integer.parseInt(currentMonth)){
                if(Integer.parseInt(day) == Integer.parseInt(currentDay)){
                    return String.format("%s:%s", hours, minutes);
                }
                else{
                    return String.format("%s.%s", month, day);
                }
            }
            else{
                return String.format("%s.%s", month, day);
            }
        }
        else{
            return String.format("%s.%s.%s", year, month, day);
        }
    }

    public static String nameFormat(String lastMessage, String friendsName, boolean wasSentByMe, String you){
        if (wasSentByMe){
            return String.format("%s: %s", you, lastMessage);
        }
        else{
            return String.format("%s: %s", friendsName, lastMessage);
        }
    }
}
