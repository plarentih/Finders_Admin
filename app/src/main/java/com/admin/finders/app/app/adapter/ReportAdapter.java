package com.admin.finders.app.app.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.admin.finders.R;
import com.admin.finders.app.app.model.Report;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ema on 1/19/2018.
 */

public class ReportAdapter extends ArrayAdapter<Report> {

    private ArrayList<Report> newEventList = new ArrayList<>();

    private static class ViewHolder{
        ImageView imgView;
        TextView titleTxv;
        TextView descTxv;
    }

    public ReportAdapter(Context context, List<Report> eventList) {
        super(context, 0, eventList);
        newEventList.addAll(eventList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Report report = newEventList.get(position);
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.report_row, parent, false);
            viewHolder.imgView = convertView.findViewById(R.id.img);
            viewHolder.titleTxv = convertView.findViewById(R.id.titleReport);
            viewHolder.descTxv = convertView.findViewById(R.id.description);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.titleTxv.setText(report.getTitle());
        viewHolder.descTxv.setText(report.getDescription());
        Glide.with(getContext()).load(report.getUrlPhoto()).into(viewHolder.imgView);
        return convertView;
    }


}
