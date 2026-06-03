package com.example.lingguibafa;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button btnCalculate;
    private TextView tvDayStemBranch, tvHourStemBranch, tvAcupoint, tvProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        btnCalculate = findViewById(R.id.btnCalculate);
        tvDayStemBranch = findViewById(R.id.tvDayStemBranch);
        tvHourStemBranch = findViewById(R.id.tvHourStemBranch);
        tvAcupoint = findViewById(R.id.tvAcupoint);
        tvProcess = findViewById(R.id.tvProcess);

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });
    }

    private void calculate() {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        Date selectedDate = calendar.getTime();

        String daySb = ChineseCalendar.getDayStemBranch(selectedDate);
        String hourSb = ChineseCalendar.getHourStemBranch(daySb.substring(0, 1), hour);

        tvDayStemBranch.setText("日干支：" + daySb);
        tvHourStemBranch.setText("时干支：" + hourSb);

        Map<String, Object> result = LingGuiBaFa.calculateAcupoint(daySb, hourSb);

        tvAcupoint.setText("灵龟八法：应开穴位 - " + result.get("穴位"));

        String processText =
            "计算过程：\n" +
            "日干（" + daySb.substring(0, 1) + "）：" + result.get("日干数") + "\n" +
            "日支（" + daySb.substring(1, 2) + "）：" + result.get("日支数") + "\n" +
            "时干（" + hourSb.substring(0, 1) + "）：" + result.get("时干数") + "\n" +
            "时支（" + hourSb.substring(1, 2) + "）：" + result.get("时支数") + "\n" +
            "总和：" + result.get("总和") + "\n" +
            result.get("阴阳") + "日，除以" + result.get("除数") + "，余数为" + result.get("余数") + "\n" +
            "对应穴位：" + result.get("穴位");

        tvProcess.setText(processText);
    }
}