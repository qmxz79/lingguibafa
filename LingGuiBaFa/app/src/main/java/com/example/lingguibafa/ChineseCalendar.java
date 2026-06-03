package com.example.lingguibafa;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class ChineseCalendar {
    // 天干
    private static final String[] HEAVENLY_STEMS = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    // 地支
    private static final String[] EARTHLY_BRANCHES = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};

    public static String getDayStemBranch(Date inputDate) {
        Calendar baseCal = Calendar.getInstance();
        baseCal.set(2024, 0, 1); // Month is 0-based in Calendar
        Date baseDate = baseCal.getTime();

        long diff = inputDate.getTime() - baseDate.getTime();
        long daysDiff = diff / (24 * 60 * 60 * 1000);

        int stemIndex = (int) (daysDiff % 10);
        int branchIndex = (int) (daysDiff % 12);

        return HEAVENLY_STEMS[stemIndex] + EARTHLY_BRANCHES[branchIndex];
    }

    public static String getHourStemBranch(String dayStem, int hour) {
        int doubleHour;
        if (hour == 23 || hour == 0) {
            doubleHour = 0; // 子时
        } else {
            doubleHour = ((hour + 1) % 24) / 2;
        }

        int dayStemIndex = -1;
        for (int i = 0; i < HEAVENLY_STEMS.length; i++) {
            if (HEAVENLY_STEMS[i].equals(dayStem)) {
                dayStemIndex = i;
                break;
            }
        }

        int ziStemIndex = 0;
        if (dayStem.equals("甲") || dayStem.equals("己")) {
            ziStemIndex = 0;
        } else if (dayStem.equals("乙") || dayStem.equals("庚")) {
            ziStemIndex = 2;
        } else if (dayStem.equals("丙") || dayStem.equals("辛")) {
            ziStemIndex = 4;
        } else if (dayStem.equals("丁") || dayStem.equals("壬")) {
            ziStemIndex = 6;
        } else if (dayStem.equals("戊") || dayStem.equals("癸")) {
            ziStemIndex = 8;
        }

        int hourStemIndex = (ziStemIndex + doubleHour) % 10;
        int hourBranchIndex = doubleHour;

        return HEAVENLY_STEMS[hourStemIndex] + EARTHLY_BRANCHES[hourBranchIndex];
    }
}