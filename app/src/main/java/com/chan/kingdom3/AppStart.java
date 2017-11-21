package com.chan.kingdom3;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

public class AppStart extends Activity {

    private GestureDetector detector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = View.inflate(this, R.layout.start, null);
        setContentView(view);

        TextView memb1 = (TextView) findViewById(R.id.memb1);
        TextView memb2 = (TextView) findViewById(R.id.memb2);
        TextView memb3 = (TextView) findViewById(R.id.memb3);
        TextView memb4 = (TextView) findViewById(R.id.memb4);


        memb1.setTypeface(Typeface.createFromAsset(getAssets(), "FZLBJW.TTF"));
        memb2.setTypeface(Typeface.createFromAsset(getAssets(), "FZLBJW.TTF"));
        memb3.setTypeface(Typeface.createFromAsset(getAssets(), "FZLBJW.TTF"));
        memb4.setTypeface(Typeface.createFromAsset(getAssets(), "FZLBJW.TTF"));

        memb1.setEnabled(false);
        memb2.setEnabled(false);
        memb3.setEnabled(false);
        memb4.setEnabled(false);


        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d("distance", String.valueOf(e2.getX() - e1.getX()));
                if (e1.getX() - e2.getX() > 500) {
                    Toast.makeText(AppStart.this, "主公，末将已守候多时", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(AppStart.this, MainActivity.class);
                    startActivity(intent);
                    //设置切换动画，从右边进入，左边退出
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    return true;
                }
                return true;
            }
        });
//
//        ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.start);
//        layout.setOnClickListener(new View.OnClickListener() {
//          @Override
//          public void onClick(View v) {
//              Intent intent = new Intent();
//              intent.setClass(AppStart.this, MainActivity.class);
//              startActivity(intent);
//              //设置切换动画，从右边进入，左边退出
//              overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//          }
//        });

        //渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.3f,1.0f);
        aa.setDuration(2000);
        view.startAnimation(aa);
//        aa.setAnimationListener(new Animation.AnimationListener()
//        {
//            @Override
//            public void onAnimationEnd(Animation arg0) { }
//            @Override
//            public void onAnimationRepeat(Animation animation) {}
//            @Override
//            public void onAnimationStart(Animation animation) {}
//
//        });
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

}
