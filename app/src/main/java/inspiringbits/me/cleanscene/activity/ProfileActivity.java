package inspiringbits.me.cleanscene.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.githang.statusbar.StatusBarCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import inspiringbits.me.cleanscene.R;
import inspiringbits.me.cleanscene.model.BasicMessage;
import inspiringbits.me.cleanscene.model.User;
import inspiringbits.me.cleanscene.rest_service.UserService;
import inspiringbits.me.cleanscene.tool.FacebookTool;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends AppCompatActivity {

    public static final String USER_ID = "userId";
    @BindView(R.id.user_icon)
    SimpleDraweeView userIcon;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.gender)
    TextView gender;
    @BindView(R.id.email)
    TextView email;
    int userId;
    @BindView(R.id.num_report)
    TextView numReport;
    @BindView(R.id.num_volunteer)
    TextView numVolunteer;
    @BindView(R.id.parent)
    ScrollView parent;
    @BindView(R.id.progressBar3)
    ProgressBar progressBar3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5E97DC")));
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.status_bar));
        ButterKnife.bind(this);

        parent.setVisibility(View.GONE);
        userId = getIntent().getIntExtra(USER_ID, 0);
        loadProfile();
        loadContribution();

    }

    private void loadContribution() {
        Observable.create((ObservableOnSubscribe<String[]>) e -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.base_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            UserService userService = retrofit.create(UserService.class);
            BasicMessage m1 = userService.getNumOfReport(FacebookTool.getUserId(ProfileActivity.this)).execute().body();
            BasicMessage m2 = userService.getNumOfVolunteering(FacebookTool.getUserId(ProfileActivity.this)).execute().body();
            if (m1.getStatus() && m2.getStatus()) {
                String[] strs = {m1.getContent(), m2.getContent()};
                e.onNext(strs);
            } else {
                String[] strs = {"0", "0"};
                e.onNext(strs);
            }
            e.onComplete();
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String[]>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String[] integers) {
                        numReport.setText(integers[0]);
                        numVolunteer.setText(integers[1]);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        parent.setVisibility(View.VISIBLE);
                        progressBar3.setVisibility(View.GONE);
                    }
                });
    }

    private void loadProfile() {
        Observable.create((ObservableOnSubscribe<User>) e -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.base_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            UserService userService = retrofit.create(UserService.class);
            User user = userService.getUserById(userId).execute().body();
            e.onNext(user);
            e.onComplete();
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull User user) {
                        name.setText(user.getFullname());
                        gender.setText(user.getGender());
                        email.setText(user.getEmail());
                        userIcon.setImageURI(FacebookTool.getLargeIconUrl(user.getFacebookId()));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
