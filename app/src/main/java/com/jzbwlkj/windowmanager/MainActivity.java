package com.jzbwlkj.windowmanager;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jzbwlkj.windowmanager.service.WindowService;

/**
 * 悬浮窗口
 */
public class MainActivity extends AppCompatActivity {
    public static Context context;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent(this, WindowService.class);
        context = MainActivity.this;
    }

    /**
     * 点击事件
     *
     * @param view
     */
    public void click(View view) {
        switch (view.getId()) {
            case R.id.bt_main_start://开启
                startService(intent);
                break;
            case R.id.bt_main_end://关闭
                stopService(intent);
                break;
        }
    }
}
