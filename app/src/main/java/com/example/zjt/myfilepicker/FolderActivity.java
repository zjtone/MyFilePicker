package com.example.zjt.myfilepicker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by zjt on 2017/9/12.
 */

public class FolderActivity extends AppCompatActivity {
    private MySimpleAdapter sa;
    private ArrayList<File> allFileList;
    private ArrayList<String> typesList;
    private ArrayList<String> selectList;

    private ArrayList<File> filesList;
    private ArrayList<File> folderList;

    private TextView tv_title;
    private TextView tv_location;
    private ListView lv;

    private String location = Environment.getExternalStorageDirectory().getAbsolutePath();
    private Intent receivedIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fp_layout);

        if( !isExternalStorageReadable() ){
            Toast.makeText(this, "缺少权限，无法运行", Toast.LENGTH_LONG).show();
            finish();
        }
        tv_title = findViewById(R.id.fp_tv_title);
        tv_location = findViewById(R.id.fp_tv_location);
        lv = findViewById(R.id.fp_listView);
        receivedIntent = getIntent();
        allFileList = new ArrayList<>();
        filesList = new ArrayList<>();
        folderList = new ArrayList<>();
        loadLists(location);
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public void loadLists(String location) {
        try {
            File folder = new File(location);
            if(selectList == null)selectList = new ArrayList<>();
            if(!folder.exists() ||  !folder.isDirectory() )
                return;
            this.clear();
            tv_location.setText("Location : "+ folder.getAbsolutePath());
            File[] files = folder.listFiles();
            Log.d("tag","loadLists: "+folder.isDirectory());
            for ( File currentFile : files ) {
                if(currentFile.isHidden())continue;
                if(currentFile.isFile())
                    filesList.add(currentFile);
                else
                    folderList.add(currentFile);//  文件夹
            }
            Log.d("tag","" + files.length);
            // sort & add to final List
            Collections.sort(folderList);
            allFileList.addAll(folderList);
            Collections.sort(filesList);
            allFileList.addAll(filesList);
            // add types
            if(typesList == null)typesList = new ArrayList<>();
            for (int i = 0; i < allFileList.size(); i++) {
                if(allFileList.get(i).isDirectory())
                    typesList.add("folder");
                else
                    typesList.add("file");
            }
            showList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sa.notifyDataSetChanged();
    } // load List

    public void clear() {
        if(filesList!=null)filesList.clear();
        if(folderList!=null)folderList.clear();
        if(allFileList!=null)allFileList.clear();
        if(selectList!=null)selectList.clear();
        if(typesList!=null)typesList.clear();
    }

    public void showList() {

        try {
           if(sa==null) sa = new MySimpleAdapter(this, allFileList, typesList);
            lv.setAdapter(sa);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    listClick(i);
                }
            });

            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    listLongClick(i);
                    sa.notifyDataSetChanged();
                    return true;// 不再触发onItemClickListener事件
//                    return false;// 依旧触发onItemClickListener事件，且先触发long click
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void listClick(int position) {
        File file = allFileList.get(position);
        if(file.isDirectory()) {
            location = location + File.separator + file.getName();
            loadLists(location);
        }
    }

    public void listLongClick(int position){
        File file = allFileList.get(position);
        Log.d("tag","listLongClick: "+file);
        boolean contains = false;
        for(String p:selectList){
            if(p.equalsIgnoreCase(file.getAbsolutePath()))contains = true;
        }
        if(contains){
            Log.d("tag","listLongClick: "+selectList.contains(file.getAbsolutePath())+ ", " +selectList.size());
            selectList.remove(file.getAbsolutePath());
            typesList.set(position,"file");
        } else if(file.isFile()){
            Log.d("tag","listLongClick: "+file.isFile());
            selectList.add(file.getAbsolutePath());
            typesList.set(position,"shared");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            goBack(null);
        }
        return true;// 不再进行事件分发
//        return false;// 依旧有事件分发
    }

    public void goBack(View v) {
        if( location!=null && !location.equals("") && !location.equals("/") ) {
            int start = location.lastIndexOf('/');
            if(location.substring(0, start).equalsIgnoreCase("/storage/emulated"))return;
            location = location.substring(0, start);
            loadLists(location);
        }
    }

    public void newFolder(String filename) {
        try {
            File file = new File(location + File.separator + filename);
            file.mkdirs();
            loadLists(location);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error:" + e.toString(), Toast.LENGTH_LONG)
                    .show();
        }

    }

    public void newFolderDialog(View v) {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("Enter Folder Name");

        final EditText et = new EditText(this);
        dialog.setView(et);

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Create",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        newFolder(et.getText().toString());
                    }
                });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        dialog.show();

    }


    public void select(View v) {
        if (receivedIntent != null) {
            receivedIntent.putExtra("file",selectList);
            setResult(RESULT_OK, receivedIntent);
            finish();
        }
    }

    public void cancel(View v) {
        finish();
    }
}
