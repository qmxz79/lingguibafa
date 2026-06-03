package com.example.lingguibafa;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private DatePicker datePicker;
    private TimePicker timePicker;
    private CheckBox cbSolar;
    private LinearLayout solarPanel;
    private EditText etLongitude;
    private TextView tvDaySB, tvHourSB, tvBranch;
    private TextView tvLingGui, tvFeiTeng, tvNaZi, tvNaJia, tvSolar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        cbSolar = findViewById(R.id.cbSolar);
        solarPanel = findViewById(R.id.solarPanel);
        etLongitude = findViewById(R.id.etLongitude);
        tvDaySB = findViewById(R.id.tvDaySB);
        tvHourSB = findViewById(R.id.tvHourSB);
        tvBranch = findViewById(R.id.tvBranch);
        tvLingGui = findViewById(R.id.tvLingGui);
        tvFeiTeng = findViewById(R.id.tvFeiTeng);
        tvNaZi = findViewById(R.id.tvNaZi);
        tvNaJia = findViewById(R.id.tvNaJia);
        tvSolar = findViewById(R.id.tvSolar);

        cbSolar.setOnCheckedChangeListener((b, checked) -> solarPanel.setVisibility(checked ? View.VISIBLE : View.GONE));
        findViewById(R.id.btnNow).setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            datePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            timePicker.setHour(c.get(Calendar.HOUR_OF_DAY));
            timePicker.setMinute(c.get(Calendar.MINUTE));
        });
        findViewById(R.id.btnCalc).setOnClickListener(v -> calculate());
    }

    private void calculate() {
        int y = datePicker.getYear(), m = datePicker.getMonth(), d = datePicker.getDayOfMonth();
        int h = timePicker.getHour(), mi = timePicker.getMinute();

        ChineseCalendar.SolarTime solar = null;
        if (cbSolar.isChecked()) {
            double lon = 116.4;
            try { lon = Double.parseDouble(etLongitude.getText().toString()); } catch (Exception ignored) {}
            solar = ChineseCalendar.calcSolarTime(y, m + 1, d, h, mi, lon);
            y = solar.year; m = solar.month - 1; d = solar.day; h = solar.hour;
        }

        Calendar cal = Calendar.getInstance();
        cal.set(y, m, d, h, mi);
        java.util.Date date = cal.getTime();

        String daySB = ChineseCalendar.getDayStemBranch(date);
        String hourSB = ChineseCalendar.getHourStemBranch(daySB.substring(0, 1), h);
        String branch = ChineseCalendar.getBranchLabel(h);

        tvDaySB.setText(daySB);
        tvHourSB.setText(hourSB);
        tvBranch.setText(branch);

        // 灵龟八法
        String[] lg = LingGuiBaFa.calcLingGui(daySB, hourSB);
        tvLingGui.setText(formatPair(lg));

        // 飞腾八法
        String[] ft = LingGuiBaFa.calcFeiTeng(hourSB);
        tvFeiTeng.setText(formatPair(ft));

        // 子午流注纳子法
        String[] nz = LingGuiBaFa.calcNaZi(hourSB);
        tvNaZi.setText(formatNaZi(nz));

        // 子午流注纳甲法
        LingGuiBaFa.NaJiaPoint[] nj = LingGuiBaFa.calcNaJia(daySB, hourSB);
        tvNaJia.setText(formatNaJia(nj));

        // 真太阳时
        if (solar != null) {
            tvSolar.setVisibility(View.VISIBLE);
            tvSolar.setText(String.format("经度修正 %+.0f分 · 均时差 %+.0f分 · 合计 %+.0f分\n真太阳时：%02d:%02d",
                    solar.lonCorrection, solar.eot, solar.totalDiff, solar.hour, Math.round(solar.minute)));
        } else {
            tvSolar.setVisibility(View.GONE);
        }
    }

    private String formatPair(String[] pair) {
        String[] m = LingGuiBaFa.getPointMeta(pair[0]);
        String s = pair[0] + "（" + m[0] + "·通" + m[1] + "）";
        if (pair[1] != null && !pair[1].isEmpty()) {
            String[] p = LingGuiBaFa.getPointMeta(pair[1]);
            s += " + " + pair[1] + "（" + p[0] + "·通" + p[1] + "）";
        }
        return s;
    }

    private String formatNaZi(String[] row) {
        String s = row[1] + "·" + row[2] + "（本穴）";
        if (!row[1].equals(row[3]))
            s += " + " + row[3] + "·" + row[4] + "（原穴）";
        return row[0] + "当令\n" + s;
    }

    private String formatNaJia(LingGuiBaFa.NaJiaPoint[] points) {
        if (points == null) return "此时辰闭穴";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < points.length; i++) {
            if (i > 0) sb.append("  ");
            sb.append(points[i].type).append(":").append(points[i].name)
                    .append("·").append(points[i].code).append("（").append(points[i].meridian).append("）");
        }
        return sb.toString();
    }
}
