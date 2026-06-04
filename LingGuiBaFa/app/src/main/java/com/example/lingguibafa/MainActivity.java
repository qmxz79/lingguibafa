package com.example.lingguibafa;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private CheckBox cbSolar;
    private LinearLayout solarPanel;
    private EditText etLongitude;
    private TextView tvDaySB, tvHourSB, tvBranch;
    private TextView tvLingGui, tvFeiTeng, tvNaZi, tvNaJia, tvSolar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        findViewById(R.id.btnCalc).setOnClickListener(v -> calculate());

        calculate();
    }

    private void calculate() {
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR), m = c.get(Calendar.MONTH), d = c.get(Calendar.DAY_OF_MONTH);
        int h = c.get(Calendar.HOUR_OF_DAY), mi = c.get(Calendar.MINUTE);

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

        String[] lg = LingGuiBaFa.calcLingGui(daySB, hourSB);
        tvLingGui.setText(formatPair(lg));

        String[] ft = LingGuiBaFa.calcFeiTeng(hourSB);
        tvFeiTeng.setText(formatPair(ft));

        String[] nz = LingGuiBaFa.calcNaZi(hourSB);
        tvNaZi.setText(formatNaZi(nz));

        LingGuiBaFa.NaJiaPoint[] nj = LingGuiBaFa.calcNaJia(daySB, hourSB);
        tvNaJia.setText(formatNaJia(nj));

        if (solar != null) {
            tvSolar.setVisibility(View.VISIBLE);
            tvSolar.setText(String.format("真太阳时 %02d:%02d  经度%+.0f分 + 均时差%+.0f分 = %+.0f分",
                    solar.hour, Math.round(solar.minute),
                    solar.lonCorrection, solar.eot, solar.totalDiff));
        } else {
            tvSolar.setVisibility(View.GONE);
        }
    }

    private String formatPair(String[] pair) {
        String[] m = LingGuiBaFa.getPointMeta(pair[0]);
        String s = pair[0] + "（" + m[0] + "·通" + m[1] + "）";
        if (pair[1] != null && !pair[1].isEmpty()) {
            String[] p = LingGuiBaFa.getPointMeta(pair[1]);
            s += "\n配穴：" + pair[1] + "（" + p[0] + "·通" + p[1] + "）";
        }
        return s;
    }

    private String formatNaZi(String[] row) {
        String s = row[0] + "当令\n本穴：" + row[1] + "·" + row[2];
        if (!row[1].equals(row[3]))
            s += "\n原穴：" + row[3] + "·" + row[4];
        return s;
    }

    private String formatNaJia(LingGuiBaFa.NaJiaPoint[] points) {
        if (points == null) return "闭穴";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < points.length; i++) {
            if (i > 0) sb.append("\n");
            sb.append(points[i].type).append("穴：").append(points[i].name)
                    .append("（").append(points[i].meridian).append("·").append(points[i].code).append("）");
        }
        return sb.toString();
    }
}
