package inspiringbits.me.cleanscene.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import inspiringbits.me.cleanscene.R;
import inspiringbits.me.cleanscene.model.Volunteer;

/**
 * Created by Ivan on 2017/8/19.
 */

public class VolunteerCentreListAdapter extends BaseAdapter {

    private Context context;
    private List<Volunteer> volunteerList;

    public VolunteerCentreListAdapter(Context context, List<Volunteer> volunteerList) {
        this.context = context;
        this.volunteerList = volunteerList;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Volunteer> getVolunteerList() {
        return volunteerList;
    }

    public void setVolunteerList(List<Volunteer> volunteerList) {
        this.volunteerList = volunteerList;
    }

    @Override
    public int getCount() {
        return volunteerList.size();
    }

    @Override
    public Object getItem(int position) {
        return volunteerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row;
        row = inflater.inflate(R.layout.volunteer_center_list, parent, false);
        TextView name=(TextView)row.findViewById(R.id.volunteer_centre_name);
        TextView suburb=(TextView)row.findViewById(R.id.volunteer_centre_suburb);
        TextView address=(TextView)row.findViewById(R.id.volunteer_centre_address);
        TextView zipcode=(TextView)row.findViewById(R.id.volunteer_centre_zipcode);
        name.setText(volunteerList.get(position).getvName());
        suburb.setText(volunteerList.get(position).getSuburb());
        address.setText(volunteerList.get(position).getAddress());
        zipcode.setText(volunteerList.get(position).getZipcode());
        row.setOnClickListener(v->{
            Uri gmmIntentUri = Uri.parse("geo:0,0?q="+volunteerList.get(position).getAddress()+", "+volunteerList.get(position).getSuburb());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            context.startActivity(mapIntent);
        });
        return row;
    }
}
