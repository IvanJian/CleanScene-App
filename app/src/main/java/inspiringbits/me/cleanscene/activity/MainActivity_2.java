package inspiringbits.me.cleanscene.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.githang.statusbar.StatusBarCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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


public class MainActivity_2 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String USER_ID = "userId";
    @BindView(R.id.new_report)
    ImageView reportBtn;
    @BindView(R.id.report_map)
    ImageView mapBtn;
    @BindView(R.id.my_report)
    ImageView myReportBtn;
    @BindView(R.id.volunteer_menu)
    ImageView volunteerBtn;

    static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION=1;

    NavigationView navigationView;
    LoginButton loginButton;
    CallbackManager callbackManager;
    ProfileTracker profileTracker;


    @OnClick(R.id.new_report)
    public void newReport(ImageView view){
        startActivity(new Intent(this,NewReportActivity_2.class));
    }

    @OnClick(R.id.report_map)
    public void mapView(ImageView view){
        Intent intent=new Intent(this,ViewReportsOnMapActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.volunteer_menu)
    public void volunteerView(ImageView view){
        Intent intent=new Intent(this,VolunteerMenuActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.my_report)
    public void myReportView(ImageView b){
        Intent intent=new Intent(this,MyReportActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    private void login(){
        if (FacebookTool.isLoggedIn()){
            Profile profile=Profile.getCurrentProfile();
            loadingProfile(profile);
        }
        loginButton=(LoginButton) navigationView.getHeaderView(0).findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
        callbackManager=CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.d("res", "onCompleted: "+response.toString());
                                User user=new User();
                                try {
                                    user.setGender(object.getString("gender"));
                                    user.setEmail(object.getString("email"));
                                    user.setFacebookId(object.getString("id"));
                                    user.setFullname(object.getString("name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (user.getFacebookId()!=null){
                                    loadUser(user);
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {
                loadingProfile(currentProfile);
            }
        };
    }

    private void loadingProfile(Profile currentProfile) {
        TextView name;
        name=(TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
        SimpleDraweeView navHeaderPhoto=(SimpleDraweeView)navigationView.getHeaderView(0).findViewById(R.id.nav_header_photo);
        if(currentProfile == null){
            SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor=sp.edit();
            editor.putString(this.USER_ID,"");
            editor.apply();
            name.setText("Login with Facebook");
            Uri uri = new Uri.Builder()
                    .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                    .path(String.valueOf(R.drawable.user_placeholder_image))
                    .build();
            navHeaderPhoto.setImageURI(uri);
        } else {
            String userId=currentProfile.getId();
            User user=new User();
            user.setFacebookId(userId);
            loadUser(user);
            name.setText(currentProfile.getName());
            navHeaderPhoto.setImageURI(currentProfile.getProfilePictureUri(90,90));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);
        ButterKnife.bind(this);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.status_bar));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);

            return;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        login();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void loadUser(User user){
        Observable.create((ObservableOnSubscribe<String>) e -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.base_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            UserService userService=retrofit.create(UserService.class);
            BasicMessage message=userService.loadUser(user).execute().body();
            Log.d("load", "loadUser: "+message.getContent());
            if (message.getStatus()){
                e.onNext(message.getContent());
            }
            e.onComplete();
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull String userId) {
                Log.d("userId", "onNext: "+userId);
                SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(MainActivity_2.this);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString(MainActivity_2.USER_ID,userId);
                editor.apply();
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_new_report) {
            startActivity(new Intent(this,NewReportActivity_2.class));
        } else if (id == R.id.nav_my_reports) {
            startActivity(new Intent(this,MyReportActivity.class));
        } else if (id == R.id.nav_report_map) {
            startActivity(new Intent(this,ViewReportsOnMapActivity.class));
        } else if (id == R.id.nav_new_volunteer) {
            startActivity(new Intent(this, NewVolunteeringActivity.class));
        } else if (id == R.id.nav_my_volunteer) {
            startActivity(new Intent(this,VolunteeringDetailActivity.class));
        } else if (id == R.id.nav_find_volunteer) {
            startActivity(new Intent(this,FindVolunteeringActivity.class));
        } else if (id == R.id.nav_volunteer_centre){
            startActivity(new Intent(this,VolunteerCentreActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
