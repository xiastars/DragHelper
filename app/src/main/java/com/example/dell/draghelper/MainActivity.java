package com.example.dell.draghelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    DragLayer dragLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dragLayer = (DragLayer) findViewById(R.id.dl_layer);
        dragLayer.addBackgroundView();
        //dragLayer.setBackgroundResource(R.drawable.background1);
        creageBig();
        for(int i = 0;i < 10;i++){
            createLittle();
        }
    }

    private void createLittle() {
        DragView controlLayout = new DragView(this);
        controlLayout.setLayoutPosition(500,300,100,100);
        controlLayout.setDefalultIcon(R.drawable.so_bluela_oval);
        dragLayer.addView(controlLayout);
        controlLayout.setOnSingleClickListener(new DragView.SingleClickListener() {
            @Override
            public void onSClick() {
                makeToast("single click");
            }
        });
        controlLayout.setOnDoubleClickListener(new DragView.DoubleClickListener() {
            @Override
            public void onDClick() {
                makeToast("double click");
            }
        });
        controlLayout.setOnLongClickListener(new DragView.LongClickListener() {
            @Override
            public void longClick() {
                makeToast("long click");
            }
        });
    }

    private void makeToast(String action){
        Toast.makeText(this,action,Toast.LENGTH_SHORT).show();
    }

    private void creageBig(){
        DragView controlLayout = new DragView(this);
        controlLayout.setLayoutPosition(200,300,300,300);
        controlLayout.setDefalultIcon(R.mipmap.default_icon_triangle);
        dragLayer.addView(controlLayout);
    }
}
