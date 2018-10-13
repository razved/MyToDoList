package com.example.android.mytodolist.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.android.mytodolist.R;
import com.example.android.mytodolist.Utils;

import java.util.List;

public class TypeColorAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final LayoutInflater inflater;
    private final List<Integer> colors;

    private final static int LAYOUT_FILE = R.layout.spinner_item_layout;
    public static final int TEXT_VIEW_ID = R.id.textview_spinner_color;


    private static final String LOG_TAG = "TypeColorAdapter";

    public TypeColorAdapter(Context c, List objects) {
        super(c, LAYOUT_FILE, TEXT_VIEW_ID, objects);
        context = c;
        inflater = LayoutInflater.from(context);
        colors = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        final View view = inflater.inflate(LAYOUT_FILE, parent, false);

        TextView colorTextView = (TextView) view.findViewById(R.id.textview_spinner_color);

        Integer color = colors.get(position);
        view.setBackgroundColor(Utils.getColorById(context, color));
        //colorTextView.setText(String.valueOf(color));
        Log.i(LOG_TAG, "Colors - position: " + position + " color: " + Utils.getColorById(context, color));

        return view;
    }
}
