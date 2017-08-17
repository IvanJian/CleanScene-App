package inspiringbits.me.cleanscene.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import inspiringbits.me.cleanscene.R;
import me.relex.photodraweeview.OnPhotoTapListener;
import me.relex.photodraweeview.OnViewTapListener;
import me.relex.photodraweeview.PhotoDraweeView;


public class PictureActivity extends Activity {

    private PhotoDraweeView mPhotoDraweeView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_picture);
        Uri uri=(Uri)getIntent().getParcelableExtra("uri");
        mPhotoDraweeView = (PhotoDraweeView) findViewById(R.id.picture_pic);
        mPhotoDraweeView.setPhotoUri(uri);
        mPhotoDraweeView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override public void onPhotoTap(View view, float x, float y) {
                PictureActivity.this.finish();
            }
        });
        mPhotoDraweeView.setOnViewTapListener(new OnViewTapListener() {
            @Override public void onViewTap(View view, float x, float y) {
                PictureActivity.this.finish();
            }
        });

    }
}
