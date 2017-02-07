package com.jzbwlkj.windowmanager.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.jzbwlkj.windowmanager.MainActivity;
import com.jzbwlkj.windowmanager.R;

import java.util.List;

public class WindowService extends Service {
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private View mWindowView;
    private ImageView mImgView;

    private int mStartX;
    private int mStartY;
    private int mEndX;
    private int mEndY;


    @Override
    public void onCreate() {
        super.onCreate();
        initView();
        setWindowParams();
        addView();
        initClick();

    }

    /**
     * WindowManager添加View
     */
    private void addView() {
        mWindowManager.addView(mWindowView, mParams);
    }

    /**
     * 设置LayoutParams
     */
    private void setWindowParams() {
        //TYPE_APPLICATION 本应用内
        //TYPE_PHONE 整个手机
        mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mParams.gravity = Gravity.RIGHT | Gravity.TOP;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    /**
     * 初始化View
     */
    private void initView() {
        //注意使用TYPE_PHONE时，要用MainActivity.this
        mWindowManager = (WindowManager) MainActivity.context.getSystemService(this.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();
        mWindowView = LayoutInflater.from(MainActivity.context).inflate(R.layout.view_window, null);
        mImgView = (ImageView) mWindowView.findViewById(R.id.img_window);
    }

    public WindowService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWindowManager != null) {
            //移除悬浮窗口
            mWindowManager.removeView(mWindowView);
        }
    }

    private void initClick() {
        mImgView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mStartX = (int) event.getRawX();
                        mStartY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mEndX = (int) event.getRawX();
                        mEndY = (int) event.getRawY();
                        if (needIntercept()) {
                            //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                            mParams.x = (int) event.getRawX() - mWindowView.getMeasuredWidth() / 2;
                            mParams.y = (int) event.getRawY() - mWindowView.getMeasuredHeight() / 2;
                            mWindowManager.updateViewLayout(mWindowView, mParams);
                            return true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (needIntercept()) {
                            return true;
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        mImgView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(WindowService.this, "点击了", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 是否拦截
     *
     * @return true:拦截;false:不拦截.
     */
    private boolean needIntercept() {
        if (Math.abs(mStartX - mEndX) > 30 || Math.abs(mStartY - mEndY) > 30) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前应用程序处于前台还是后台
     */
    private boolean isAppAtBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                mWindowManager.removeView(mWindowView);
                return true;
            }
        } else {
            mWindowManager.addView(mWindowView, mParams);
        }
        return false;
    }
}
