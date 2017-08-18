package inspiringbits.me.cleanscene.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inspiringbits.me.cleanscene.R;
import inspiringbits.me.cleanscene.adapter.VolunteerCentreListAdapter;
import inspiringbits.me.cleanscene.model.Volunteer;
import inspiringbits.me.cleanscene.rest_service.VolunteerService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VolunteerCentreActivity extends AppCompatActivity {

    @BindView(R.id.volunteer_centre_list)
    ListView listView;
    @BindView(R.id.volunteer_centre_pBar)
    ProgressBar pBar;
    @BindView(R.id.postcode)
    EditText postcode;
    @BindView(R.id.find_btn)
    Button find;
    @BindView(R.id.display_all_btn)
    Button displayAll;
    VolunteerCentreListAdapter adapter;
    List<Volunteer> volunteers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_centre);
        ButterKnife.bind(this);
        find.setClickable(false);
        displayAll.setClickable(false);
        listView.setVisibility(View.GONE);
        adapter=new VolunteerCentreListAdapter(this,new ArrayList<Volunteer>());
        listView.setAdapter(adapter);
        GetVolunteerCentre g=new GetVolunteerCentre();
        g.execute();
    }

    @OnClick(R.id.display_all_btn)
    public void displayAll(Button b){
        adapter.setVolunteerList(this.volunteers);
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.find_btn)
    public void findByPostcode(Button b){
        if ("".equals(postcode.getText()));
        List<Volunteer> vList=new ArrayList<Volunteer>();
        List<Volunteer> vList2=new ArrayList<Volunteer>();
        vList2.addAll(volunteers);
        vList.addAll(adapter.getVolunteerList());
        for (Volunteer v:vList){
            if (!v.getZipcode().equals(postcode.getText().toString())){
                vList2.remove(v);
            }
        }
        adapter.setVolunteerList(vList2);
        adapter.notifyDataSetChanged();
    }

    private class GetVolunteerCentre extends AsyncTask<Void,Void,List<Volunteer>>{

        @Override
        protected List<Volunteer> doInBackground(Void... params) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.base_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            VolunteerService service=retrofit.create(VolunteerService.class);
            List<Volunteer> volunteers=new ArrayList<Volunteer>();
            try {
                volunteers=service.getVolunteerCentre().execute().body();
                return volunteers;
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(VolunteerCentreActivity.this,"Network Error. Try Later.",Toast.LENGTH_LONG).show();
                VolunteerCentreActivity.this.finish();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Volunteer> volunteers) {
            adapter.setVolunteerList(volunteers);
            VolunteerCentreActivity.this.volunteers=volunteers;
            adapter.notifyDataSetChanged();
            listView.setVisibility(View.VISIBLE);
            pBar.setVisibility(View.GONE);
            find.setClickable(true);
            displayAll.setClickable(true);
        }
    }
}
