package com.example.lingguibafa;

import java.util.Calendar;
import java.util.Date;

public class ChineseCalendar {
    private static final String[] HEAVENLY_STEMS = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    private static final String[] EARTHLY_BRANCHES = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};

    public static String getDayStemBranch(Date inputDate) {
        Calendar baseCal = Calendar.getInstance();
        baseCal.set(2024, 0, 1, 0, 0, 0);
        long diff = inputDate.getTime() - baseCal.getTimeInMillis();
        long daysDiff = diff / (24 * 60 * 60 * 1000);
        return HEAVENLY_STEMS[(int) ((daysDiff % 10 + 10) % 10)]
                + EARTHLY_BRANCHES[(int) ((daysDiff % 12 + 12) % 12)];
    }

    public static String getHourStemBranch(String dayStem, int hour) {
        int branchIdx = (hour == 23 || hour == 0) ? 0 : ((hour + 1) % 24) / 2;
        int ziStem = 0;
        if (dayStem.equals("甲") || dayStem.equals("己")) ziStem = 0;
        else if (dayStem.equals("乙") || dayStem.equals("庚")) ziStem = 2;
        else if (dayStem.equals("丙") || dayStem.equals("辛")) ziStem = 4;
        else if (dayStem.equals("丁") || dayStem.equals("壬")) ziStem = 6;
        else if (dayStem.equals("戊") || dayStem.equals("癸")) ziStem = 8;
        return HEAVENLY_STEMS[(ziStem + branchIdx) % 10] + EARTHLY_BRANCHES[branchIdx];
    }

    public static String getBranchLabel(int hour) {
        int idx = (hour == 23) ? 0 : ((hour + 1) / 2) % 12;
        return EARTHLY_BRANCHES[idx] + "时";
    }

    // ═══ 真太阳时修正 ═══
    public static class SolarTime {
        public final int year, month, day, hour, minute;
        public final double lonCorrection, eot, totalDiff;

        SolarTime(int y, int mo, int d, int h, int mi, double lc, double e, double td) {
            year = y; month = mo; day = d; hour = h; minute = mi;
            lonCorrection = lc; eot = e; totalDiff = td;
        }
    }

    public static SolarTime calcSolarTime(int year, int month, int day, int hour, int minute, double longitude) {
        double lonCorr = (longitude - 120) * 4;
        double eot = equationOfTime(year, month, day);
        double totalMinutes = hour * 60 + minute + lonCorr + eot;
        int modMin = (int) ((totalMinutes % 1440 + 1440) % 1440);
        int solarH = modMin / 60;
        int solarM = modMin % 60;

        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, hour, minute);
        cal.add(Calendar.MINUTE, (int) Math.round(lonCorr + eot));
        return new SolarTime(
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH),
                solarH, solarM,
                Math.round(lonCorr * 10) / 10.0, Math.round(eot * 10) / 10.0,
                Math.round((lonCorr + eot) * 10) / 10.0
        );
    }

    private static double equationOfTime(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        double b = (360.0 / 365.0) * (dayOfYear - 81) * (Math.PI / 180.0);
        return Math.round((9.87 * Math.sin(2 * b) - 7.53 * Math.cos(b) - 1.5 * Math.sin(b)) * 10) / 10.0;
    }
}
