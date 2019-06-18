package com.example.t2m.moneytracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.t2m.moneytracker.R;
import com.example.t2m.moneytracker.model.DateRange;
import com.example.t2m.moneytracker.utilities.DateUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TimeRangeListAdapter  extends ArrayAdapter<String> {
    Context context;
    int resource;
    public TimeRangeListAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String period = getItem(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null) {
            convertView = inflater.inflate(resource,null);
        }

        TextView big_time = convertView.findViewById(R.id.txtBigTitle);
        TextView small_time = convertView.findViewById(R.id.txtSmallTitle);

        big_time.setText(period);
        small_time.setText(getTextTime(period));
        return convertView;
    }

    String getTextTime(String time) {
        DateRange dateRange;
        DateUtils dateUtils = new DateUtils();

        dateRange = dateUtils.getDateRangeForPeriod(context,time);

        return dateRange.toString();
    }
}
