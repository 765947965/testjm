package com.floatingwindow;

import android.app.Instrumentation;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * <br> ClassName:   ${className}
 * <br> Description:
 * <br>
 * <br> @author:      谢文良
 * <br> Date:        2018/1/11 9:47
 */

public class FloatingService extends Service implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private WindowManager windowManager;
    private View viewRoot;
    private WindowManager.LayoutParams params;
    private TextView text1, text2, text3, cut;
    private Button mButton;
    private Disposable mDisposable;
    private SeekBar SeekBar1, SeekBar2, SeekBar3, SeekBar4;
    private DisView mDisView;
    private int value1, value2;
    private double multiple;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //设置效果为背景透明.
        params.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        //设置窗口初始停靠位置.
        params.gravity = Gravity.LEFT | Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 0;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;

        viewRoot = LayoutInflater.from(getApplicationContext()).inflate(R.layout.floating, null);
        text1 = viewRoot.findViewById(R.id.text1);
        text2 = viewRoot.findViewById(R.id.text2);
        text3 = viewRoot.findViewById(R.id.text3);
        cut = viewRoot.findViewById(R.id.cut);
        mButton = viewRoot.findViewById(R.id.mButton);
        SeekBar1 = viewRoot.findViewById(R.id.SeekBar1);
        SeekBar2 = viewRoot.findViewById(R.id.SeekBar2);
        SeekBar3 = viewRoot.findViewById(R.id.SeekBar3);
        SeekBar4 = viewRoot.findViewById(R.id.SeekBar4);
        mDisView = viewRoot.findViewById(R.id.mDisView);
        SeekBar1.setOnSeekBarChangeListener(this);
        SeekBar2.setOnSeekBarChangeListener(this);
        mButton.setOnClickListener(this);
        windowManager.addView(viewRoot, params);
        timer();
    }

    private void changeMultiple() {
        double key1 = value1;
        double key2 = ((double) value2) / 10d;
        multiple = key1 * key2;
        text1.setText(String.valueOf(key1));
        text2.setText(String.valueOf(key2));
        text3.setText(String.valueOf(multiple));
    }

    private void timer() {
        Observable.timer(10000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                // 拦截所有事件
                params.flags = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
                viewRoot.setBackgroundColor(Color.parseColor("#55ffddd1"));
                mButton.setText("完成");
                windowManager.updateViewLayout(viewRoot, params);
                value1 = SeekBar1.getProgress();
                value2 = SeekBar2.getProgress();
                changeMultiple();
            }
        });
    }


    private void interval() {
        mDisposable = Observable.interval(5000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (aLong % 2L == 0) {
                            // 拦截所有事件
                            params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
                            cut.setText("拦截所有事件");
                        } else {
                            // 不拦截事件
                            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
                            cut.setText("不拦截事件");
                        }
                        windowManager.updateViewLayout(viewRoot, params);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
        windowManager.removeView(viewRoot);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId() == R.id.SeekBar1) {
            value1 = progress;
        } else {
            value2 = progress;
        }
        changeMultiple();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private Disposable ll;
    private boolean isRun;

    @Override
    public void onClick(View v) {
        if (isRun) {
            return;
        }
        isRun = true;
        long time = (long) (multiple * mDisView.getLine());
        cut.setText(String.valueOf(time));
        long startTime = 2500 - time;
        startTime = startTime < 0 ? 0 : startTime;
        SeekBar3.setProgress(0);
        SeekBar4.setProgress((int) (startTime / 25));
        Observable.timer(startTime - 500 < 0 ? 0 : startTime - 500, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                // 不拦截所有事件
                params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
                viewRoot.setBackgroundColor(Color.parseColor("#55ddddd1"));
                windowManager.updateViewLayout(viewRoot, params);
            }
        });
        Observable.timer(3500, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                // 拦截所有事件
                params.flags = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
                viewRoot.setBackgroundColor(Color.parseColor("#55ffddd1"));
                windowManager.updateViewLayout(viewRoot, params);
                isRun = false;
            }
        });
        ll = Observable.interval(25, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        int progress = SeekBar3.getProgress() + 1;
                        if (progress > 100) {
                            SeekBar3.setProgress(0);
                            if (ll != null) {
                                ll.dispose();
                            }
                        } else {
                            SeekBar3.setProgress(progress);
                        }
                    }
                });

//        final Instrumentation inst = new Instrumentation();
//        Observable.timer(startTime, TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
//            @Override
//            public void accept(Long aLong) throws Exception {
//                inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),
//                        MotionEvent.ACTION_DOWN, 400, 700, 0));
//            }
//        });
//        Observable.timer(3500, TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
//            @Override
//            public void accept(Long aLong) throws Exception {
//                inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),
//                        MotionEvent.ACTION_UP, 400, 700, 0));
//            }
//        });
    }
}
