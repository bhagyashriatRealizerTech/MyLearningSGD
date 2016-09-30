package com.realizer.schoolgeine.driver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.realizer.schoolgeine.driver.R;
import com.realizer.schoolgeine.driver.model.TrackModel;

import java.util.ArrayList;



/**
 * Created by Win on 11/20/2015.
 */
public class TrackTableListAdapter extends BaseAdapter {


    private static ArrayList<TrackModel> track;
    private LayoutInflater publicholidayDetails;
    private Context context1;
    View convrtview;


    public TrackTableListAdapter(Context context, ArrayList<TrackModel> dicatationlist) {
        track = dicatationlist;
        publicholidayDetails = LayoutInflater.from(context);
        context1 = context;
    }

    @Override
    public int getCount() {
        return track.size();
    }

    @Override
    public Object getItem(int position) {

        return track.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        convrtview = convertView;
        if (convertView == null) {
            convertView = publicholidayDetails.inflate(R.layout.track_list_layout, null);
            holder = new ViewHolder();
            holder.locTime = (TextView) convertView.findViewById(R.id.txttime);
            holder.lat = (TextView) convertView.findViewById(R.id.txtlat);
            holder.lang = (TextView) convertView.findViewById(R.id.txtlang);
            holder.upload = (TextView)convertView.findViewById(R.id.txthasupload);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.locTime.setText(track.get(position).getLocationTime());
        holder.lat.setText(track.get(position).getLati());
        holder.lang.setText(track.get(position).getLangi());
        holder.upload.setText(track.get(position).getHasBroadcast());

        return convertView;
    }

    static class ViewHolder {

        TextView locTime,lat,lang,upload;

    }
}
