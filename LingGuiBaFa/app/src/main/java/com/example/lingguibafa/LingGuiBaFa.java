package com.example.lingguibafa;

import java.util.HashMap;
import java.util.Map;

public class LingGuiBaFa {
    private static final Map<String, Integer> DAY_STEM_NUMBERS = new HashMap<>();
    static {
        DAY_STEM_NUMBERS.put("甲", 10); DAY_STEM_NUMBERS.put("己", 10);
        DAY_STEM_NUMBERS.put("乙", 9); DAY_STEM_NUMBERS.put("庚", 9);
        DAY_STEM_NUMBERS.put("丁", 8); DAY_STEM_NUMBERS.put("壬", 8);
        DAY_STEM_NUMBERS.put("戊", 7); DAY_STEM_NUMBERS.put("癸", 7);
        DAY_STEM_NUMBERS.put("丙", 7); DAY_STEM_NUMBERS.put("辛", 7);
    }

    private static final Map<String, Integer> DAY_BRANCH_NUMBERS = new HashMap<>();
    static {
        DAY_BRANCH_NUMBERS.put("辰", 10); DAY_BRANCH_NUMBERS.put("戌", 10);
        DAY_BRANCH_NUMBERS.put("丑", 10); DAY_BRANCH_NUMBERS.put("未", 10);
        DAY_BRANCH_NUMBERS.put("申", 9); DAY_BRANCH_NUMBERS.put("酉", 9);
        DAY_BRANCH_NUMBERS.put("寅", 8); DAY_BRANCH_NUMBERS.put("卯", 8);
        DAY_BRANCH_NUMBERS.put("巳", 7); DAY_BRANCH_NUMBERS.put("午", 7);
        DAY_BRANCH_NUMBERS.put("亥", 7); DAY_BRANCH_NUMBERS.put("子", 7);
    }

    private static final Map<String, Integer> HOUR_STEM_NUMBERS = new HashMap<>();
    static {
        HOUR_STEM_NUMBERS.put("甲", 9); HOUR_STEM_NUMBERS.put("己", 9);
        HOUR_STEM_NUMBERS.put("乙", 8); HOUR_STEM_NUMBERS.put("庚", 8);
        HOUR_STEM_NUMBERS.put("丙", 7); HOUR_STEM_NUMBERS.put("辛", 7);
        HOUR_STEM_NUMBERS.put("丁", 6); HOUR_STEM_NUMBERS.put("壬", 6);
        HOUR_STEM_NUMBERS.put("戊", 5); HOUR_STEM_NUMBERS.put("癸", 5);
    }

    private static final Map<String, Integer> HOUR_BRANCH_NUMBERS = new HashMap<>();
    static {
        HOUR_BRANCH_NUMBERS.put("子", 9); HOUR_BRANCH_NUMBERS.put("午", 9);
        HOUR_BRANCH_NUMBERS.put("丑", 8); HOUR_BRANCH_NUMBERS.put("未", 8);
        HOUR_BRANCH_NUMBERS.put("寅", 7); HOUR_BRANCH_NUMBERS.put("申", 7);
        HOUR_BRANCH_NUMBERS.put("卯", 6); HOUR_BRANCH_NUMBERS.put("酉", 6);
        HOUR_BRANCH_NUMBERS.put("辰", 5); HOUR_BRANCH_NUMBERS.put("戌", 5);
        HOUR_BRANCH_NUMBERS.put("巳", 4); HOUR_BRANCH_NUMBERS.put("亥", 4);
    }

    private static final Map<Integer, String> ACUPOINT_MAP = new HashMap<>();
    static {
        ACUPOINT_MAP.put(1, "申脉"); ACUPOINT_MAP.put(2, "照海");
        ACUPOINT_MAP.put(3, "外关"); ACUPOINT_MAP.put(4, "临泣");
        ACUPOINT_MAP.put(5, "照海"); ACUPOINT_MAP.put(6, "公孙");
        ACUPOINT_MAP.put(7, "后溪"); ACUPOINT_MAP.put(8, "内关");
        ACUPOINT_MAP.put(9, "列缺");
    }

    public static Map<String, Object> calculateAcupoint(String daySb, String hourSb) {
        String dayStem = daySb.substring(0, 1);
        String dayBranch = daySb.substring(1, 2);
        String hourStem = hourSb.substring(0, 1);
        String hourBranch = hourSb.substring(1, 2);

        int dayStemNum = DAY_STEM_NUMBERS.get(dayStem);
        int dayBranchNum = DAY_BRANCH_NUMBERS.get(dayBranch);
        int hourStemNum = HOUR_STEM_NUMBERS.get(hourStem);
        int hourBranchNum = HOUR_BRANCH_NUMBERS.get(hourBranch);

        int total = dayStemNum + dayBranchNum + hourStemNum + hourBranchNum;

        int dayStemIndex = -1;
        String[] heavenlyStems = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
        for (int i = 0; i < heavenlyStems.length; i++) {
            if (heavenlyStems[i].equals(dayStem)) {
                dayStemIndex = i;
                break;
            }
        }
        boolean isYang = dayStemIndex % 2 == 0;

        int divisor = isYang ? 9 : 6;
        int remainder = total % divisor;
        if (remainder == 0) {
            remainder = divisor;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("日干数", dayStemNum);
        result.put("日支数", dayBranchNum);
        result.put("时干数", hourStemNum);
        result.put("时支数", hourBranchNum);
        result.put("总和", total);
        result.put("阴阳", isYang ? "阳" : "阴");
        result.put("除数", divisor);
        result.put("余数", remainder);
        result.put("穴位", ACUPOINT_MAP.get(remainder));

        return result;
    }
}