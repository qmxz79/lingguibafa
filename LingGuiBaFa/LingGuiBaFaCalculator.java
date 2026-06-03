import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class LingGuiBaFaCalculator {
    private JFrame frame;
    private JLabel resultLabel;
    private JLabel daySblabel;
    private JLabel hourSblabel;
    private JTextArea processArea;

    private static final String[] STEMS = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    private static final String[] BRANCHES = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};

    private static final Map<String, Integer> HOUR_BASE = new HashMap<>();
    static {
        HOUR_BASE.put("甲", 0); HOUR_BASE.put("己", 0);
        HOUR_BASE.put("乙", 2); HOUR_BASE.put("庚", 2);
        HOUR_BASE.put("丙", 4); HOUR_BASE.put("辛", 4);
        HOUR_BASE.put("丁", 6); HOUR_BASE.put("壬", 6);
        HOUR_BASE.put("戊", 8); HOUR_BASE.put("癸", 8);
    }

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

    private static final Map<Integer, String> ACUPOINT_MAP = new HashMap<>();
    static {
        ACUPOINT_MAP.put(1, "申脉"); ACUPOINT_MAP.put(2, "照海");
        ACUPOINT_MAP.put(3, "外关"); ACUPOINT_MAP.put(4, "足临泣");
        ACUPOINT_MAP.put(5, "照海"); ACUPOINT_MAP.put(6, "公孙");
        ACUPOINT_MAP.put(7, "后溪"); ACUPOINT_MAP.put(8, "内关");
        ACUPOINT_MAP.put(9, "列缺");
    }

    public LingGuiBaFaCalculator() {
        frame = new JFrame("灵龟八法取穴计算器");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 400);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JButton calculateButton = new JButton("计算当前日干支和时干支");
        calculateButton.addActionListener(e -> calculateStemBranch());
        topPanel.add(calculateButton);

        JButton acupointButton = new JButton("计算灵龟八法开穴");
        acupointButton.addActionListener(e -> calculateAcupoint());
        topPanel.add(acupointButton);

        frame.add(topPanel, BorderLayout.NORTH);

        JPanel resultPanel = new JPanel(new GridLayout(4, 1, 0, 5));
        resultPanel.setBorder(BorderFactory.createTitledBorder("计算结果"));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        daySblabel = new JLabel("日干支：");
        hourSblabel = new JLabel("时干支：");
        resultLabel = new JLabel("灵龟八法开穴：");
        resultPanel.add(daySblabel);
        resultPanel.add(hourSblabel);
        resultPanel.add(resultLabel);

        processArea = new JTextArea(8, 40);
        processArea.setEditable(false);
        processArea.setFont(new Font("Serif", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(processArea);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(resultPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        frame.add(centerPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private String getDayStemBranch(LocalDateTime dateTime) {
        LocalDateTime baseDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        long daysBetween = ChronoUnit.DAYS.between(baseDate, dateTime);
        int stemIndex = (int) (((daysBetween % 10) + 10) % 10);
        int branchIndex = (int) (((daysBetween % 12) + 12) % 12);
        return STEMS[stemIndex] + BRANCHES[branchIndex];
    }

    private String getHourStemBranch(String dayStem, int hour) {
        int branchIndex;
        if (hour == 23 || hour == 0) {
            branchIndex = 0;
        } else {
            branchIndex = ((hour + 1) % 24) / 2;
        }
        int stemIndex = (HOUR_BASE.get(dayStem) + branchIndex) % 10;
        return STEMS[stemIndex] + BRANCHES[branchIndex];
    }

    private void calculateStemBranch() {
        LocalDateTime now = LocalDateTime.now();
        String daySB = getDayStemBranch(now);
        String dayStem = daySB.substring(0, 1);
        String hourSB = getHourStemBranch(dayStem, now.getHour());
        daySblabel.setText("日干支：" + daySB);
        hourSblabel.setText("时干支：" + hourSB);
        resultLabel.setText("灵龟八法开穴：");
        processArea.setText("");
    }

    private void calculateAcupoint() {
        LocalDateTime now = LocalDateTime.now();
        String daySB = getDayStemBranch(now);
        String dayStem = daySB.substring(0, 1);
        String dayBranch = daySB.substring(1, 2);
        String hourSB = getHourStemBranch(dayStem, now.getHour());
        String hourStem = hourSB.substring(0, 1);
        String hourBranch = hourSB.substring(1, 2);

        int dsNum = LG_DS.get(dayStem);
        int dbNum = LG_DB.get(dayBranch);
        int hsNum = LG_HS.get(hourStem);
        int hbNum = LG_HB.get(hourBranch);
        int total = dsNum + dbNum + hsNum + hbNum;

        int dayStemIndex = -1;
        for (int i = 0; i < STEMS.length; i++) {
            if (STEMS[i].equals(dayStem)) {
                dayStemIndex = i;
                break;
            }
        }
        boolean isYang = dayStemIndex % 2 == 0;
        int divisor = isYang ? 9 : 6;
        int remainder = total % divisor;
        if (remainder == 0) remainder = divisor;

        String acupoint = ACUPOINT_MAP.get(remainder);

        daySblabel.setText("日干支：" + daySB);
        hourSblabel.setText("时干支：" + hourSB);
        resultLabel.setText("灵龟八法开穴：" + acupoint);

        StringBuilder sb = new StringBuilder();
        sb.append("计算过程：\n");
        sb.append("日干（").append(dayStem).append("）：").append(dsNum).append("\n");
        sb.append("日支（").append(dayBranch).append("）：").append(dbNum).append("\n");
        sb.append("时干（").append(hourStem).append("）：").append(hsNum).append("\n");
        sb.append("时支（").append(hourBranch).append("）：").append(hbNum).append("\n");
        sb.append("总和：").append(total).append("\n");
        sb.append(isYang ? "阳" : "阴").append("日，除以").append(divisor).append("，余数为").append(remainder).append("\n");
        sb.append("对应穴位：").append(acupoint);
        processArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LingGuiBaFaCalculator::new);
    }
}
