package com.example.lingguibafa;

import java.util.HashMap;
import java.util.Map;

public class LingGuiBaFa {

    // ─── 灵龟八法日干支数字表 ───
    private static final Map<String, Integer> LG_DS = new HashMap<>();
    static {
        LG_DS.put("甲", 10); LG_DS.put("己", 10);
        LG_DS.put("乙", 9);  LG_DS.put("庚", 9);
        LG_DS.put("丁", 8);  LG_DS.put("壬", 8);
        LG_DS.put("戊", 7);  LG_DS.put("癸", 7);
        LG_DS.put("丙", 7);  LG_DS.put("辛", 7);
    }
    private static final Map<String, Integer> LG_DB = new HashMap<>();
    static {
        LG_DB.put("辰", 10); LG_DB.put("戌", 10); LG_DB.put("丑", 10); LG_DB.put("未", 10);
        LG_DB.put("申", 9);  LG_DB.put("酉", 9);
        LG_DB.put("寅", 8);  LG_DB.put("卯", 8);
        LG_DB.put("巳", 7);  LG_DB.put("午", 7);  LG_DB.put("亥", 7);  LG_DB.put("子", 7);
    }
    private static final Map<String, Integer> LG_HS = new HashMap<>();
    static {
        LG_HS.put("甲", 9); LG_HS.put("己", 9);
        LG_HS.put("乙", 8); LG_HS.put("庚", 8);
        LG_HS.put("丙", 7); LG_HS.put("辛", 7);
        LG_HS.put("丁", 6); LG_HS.put("壬", 6);
        LG_HS.put("戊", 5); LG_HS.put("癸", 5);
    }
    private static final Map<String, Integer> LG_HB = new HashMap<>();
    static {
        LG_HB.put("子", 9); LG_HB.put("午", 9);
        LG_HB.put("丑", 8); LG_HB.put("未", 8);
        LG_HB.put("寅", 7); LG_HB.put("申", 7);
        LG_HB.put("卯", 6); LG_HB.put("酉", 6);
        LG_HB.put("辰", 5); LG_HB.put("戌", 5);
        LG_HB.put("巳", 4); LG_HB.put("亥", 4);
    }
    private static final Map<Integer, String> LG_MAP = new HashMap<>();
    static {
        LG_MAP.put(1, "申脉"); LG_MAP.put(2, "照海");
        LG_MAP.put(3, "外关"); LG_MAP.put(4, "足临泣");
        LG_MAP.put(5, "照海"); LG_MAP.put(6, "公孙");
        LG_MAP.put(7, "后溪"); LG_MAP.put(8, "内关");
        LG_MAP.put(9, "列缺");
    }

    // ─── 穴位配对 & 属性 ───
    private static final Map<String, String> PAIR = new HashMap<>();
    static {
        PAIR.put("申脉", "后溪"); PAIR.put("后溪", "申脉");
        PAIR.put("照海", "列缺"); PAIR.put("列缺", "照海");
        PAIR.put("外关", "足临泣"); PAIR.put("足临泣", "外关");
        PAIR.put("内关", "公孙"); PAIR.put("公孙", "内关");
    }
    private static final Map<String, String[]> POINT_META = new HashMap<>();
    static {
        POINT_META.put("申脉", new String[]{"膀胱经", "阳跷脉"});
        POINT_META.put("照海", new String[]{"肾经", "阴跷脉"});
        POINT_META.put("外关", new String[]{"三焦经", "阳维脉"});
        POINT_META.put("足临泣", new String[]{"胆经", "带脉"});
        POINT_META.put("公孙", new String[]{"脾经", "冲脉"});
        POINT_META.put("后溪", new String[]{"小肠经", "督脉"});
        POINT_META.put("内关", new String[]{"心包经", "阴维脉"});
        POINT_META.put("列缺", new String[]{"肺经", "任脉"});
    }

    // ─── 飞腾八法 ───
    private static final Map<String, String> FT_MAP = new HashMap<>();
    static {
        FT_MAP.put("甲", "公孙"); FT_MAP.put("壬", "公孙");
        FT_MAP.put("丙", "内关");
        FT_MAP.put("戊", "足临泣");
        FT_MAP.put("庚", "外关");
        FT_MAP.put("辛", "后溪");
        FT_MAP.put("乙", "申脉"); FT_MAP.put("癸", "申脉");
        FT_MAP.put("己", "列缺");
        FT_MAP.put("丁", "照海");
    }

    // ─── 子午流注纳子法 ───
    private static final Map<String, String[]> NAZI_TABLE = new HashMap<>();
    static {
        NAZI_TABLE.put("子", new String[]{"胆经", "足临泣", "GB41", "丘墟", "GB40"});
        NAZI_TABLE.put("丑", new String[]{"肝经", "大敦", "LR1", "太冲", "LR3"});
        NAZI_TABLE.put("寅", new String[]{"肺经", "太渊", "LU9", "太渊", "LU9"});
        NAZI_TABLE.put("卯", new String[]{"大肠经", "阳溪", "LI5", "合谷", "LI4"});
        NAZI_TABLE.put("辰", new String[]{"胃经", "足三里", "ST36", "冲阳", "ST42"});
        NAZI_TABLE.put("巳", new String[]{"脾经", "太白", "SP3", "太白", "SP3"});
        NAZI_TABLE.put("午", new String[]{"心经", "少府", "HT8", "神门", "HT7"});
        NAZI_TABLE.put("未", new String[]{"小肠经", "阳谷", "SI5", "腕骨", "SI4"});
        NAZI_TABLE.put("申", new String[]{"膀胱经", "昆仑", "BL60", "京骨", "BL64"});
        NAZI_TABLE.put("酉", new String[]{"肾经", "然谷", "KI2", "太溪", "KI3"});
        NAZI_TABLE.put("戌", new String[]{"心包经", "劳宫", "PC8", "大陵", "PC7"});
        NAZI_TABLE.put("亥", new String[]{"三焦经", "支沟", "TE6", "阳池", "TE4"});
    }

    // ─── 子午流注纳甲法穴位 ───
    public static class NaJiaPoint {
        public final String name, code, type, meridian;
        NaJiaPoint(String name, String code, String type, String meridian) {
            this.name = name; this.code = code; this.type = type; this.meridian = meridian;
        }
    }
    private static final Map<String, Map<String, NaJiaPoint[]>> NAJIA_TABLE = new HashMap<>();
    static {
        Map<String, NaJiaPoint[]> m;
        m = new HashMap<>();
        m.put("甲戌", new NaJiaPoint[]{new NaJiaPoint("窍阴", "GB44", "井", "胆经")});
        m.put("丙子", new NaJiaPoint[]{new NaJiaPoint("前谷", "SI2", "荥", "小肠经")});
        m.put("戊寅", new NaJiaPoint[]{new NaJiaPoint("陷谷", "ST43", "输", "胃经"), new NaJiaPoint("丘墟", "GB40", "原", "胆经")});
        m.put("庚辰", new NaJiaPoint[]{new NaJiaPoint("阳溪", "LI5", "经", "大肠经")});
        m.put("壬午", new NaJiaPoint[]{new NaJiaPoint("委中", "BL40", "合", "膀胱经")});
        m.put("甲申", new NaJiaPoint[]{new NaJiaPoint("液门", "TE2", "荥", "三焦经")});
        NAJIA_TABLE.put("甲", m);

        m = new HashMap<>();
        m.put("乙酉", new NaJiaPoint[]{new NaJiaPoint("大敦", "LR1", "井", "肝经")});
        m.put("丁亥", new NaJiaPoint[]{new NaJiaPoint("少府", "HT8", "荥", "心经")});
        m.put("己丑", new NaJiaPoint[]{new NaJiaPoint("太白", "SP3", "输", "脾经"), new NaJiaPoint("太冲", "LR3", "原", "肝经")});
        m.put("辛卯", new NaJiaPoint[]{new NaJiaPoint("经渠", "LU8", "经", "肺经")});
        m.put("癸巳", new NaJiaPoint[]{new NaJiaPoint("阴谷", "KI10", "合", "肾经")});
        m.put("乙未", new NaJiaPoint[]{new NaJiaPoint("劳宫", "PC8", "荥", "心包经")});
        NAJIA_TABLE.put("乙", m);

        m = new HashMap<>();
        m.put("丙申", new NaJiaPoint[]{new NaJiaPoint("少泽", "SI1", "井", "小肠经")});
        m.put("戊戌", new NaJiaPoint[]{new NaJiaPoint("内庭", "ST44", "荥", "胃经")});
        m.put("庚子", new NaJiaPoint[]{new NaJiaPoint("三间", "LI3", "输", "大肠经"), new NaJiaPoint("腕骨", "SI4", "原", "小肠经")});
        m.put("壬寅", new NaJiaPoint[]{new NaJiaPoint("昆仑", "BL60", "经", "膀胱经")});
        m.put("甲辰", new NaJiaPoint[]{new NaJiaPoint("阳陵泉", "GB34", "合", "胆经")});
        m.put("丙午", new NaJiaPoint[]{new NaJiaPoint("中渚", "TE3", "输", "三焦经")});
        NAJIA_TABLE.put("丙", m);

        m = new HashMap<>();
        m.put("丁未", new NaJiaPoint[]{new NaJiaPoint("少冲", "HT9", "井", "心经")});
        m.put("己酉", new NaJiaPoint[]{new NaJiaPoint("大都", "SP2", "荥", "脾经")});
        m.put("辛亥", new NaJiaPoint[]{new NaJiaPoint("太渊", "LU9", "输", "肺经"), new NaJiaPoint("神门", "HT7", "原", "心经")});
        m.put("癸丑", new NaJiaPoint[]{new NaJiaPoint("复溜", "KI7", "经", "肾经")});
        m.put("乙卯", new NaJiaPoint[]{new NaJiaPoint("曲泉", "LR8", "合", "肝经")});
        m.put("丁巳", new NaJiaPoint[]{new NaJiaPoint("大陵", "PC7", "输", "心包经")});
        NAJIA_TABLE.put("丁", m);

        m = new HashMap<>();
        m.put("戊午", new NaJiaPoint[]{new NaJiaPoint("厉兑", "ST45", "井", "胃经")});
        m.put("庚申", new NaJiaPoint[]{new NaJiaPoint("二间", "LI2", "荥", "大肠经")});
        m.put("壬戌", new NaJiaPoint[]{new NaJiaPoint("束骨", "BL65", "输", "膀胱经"), new NaJiaPoint("冲阳", "ST42", "原", "胃经")});
        m.put("甲子", new NaJiaPoint[]{new NaJiaPoint("阳辅", "GB38", "经", "胆经")});
        m.put("丙寅", new NaJiaPoint[]{new NaJiaPoint("小海", "SI8", "合", "小肠经")});
        m.put("戊辰", new NaJiaPoint[]{new NaJiaPoint("支沟", "TE6", "经", "三焦经")});
        NAJIA_TABLE.put("戊", m);

        m = new HashMap<>();
        m.put("己巳", new NaJiaPoint[]{new NaJiaPoint("隐白", "SP1", "井", "脾经")});
        m.put("辛未", new NaJiaPoint[]{new NaJiaPoint("鱼际", "LU10", "荥", "肺经")});
        m.put("癸酉", new NaJiaPoint[]{new NaJiaPoint("太溪", "KI3", "输", "肾经"), new NaJiaPoint("太白", "SP3", "原", "脾经")});
        m.put("乙亥", new NaJiaPoint[]{new NaJiaPoint("中封", "LR4", "经", "肝经")});
        m.put("丁丑", new NaJiaPoint[]{new NaJiaPoint("少海", "HT3", "合", "心经")});
        m.put("己卯", new NaJiaPoint[]{new NaJiaPoint("间使", "PC5", "经", "心包经")});
        NAJIA_TABLE.put("己", m);

        m = new HashMap<>();
        m.put("庚辰", new NaJiaPoint[]{new NaJiaPoint("商阳", "LI1", "井", "大肠经")});
        m.put("壬午", new NaJiaPoint[]{new NaJiaPoint("通谷", "BL66", "荥", "膀胱经")});
        m.put("甲申", new NaJiaPoint[]{new NaJiaPoint("足临泣", "GB41", "输", "胆经"), new NaJiaPoint("合谷", "LI4", "原", "大肠经")});
        m.put("丙戌", new NaJiaPoint[]{new NaJiaPoint("阳谷", "SI5", "经", "小肠经")});
        m.put("戊子", new NaJiaPoint[]{new NaJiaPoint("足三里", "ST36", "合", "胃经")});
        m.put("庚寅", new NaJiaPoint[]{new NaJiaPoint("天井", "TE10", "合", "三焦经")});
        NAJIA_TABLE.put("庚", m);

        m = new HashMap<>();
        m.put("辛卯", new NaJiaPoint[]{new NaJiaPoint("少商", "LU11", "井", "肺经")});
        m.put("癸巳", new NaJiaPoint[]{new NaJiaPoint("然谷", "KI2", "荥", "肾经")});
        m.put("乙未", new NaJiaPoint[]{new NaJiaPoint("太冲", "LR3", "输", "肝经"), new NaJiaPoint("太渊", "LU9", "原", "肺经")});
        m.put("丁酉", new NaJiaPoint[]{new NaJiaPoint("灵道", "HT4", "经", "心经")});
        m.put("己亥", new NaJiaPoint[]{new NaJiaPoint("阴陵泉", "SP9", "合", "脾经")});
        m.put("辛丑", new NaJiaPoint[]{new NaJiaPoint("曲泽", "PC3", "合", "心包经")});
        NAJIA_TABLE.put("辛", m);

        m = new HashMap<>();
        m.put("壬寅", new NaJiaPoint[]{new NaJiaPoint("至阴", "BL67", "井", "膀胱经")});
        m.put("甲辰", new NaJiaPoint[]{new NaJiaPoint("侠溪", "GB43", "荥", "胆经")});
        m.put("丙午", new NaJiaPoint[]{
            new NaJiaPoint("后溪", "SI3", "输", "小肠经"),
            new NaJiaPoint("京骨", "BL64", "原", "膀胱经"),
            new NaJiaPoint("阳池", "TE4", "原", "三焦经")});
        m.put("戊申", new NaJiaPoint[]{new NaJiaPoint("解溪", "ST41", "经", "胃经")});
        m.put("庚戌", new NaJiaPoint[]{new NaJiaPoint("曲池", "LI11", "合", "大肠经")});
        m.put("壬子", new NaJiaPoint[]{new NaJiaPoint("关冲", "TE1", "井", "三焦经")});
        NAJIA_TABLE.put("壬", m);

        m = new HashMap<>();
        m.put("癸亥", new NaJiaPoint[]{new NaJiaPoint("涌泉", "KI1", "井", "肾经")});
        m.put("乙丑", new NaJiaPoint[]{new NaJiaPoint("行间", "LR2", "荥", "肝经")});
        m.put("丁卯", new NaJiaPoint[]{
            new NaJiaPoint("神门", "HT7", "输", "心经"),
            new NaJiaPoint("太溪", "KI3", "原", "肾经"),
            new NaJiaPoint("大陵", "PC7", "原", "心包经")});
        m.put("己巳", new NaJiaPoint[]{new NaJiaPoint("商丘", "SP5", "经", "脾经")});
        m.put("辛未", new NaJiaPoint[]{new NaJiaPoint("尺泽", "LU5", "合", "肺经")});
        m.put("癸酉", new NaJiaPoint[]{new NaJiaPoint("中冲", "PC9", "井", "心包经")});
        NAJIA_TABLE.put("癸", m);
    }

    // ════════════════════════════════════
    //  灵龟八法
    // ════════════════════════════════════
    public static String[] calcLingGui(String daySB, String hourSB) {
        String ds = daySB.substring(0, 1), db = daySB.substring(1, 2);
        String hs = hourSB.substring(0, 1), hb = hourSB.substring(1, 2);
        int total = LG_DS.get(ds) + LG_DB.get(db) + LG_HS.get(hs) + LG_HB.get(hb);
        boolean isYang = "甲丙戊庚壬".contains(ds);
        int div = isYang ? 9 : 6;
        int rem = total % div;
        if (rem == 0) rem = div;
        String main = LG_MAP.get(rem);
        return new String[]{main, PAIR.get(main)};
    }

    // ════════════════════════════════════
    //  飞腾八法
    // ════════════════════════════════════
    public static String[] calcFeiTeng(String hourSB) {
        String main = FT_MAP.get(hourSB.substring(0, 1));
        return new String[]{main, PAIR.get(main)};
    }

    // ════════════════════════════════════
    //  子午流注纳子法
    // ════════════════════════════════════
    public static String[] calcNaZi(String hourSB) {
        return NAZI_TABLE.get(hourSB.substring(1, 2));
    }

    // ════════════════════════════════════
    //  子午流注纳甲法
    // ════════════════════════════════════
    public static NaJiaPoint[] calcNaJia(String daySB, String hourSB) {
        Map<String, NaJiaPoint[]> dayTable = NAJIA_TABLE.get(daySB.substring(0, 1));
        if (dayTable == null) return null;
        return dayTable.get(hourSB);
    }

    public static String[] getPointMeta(String name) {
        return POINT_META.get(name);
    }
}
