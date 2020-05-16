package com.smartconnect.locationsockets.Adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.smartconnect.locationsockets.R;

import java.util.ArrayList;

public class MyListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> maintitle;
    private final ArrayList<String> subtitle;
    private final Integer[] imgid;

    public MyListAdapter(Activity context, ArrayList<String> maintitle, ArrayList<String> subtitle, Integer[] imgid) {
        super(context, R.layout.activity_listview, maintitle);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.maintitle = maintitle;
        this.subtitle = subtitle;
        this.imgid = imgid;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.activity_listview, null, true);

        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.subtitle);
        Log.d("VALUE  ", +position + " " + maintitle.get(position).toString());
        titleText.setText(maintitle.get(position).toString());
        imageView.setImageResource(imgid[position]);
        subtitleText.setText(subtitle.get(position).toString());


        return rowView;

    }

    ;
}