package com.example.page2x_200514_mj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;

public class Page2_X_CategoryBottom_Adapter extends BaseAdapter {

    private ArrayList<Page2_X_CategoryBottom.Category_item> list;
    private Context context;
    private LayoutInflater inflate;
    private ViewHolder viewHolder;
    private View.OnClickListener mONnClickListener = null;


    public Page2_X_CategoryBottom_Adapter (ArrayList<Page2_X_CategoryBottom.Category_item> list, Context context, View.OnClickListener onClickListener){
        this.context = context;
        this.list = list;
        this.inflate = LayoutInflater.from(context);
        this.mONnClickListener = onClickListener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflate.inflate(R.layout.page2_x_category_bottomsheet_item, null );

            viewHolder = new ViewHolder();
            viewHolder.category_text = convertView.findViewById(R.id.category_type_text);
            viewHolder.category_text.setText(list.get(position).getName());

            convertView.setTag(position);
            ;
        } else {
            viewHolder=(ViewHolder)convertView.getTag();
        }

        //아이템을 누르면
        if(mONnClickListener != null){
            viewHolder.category_text.setTag(position);
            viewHolder.category_text.setOnClickListener(mONnClickListener);
        }

        return convertView;
    }


    class ViewHolder{
        public Button category_text;
    }

}