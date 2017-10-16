package com.example.zjt.myfilepicker;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by zjt on 2017/9/12.
 */

public class MySimpleAdapter extends BaseAdapter {
    private Activity context;
    private ArrayList<File> allFileList;
    private ArrayList<String> typesList;

    public MySimpleAdapter(Activity context, ArrayList<File> allFileList,ArrayList<String> typesList) {
        this.context = context;
        this.allFileList = allFileList;
        this.typesList = typesList;
    }

    @Override
    public int getCount() {
        return allFileList.size();
    }

    @Override
    public Object getItem(int i) {
        return allFileList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.filerow, parent, false);
        ImageView check = convertView.findViewById(R.id.check);
        ImageView imageView = convertView.findViewById(R.id.fp_iv_icon);
        TextView name = convertView.findViewById(R.id.fp_tv_name);

        if( typesList.get(position).equals("folder") )
        {
            imageView.setImageResource( R.drawable.folder );
        }
        else
        {
            imageView.setImageResource( R.drawable.file );
            if(typesList.get(position).equals("shared")){
                check.setVisibility(View.VISIBLE);
            }
        }
        name.setText(allFileList.get(position).getName());
        return convertView;
    }

}