package inspiringbits.me.cleanscene.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import inspiringbits.me.cleanscene.R;
import inspiringbits.me.cleanscene.adapter.MyActivityAdapter;
import inspiringbits.me.cleanscene.model.VolunteeringActivity;
import inspiringbits.me.cleanscene.rest_service.VolunteerService;
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

public class MyVolunteeringActivity extends AppCompatActivity {

    public static final String VOLUNTEERING_LIST = "vlist";
    MyActivityAdapter activityAdapter;
    @BindView(R.id.my_activity)
    ListView myActivity;
    @BindView(R.id.hint)
    TextView hint;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_volunteering);
        ButterKnife.bind(this);

        if (FacebookTool.isLoggedIn()) {
            loadActivityFromServer();
        } else {
            loadActivityFromLocal();
        }
    }

    private void loadActivityFromLocal() {
        hint.setVisibility(View.GONE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String vIDListStr = sp.getString(VOLUNTEERING_LIST, "");
        Log.d("vids", "loadActivityFromLocal: "+vIDListStr);
        Gson gson=new Gson();
        List<VolunteeringActivity> vList=gson.fromJson(vIDListStr,new TypeToken<List<VolunteeringActivity>>(){}.getType());
        if (vList==null||vList.size()==0){
            hint.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            return;
        }
        /*List<VolunteeringActivity> vList=new ArrayList<VolunteeringActivity>();
        for (String vId:vIDList){
            Observable.create((ObservableOnSubscribe<VolunteeringActivity>) e -> {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(getString(R.string.base_url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                VolunteerService service = retrofit.create(VolunteerService.class);
                VolunteeringActivity v=service.getActivityById(vId).execute().body();
                e.onNext(v);
                e.onComplete();
            })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<VolunteeringActivity>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull VolunteeringActivity volunteeringActivity) {
                            vList.add(volunteeringActivity);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }*/
        activityAdapter=new MyActivityAdapter(MyVolunteeringActivity.this,vList);
        myActivity.setAdapter(activityAdapter);
        activityAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        hint.setVisibility(View.GONE);
    }

    private void loadActivityFromServer() {
        hint.setVisibility(View.GONE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String userId = sp.getString(MainActivity_2.USER_ID, "");
        if ("".equals(userId)) {
            loadActivityFromLocal();
            return;
        }
        Observable.create((ObservableOnSubscribe<List<VolunteeringActivity>>) e -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.base_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            VolunteerService service = retrofit.create(VolunteerService.class);
            List<VolunteeringActivity> vList = service.getActivityForUser(userId).execute().body();
            e.onNext(vList);
            e.onComplete();
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<VolunteeringActivity>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<VolunteeringActivity> volunteeringActivities) {
                        if (volunteeringActivities==null||volunteeringActivities.size()==0){
                            hint.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            return;
                        } else {
                            activityAdapter=new MyActivityAdapter(MyVolunteeringActivity.this,volunteeringActivities);
                            myActivity.setAdapter(activityAdapter);
                            activityAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            hint.setVisibility(View.GONE);
                        }
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
