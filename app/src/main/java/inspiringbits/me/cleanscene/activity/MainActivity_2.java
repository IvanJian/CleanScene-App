package inspiringbits.me.cleanscene.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
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
import com.tr4android.support.extension.widget.FloatingActionMenu;

import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inspiringbits.me.cleanscene.R;
import inspiringbits.me.cleanscene.adapter.TimelineAdapter;
import inspiringbits.me.cleanscene.model.TimelineElement;
import inspiringbits.me.cleanscene.tool.FacebookTool;


public class MainActivity_2 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.main_fam)
    FloatingActionMenu floatingActionMenu;
    @BindView(R.id.main_new_report_fab)
    FloatingActionButton newReportFab;
    @BindView(R.id.main_new_volunteering_fab)
    FloatingActionButton newVolunteeringFab;
    @BindView(R.id.main_timeline_listview)
    ListView timelineListView;
    TimelineAdapter timelineAdapter;
    NavigationView navigationView;
    LoginButton loginButton;
    CallbackManager callbackManager;
    ProfileTracker profileTracker;


    @OnClick(R.id.main_new_volunteering_fab)
    public void newVolunteeringFabClick(View v){
        startActivity(new Intent(this,NewVolunteeringActivity.class));
        floatingActionMenu.collapse();
    }

    @OnClick(R.id.main_new_report_fab)
    public void newReportFabClick(View v){
        startActivity(new Intent(this,NewReportActivity_2.class));
        floatingActionMenu.collapse();
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
            name.setText("Login with Facebook");
            Uri uri = new Uri.Builder()
                    .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                    .path(String.valueOf(R.drawable.user_placeholder_image))
                    .build();
            navHeaderPhoto.setImageURI(uri);
        } else {
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
        floatingActionMenu.setupWithDimmingView(findViewById(R.id.dimming_view), Color.parseColor("#99000000"));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        login();
        /**
         * Test timeline
         */
        timelineAdapter=new TimelineAdapter(null,this);
        timelineListView.setAdapter(timelineAdapter);
        for (int i=0;i<5;i++){
            TimelineElement element=new TimelineElement();
            element.setTitle("Report added");
            element.setTime("2017-08-02 23:25:14");
            element.setLocation("Moniton Beach");
            element.setType(TimelineElement.TIMELINE_TYPE_REPORT);
            timelineAdapter.addElement(element);
        }
        for (int i=0;i<5;i++){
            TimelineElement element=new TimelineElement();
            element.setTitle("Report added");
            element.setTime("2017-08-02 10AM-12PM");
            element.setLocation("Moniton Beach");
            element.setType(TimelineElement.TIMELINE_TYPE_VOLUNTEER_ACTIVITY);
            timelineAdapter.addElement(element);
        }
        timelineAdapter.notifyDataSetChanged();
        /**
         * End of test timeline
         */
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            startActivity(new Intent(this,ProfileActivity.class));
        } else if (id == R.id.nav_send) {
            startActivity(new Intent(this,VolunteeringDetailActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
