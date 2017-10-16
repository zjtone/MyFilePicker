package com.example.zjt.myfilepicker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView filelist;
    private Button filepicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
    }

    private void initListener() {
        filepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,FolderActivity.class);
                startActivityForResult(intent,1);
            }
        });
    }
    private void initView() {
        filelist = findViewById(R.id.filelist);
        filepicker = findViewById(R.id.filepicker);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<String> arrayList = null;
        if(data != null && data.getSerializableExtra("file") instanceof ArrayList)
            arrayList = (ArrayList<String>) data.getSerializableExtra("file");
        else {
            Log.d("tag", "no arraylist");
            return ;
        }
        Log.d("tag",""+arrayList);
        StringBuilder content = new StringBuilder();
        for(String s:arrayList){
            content.append(s).append("\n");
        }
        filelist.setText(content);
    }
}
