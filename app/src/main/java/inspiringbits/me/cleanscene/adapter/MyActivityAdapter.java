package inspiringbits.me.cleanscene.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import inspiringbits.me.cleanscene.R;
import inspiringbits.me.cleanscene.activity.VolunteeringDetailActivity;
import inspiringbits.me.cleanscene.model.VolunteeringActivity;

/**
 * Created by Ivan on 2017/9/2.
 */

public class MyActivityAdapter extends BaseAdapter {
    private Context context;
    private List<VolunteeringActivity> volunteeringActivities;

    public MyActivityAdapter(Context context, List<VolunteeringActivity> volunteeringActivities) {
        this.context = context;
        this.volunteeringActivities = volunteeringActivities;
        if (volunteeringActivities==null){
            this.volunteeringActivities=new ArrayList<VolunteeringActivity>();
        }
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<VolunteeringActivity> getVolunteeringActivities() {
        return volunteeringActivities;
    }

    public void setVolunteeringActivities(List<VolunteeringActivity> volunteeringActivities) {
        this.volunteeringActivities = volunteeringActivities;
    }

    @Override
    public int getCount() {
        return volunteeringActivities.size();
    }

    @Override
    public Object getItem(int position) {
        return volunteeringActivities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row;
        row = inflater.inflate(R.layout.my_activity_element, parent, false);
        TextView address=(TextView) row.findViewById(R.id.address);
        TextView date=(TextView)row.findViewById(R.id.date);
        TextView time=(TextView)row.findViewById(R.id.time);
        address.setText(volunteeringActivities.get(position).getAddress());
        date.setText("Date: "+volunteeringActivities.get(position).getActivityDate());
        time.setText("Time: "+volunteeringActivities.get(position).getFromTime()+"-"+volunteeringActivities.get(position).getToTime());
        row.setOnClickListener(v->{
            Intent intent=new Intent(context, VolunteeringDetailActivity.class);
            intent.putExtra(VolunteeringDetailActivity.VOLUNTEERING_ACTIVITY_ID,volunteeringActivities.get(position).getVolunteeringActivityId().toString());
            context.startActivity(intent);
        });
        return row;
    }
}
