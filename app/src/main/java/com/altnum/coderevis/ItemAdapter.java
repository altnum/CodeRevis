package com.altnum.coderevis;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ItemAdapter extends BaseAdapter {

    LayoutInflater mInflater;
    String[] kinds;
    boolean deletePressed;

    public ItemAdapter(Context c, String[] kinds, boolean deletePressed) {
        this.kinds = kinds;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.deletePressed = deletePressed;
    }

    @Override
    public int getCount() {
        return kinds.length;
    }

    @Override
    public Object getItem(int position) {
        return kinds[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.my_kindslistview_detail, null);
        TextView kindTextView = (TextView) v.findViewById(R.id.kindNameTextView);

        if (deletePressed)
            kindTextView.setTextColor(Color.RED);

        String name = kinds[position];

        kindTextView.setText(name);

        return v;
    }
}
