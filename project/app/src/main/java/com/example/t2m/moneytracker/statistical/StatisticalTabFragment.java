package com.example.t2m.moneytracker.statistical;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.t2m.moneytracker.R;
import com.example.t2m.moneytracker.dataaccess.CategoriesDAOImpl;
import com.example.t2m.moneytracker.dataaccess.ITransactionsDAO;
import com.example.t2m.moneytracker.dataaccess.IWalletsDAO;
import com.example.t2m.moneytracker.dataaccess.TransactionsDAOImpl;
import com.example.t2m.moneytracker.model.Category;
import com.example.t2m.moneytracker.model.MTDate;
import com.example.t2m.moneytracker.model.Transaction;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StatisticalTabFragment extends Fragment implements OnChartValueSelectedListener {
  private PieChart mChart;
  private CombinedChart comChart;
  private EditText mTextFromDate;
  private EditText mTextToDate;
  private Calendar mCalendar;
  private Spinner dropdown;
  private Spinner dropdown2;
  private BarChart barChart;
  private RelativeLayout relativeLayoutBarChart;
  private RelativeLayout relativeLayoutMchart;
  ITransactionsDAO iTransactionsDAO;
  static TransactionsDAOImpl transactionsDAOImpl;
  static CategoriesDAOImpl categoriesDAOImpl;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.activity_statistical_tab,container,false);
    transactionsDAOImpl = new TransactionsDAOImpl(getContext());
    categoriesDAOImpl = new CategoriesDAOImpl(getContext());
    this.addControls(view);
    this.methodDate();
    this.methodSpinner();
    this.methodSpinner2();
    return view;
  }

  private void addControls(View view) {
    mTextFromDate = view.findViewById(R.id.text_from_date);
    mTextToDate = view.findViewById(R.id.text_to_date);
    dropdown = view.findViewById(R.id.spinner1);
    dropdown2 = view.findViewById(R.id.spinner2);
    mChart = view.findViewById(R.id.piechart);
    mCalendar = Calendar.getInstance();
    comChart =  view.findViewById(R.id.combinedChart);
    barChart = view.findViewById(R.id.bar_chart);
    relativeLayoutBarChart = view.findViewById(R.id.bar_chart_relative_layout);
    relativeLayoutMchart = view.findViewById(R.id.piechart_relative_layout);

  }
  /* Xử lý thời gian - start*/
  public void methodDate () {
    mTextFromDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onClickedDate(v);
      }
    });
    mTextToDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onClickedDate(v);
      }
    });
  }
  private void onClickedDate(View v) {
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
      @Override
      public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mCalendar.set(year,month,dayOfMonth);
        updateLabelDate();
      }
    };
    int year = mCalendar.get(Calendar.YEAR);
    int month = mCalendar.get(Calendar.MONTH);
    int date = mCalendar.get(Calendar.DATE);
    new DatePickerDialog(getContext(),dateSetListener,year,month,date).show();
  }
  private void updateLabelDate() {
    Date date = mCalendar.getTime();
    if(DateUtils.isToday(date.getTime())) {
      mTextFromDate.setText("Từ ngày");
      mTextToDate.setText("Đến ngày");
    }
    else {
      String strDate = new MTDate(mCalendar).toIsoDateShortTimeString();
      mTextFromDate.setText(strDate);
      mTextToDate.setText(strDate);
    }
  }
  /* Xử lý thời gian - end*/

  /* Xử lý dropdown - start*/
  public void methodSpinner () {
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
      R.array.planets_array, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    dropdown.setAdapter(adapter);
  }
  public void methodSpinner2 () {
    ArrayAdapter<java.lang.CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),
      R.array.planets_array2, android.R.layout.simple_spinner_item);
    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    dropdown2.setAdapter(adapter2);


    dropdown2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
         long item = parent.getItemIdAtPosition(position);
         if(item > 0) {
           methodMChart();
           mChart.setVisibility(view.VISIBLE);
           relativeLayoutMchart.setVisibility(view.VISIBLE);
           barChart.setVisibility(view.INVISIBLE);
           relativeLayoutBarChart.setVisibility(view.INVISIBLE);
         } else if (item == 0) {
           methodBarchart();
           mChart.setVisibility(view.INVISIBLE);
           relativeLayoutMchart.setVisibility(view.INVISIBLE);
           barChart.setVisibility(view.VISIBLE);
           relativeLayoutBarChart.setVisibility(view.VISIBLE);
         }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });
  }

  /* Xử lý dropdown - end*/

  @Override
  public void onValueSelected(Entry e, Highlight h) {
//    int dur = Toast.LENGTH_SHORT;
//    Toast.makeText(, "Value: " + e.getY() + ", index: " + h.getX() + ", DataSet index: " + h.getDataSetIndex(),dur).show();
//    Toast.
  }

  /* Xử lý biểu đồ tròn - start*/
  public void methodMChart(){
    mChart.setRotationEnabled(true);
    mChart.setDescription(new Description());
    mChart.setHoleRadius(35f);
    mChart.setTransparentCircleAlpha(0);
    mChart.setCenterText("PieChart");
    mChart.setCenterTextSize(10);
    mChart.setDrawEntryLabels(true);

    addDataSet(mChart);

    mChart.setOnChartValueSelectedListener(this);
  }

  private static void addDataSet(PieChart pieChart) {
    ArrayList<PieEntry> yEntrys = new ArrayList<>();
    ArrayList<String> xEntrys = new ArrayList<>();
    List<Category> listCat = categoriesDAOImpl.getAllCategory();
    if(listCat != null && listCat.size() > 0) {
      actionCalucator(listCat);
    }

    float[] yData = { 25, 40, 35};
    String[] xData = { "January", "February", "January" };

    for (int i = 0; i < yData.length;i++){
      yEntrys.add(new PieEntry(yData[i],i));
    }
    for (int i = 0; i < xData.length;i++){
      xEntrys.add(xData[i]);
    }

    PieDataSet pieDataSet=new PieDataSet(yEntrys,"Report");
    pieDataSet.setSliceSpace(2);
    pieDataSet.setValueTextSize(12);

    ArrayList<Integer> colors=new ArrayList<>();
    colors.add(Color.GRAY);
    colors.add(Color.GREEN);
    colors.add(Color.RED);

    pieDataSet.setColors(colors);

    Legend legend=pieChart.getLegend();
    legend.setForm(Legend.LegendForm.CIRCLE);
    legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);

    PieData pieData=new PieData(pieDataSet);
    pieChart.setData(pieData);
    pieChart.invalidate();
  }
  /* Xử lý biểu đồ tròn - end*/

  @Override
  public void onNothingSelected() {

  }

  public static void initSum() {
    float SUM_EAT = 0; // tiền dành cho ăn uống
    float SUM_BILL_UTILITIES = 0; // tiền dành cho hóa đơn và tiện ích
    float SUM_GO = 0; // tiền dành cho việc di chuyển
    float SUM_SHOPPING = 0; //tiền dành cho việc mua sắm
    float SUM_FRIEND_LOVELY = 0; // baạn bè và người yêu
    float SUM_RELAX = 0; // giải trí
    float SUM_TRAVEL = 0; //Du lịch
    float SUM_HEALTHY = 0; // Sức khỏe
    float SUM_GIFTS = 0; //QUà tặng
    float SUM_FAMILY = 0; // gia đình
    float SUM_EDUCATION  = 0; //Giáo dục
  }
  public static  void actionCalucator(List<Category> listCat) {
    initSum();
    for (Category cat : listCat ) {
      // Lấy ra danh sách giao dịch theo loại
      List<Transaction> listTran = transactionsDAOImpl.getStatisticalByCategory(cat.getId(), cat.getType().getValue());
      if(listTran != null && listTran.size() > 0) {

        for( Transaction tran: listTran ) {
          if(tran.getCategory().getType().getValue()== 1) {

          } else if (tran.getCategory().getType().getValue() == 2) {

          } else if (tran.getCategory().getType().getValue() == 3) {

          } else if (tran.getCategory().getType().getValue() == 4) {

          }
        }
      }
    }
  }


  /* Vẽ biểu đồ cột - start */
  private void methodBarchart() {
    barChart.setDrawBarShadow(false);
    barChart.setDrawValueAboveBar(true);
    Description description = new Description();
    description.setText("");
    barChart.setDescription(description);
    barChart.setMaxVisibleValueCount(50);
    barChart.setPinchZoom(false);
    barChart.setDrawGridBackground(false);

    XAxis xl = barChart.getXAxis();
    xl.setGranularity(1f);
    xl.setCenterAxisLabels(true);

    YAxis leftAxis = barChart.getAxisLeft();
    leftAxis.setDrawGridLines(false);
    leftAxis.setSpaceTop(30f);
    barChart.getAxisRight().setEnabled(false);

    //data
    float groupSpace = 0.04f;
    float barSpace = 0.02f;
    float barWidth = 0.46f;

    int startYear = 1980;
    int endYear = 1985;

    List<BarEntry> yVals1 = new ArrayList<BarEntry>();
    List<BarEntry> yVals2 = new ArrayList<BarEntry>();

    for (int i = startYear; i < endYear; i++) {
      yVals1.add(new BarEntry(i, 0.4f));
    }

    for (int i = startYear; i < endYear; i++) {
      yVals2.add(new BarEntry(i, 0.7f));
    }

    BarDataSet set1, set2;

    if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
      set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
      set2 = (BarDataSet) barChart.getData().getDataSetByIndex(1);
      set1.setValues(yVals1);
      set2.setValues(yVals2);
      barChart.getData().notifyDataChanged();
      barChart.notifyDataSetChanged();
    } else {
      set1 = new BarDataSet(yVals1, "Company A");
      set1.setColor(Color.rgb(104, 241, 175));
      set2 = new BarDataSet(yVals2, "Company B");
      set2.setColor(Color.rgb(164, 228, 251));

      ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
      dataSets.add(set1);
      dataSets.add(set2);

      BarData data = new BarData(dataSets);
      barChart.setData(data);
    }

    barChart.getBarData().setBarWidth(barWidth);
    barChart.groupBars(startYear, groupSpace, barSpace);
    barChart.invalidate();

  }
  /* Vẽ biểu đồ cột - end */


}
