package inspiringbits.me.cleanscene.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import inspiringbits.me.cleanscene.R;
import inspiringbits.me.cleanscene.activity.VolunteeringDetailActivity;

/**
 * Created by Ivan on 2017/8/27.
 */

public class MemberDummyAdapter extends BaseAdapter {
    Context context;

    public MemberDummyAdapter(Context context) {
        this.context=context;
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row;
        row = inflater.inflate(R.layout.member_grid, parent, false);
        return row;
    }
}
