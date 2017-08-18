package inspiringbits.me.cleanscene.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.function.Function;

import inspiringbits.me.cleanscene.R;

public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_launch);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        super.onCreate(savedInstanceState);
        new Timer(2000,1000,this::startMainPage).start();
    }

    private Integer startMainPage(CountDownTimer timer){
        timer.cancel();
        timer=null;
        startActivity(new Intent(this,MainActivity.class));
        finish();
        return 0;
    }

    private class Timer extends CountDownTimer {

        Function<CountDownTimer,Integer> callback;

        public Timer(long millisInFuture, long countDownInterval,Function<CountDownTimer,Integer> callback) {
            super(millisInFuture, countDownInterval);
            this.callback=callback;
        }


        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            callback.apply(this);


        }
    }
}
