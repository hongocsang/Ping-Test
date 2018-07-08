package com.example.hnsang.pingtest.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.widget.TextView;

import com.example.hnsang.pingtest.Database.ConnectionDB;
import com.example.hnsang.pingtest.Object.Constant;
import com.example.hnsang.pingtest.Object.MainDataObject;
import com.example.hnsang.pingtest.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GraphPingActivity extends AppCompatActivity {

    private PieChart mPCTotal;
    private LineChart mLCPing;
    private LineChart mLCPacket;

    private TextView mTVUserName;

    private ArrayList<PieEntry> mTotalList = new ArrayList<>();
    private ArrayList<Entry> mPingList = new ArrayList<>();
    private ArrayList<Entry> mSuccessList = new ArrayList<>();
    private ArrayList<Entry> mUnSuccessList = new ArrayList<>();

    private int mPing = 0;
    private int mTotal = 0;
    private int mSuccess = 0;
    private int mUnSuccess = 0;
    private int mCountData = 0;

    private String mStrIdDevice;
    private String mStrUserName;
    private String mStrTimeStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_ping);

        mAddControl();

        mLoadData();

        reload();
    }

    private void mAddControl() {

        mPCTotal = findViewById(R.id.pc_total);
        mLCPing = findViewById(R.id.lc_ping);
        mLCPacket = findViewById(R.id.lc_packet);
        mTVUserName = findViewById(R.id.tv_username);


        Intent intent = getIntent();
        mStrIdDevice = intent.getStringExtra("idDevice");
        mStrUserName = intent.getStringExtra("UserName");
        mStrTimeStart = intent.getStringExtra("TimeStart");
        mTVUserName.setText(mStrUserName);
    }

    private void mLoadData() {
        mTotalList.clear();
        mPingList.clear();
        mSuccessList.clear();
        mUnSuccessList.clear();

        ConnectionDB mConnectionDB = new ConnectionDB();
        Connection mConnection = mConnectionDB.CONN();

        try {
            if (mConnection == null) {
                Log.i("kanna", "Error in connection with SQL server");
            } else {
                String query = "select * " +
                        "from data " +
                        "where iddevice='" + mStrIdDevice + "' and username ='" + mStrUserName + "' and timestart='" + mStrTimeStart + "'";

                Statement stmt = mConnection.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                int count = 1;
                while (rs.next()) {
                    String strPing = rs.getString("ping");
                    mPing = (mPing + Integer.parseInt(strPing)) / 2;

                    float mPingLine = Integer.parseInt(strPing);

                    String strTotal = rs.getString("total");
                    if (count != 1){
                        mTotal = (mTotal + Integer.parseInt(strTotal)) / 2;
                    }else {
                        mTotal = (mTotal + Integer.parseInt(strTotal));
                    }

                    String strSuccess = rs.getString("success");
                    mSuccess = (mSuccess + Integer.parseInt(strSuccess)) / 2;

                    float mTotalData = Integer.parseInt(strTotal);
                    float mSuccessData = Integer.parseInt(strSuccess);
                    float mUnSuccessData = mTotalData - mSuccessData;

                    mUnSuccess = mTotal - mSuccess;
                    Log.i("kanna1", strPing);
                    Log.i("kanna2", strTotal);
                    Log.i("kanna3", strSuccess);

                    mCountData++;
                    count++;

                    mPingList.add(new Entry(mCountData, mPingLine));
                    mSuccessList.add(new Entry(mCountData, mSuccessData));
                    mUnSuccessList.add(new Entry(mCountData, mUnSuccessData));
                }
                mLineChartPing();
                mLineChartPacket();
                handlingGraph(mSuccess, mUnSuccess, mPing);
            }
        } catch (Exception ex) {
            Log.i("kanna", "Exceptions");
        }
    }

    private SpannableString generateCenterSpannableText(int value3) {
        SpannableString s = new SpannableString("ping: " + value3);
        s.setSpan(new RelativeSizeSpan(1f), 0, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.colorPrimary)), 0, s.length(), 0);

        return s;
    }

    private void handlingGraph(float value1, float value2, int value3) {
        if (value1 != 0) {
            mTotalList.add(new PieEntry(value1, "Thành công"));
        }
        if (value2 != 0) {
            mTotalList.add(new PieEntry(value2, "Thất bại"));
        }
        mPieChart(mPCTotal, mTotalList, value3);
    }

    private void mPieChart(PieChart pieChart, ArrayList arrayList, int value3) {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.99f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(50f);
        pieChart.setCenterText(generateCenterSpannableText(value3));
        pieChart.setDrawCenterText(true);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(2f);
        l.setYOffset(2f);

        pieChart.setEntryLabelColor(this.getResources().getColor(R.color.colorPrimary));
        pieChart.setEntryLabelTextSize(12f);
        PieDataSet dataSet = new PieDataSet(arrayList, "Ping");
        dataSet.setDrawIcons(false);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(6f);

        ArrayList<Integer> colors = new ArrayList<>();

        colors.add(this.getResources().getColor(R.color.success));
        colors.add(this.getResources().getColor(R.color.unsuccess));

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextColor(this.getResources().getColor(R.color.colorPrimary));
        pieChart.setData(data);
        pieChart.animateX(1000);
        pieChart.invalidate();
    }

    private void reload() {
        final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                mLoadData();
                Log.i("kannaok", "ok");
            }
        }, 0, 5, TimeUnit.MINUTES);
    }

    private void mLineChartPing() {
        mLCPing.setDragEnabled(true);
        mLCPing.setSaveEnabled(false);

        Description description = new Description();
        description.setTextColor(Color.BLACK);
        description.setText("Data Ping");
        description.setTextSize(10f);
        mLCPing.setDescription(description);

        LimitLine mLimitLine = new LimitLine(mPing, "Average");
        mLimitLine.setLineWidth(3f);
        mLimitLine.enableDashedLine(10f, 10f, 10f);
        mLimitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        mLimitLine.setTextSize(10f);

        YAxis mYAxis = mLCPing.getAxisLeft();
        mYAxis.removeAllLimitLines();
        mYAxis.addLimitLine(mLimitLine);
        mYAxis.enableGridDashedLine(10f, 10f, 10f);
        mYAxis.setDrawLimitLinesBehindData(true);

        LineDataSet mSetDataPing = new LineDataSet(mPingList, "Ping");
        mSetDataPing.setFillAlpha(110);
        mSetDataPing.setLineWidth(5f);
        mSetDataPing.setValueTextSize(10f);
        mSetDataPing.setColor(this.getResources().getColor(R.color.blue));
        mSetDataPing.setValueTextColor(Color.RED);

        ArrayList<ILineDataSet> mDataSets = new ArrayList<>();
        mDataSets.add(mSetDataPing);

        LineData mLineData = new LineData(mDataSets);
        mLCPing.setData(mLineData);
    }

    private void mLineChartPacket() {
        mLCPacket.setDragEnabled(true);
        mLCPacket.setSaveEnabled(false);

        Description description = new Description();
        description.setTextColor(Color.BLACK);
        description.setText("Data Packet");
        description.setTextSize(10f);
        mLCPacket.setDescription(description);

        LimitLine mLimitLine = new LimitLine(mTotal, "Total");
        mLimitLine.setLineWidth(3f);
        mLimitLine.enableDashedLine(10f, 10f, 10f);
        mLimitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        mLimitLine.setTextSize(10f);
        mLimitLine.setLineColor(this.getResources().getColor(R.color.blue));

        YAxis mYAxis = mLCPacket.getAxisLeft();
        mYAxis.removeAllLimitLines();
        mYAxis.addLimitLine(mLimitLine);
        mYAxis.enableGridDashedLine(10f, 10f, 10f);
        mYAxis.setDrawLimitLinesBehindData(true);

        LineDataSet mSetDataSuccess = new LineDataSet(mSuccessList, "Success");
        mSetDataSuccess.setFillAlpha(110);
        mSetDataSuccess.setLineWidth(5f);
        mSetDataSuccess.setValueTextSize(10f);
        mSetDataSuccess.setColor(this.getResources().getColor(R.color.success));
        mSetDataSuccess.setValueTextColor(Color.GREEN);

        LineDataSet mSetDataUnSuccess = new LineDataSet(mUnSuccessList, "UnSuccess");
        mSetDataUnSuccess.setFillAlpha(110);
        mSetDataUnSuccess.setLineWidth(5f);
        mSetDataUnSuccess.setValueTextSize(10f);
        mSetDataUnSuccess.setColor(this.getResources().getColor(R.color.unsuccess));
        mSetDataUnSuccess.setValueTextColor(Color.RED);

        ArrayList<ILineDataSet> mDataSets = new ArrayList<>();
        mDataSets.add(mSetDataSuccess);
        mDataSets.add(mSetDataUnSuccess);

        LineData mLineData = new LineData(mDataSets);

        mLCPacket.setData(mLineData);
    }
}
