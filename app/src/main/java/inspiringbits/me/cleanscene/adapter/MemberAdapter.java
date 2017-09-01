package inspiringbits.me.cleanscene.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import inspiringbits.me.cleanscene.R;
import inspiringbits.me.cleanscene.activity.ProfileActivity;
import inspiringbits.me.cleanscene.model.User;
import inspiringbits.me.cleanscene.tool.FacebookTool;

/**
 * Created by Ivan on 2017/9/1.
 */

public class MemberAdapter extends BaseAdapter {
    private List<User> members;
    private Context context;

    public MemberAdapter(List<User> members, Context context) {
        this.members = members;
        if (members==null){
            this.members=new ArrayList<User>();
        }
        this.context = context;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return members.size();
    }

    @Override
    public Object getItem(int position) {
        return members.get(position);
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
        SimpleDraweeView simpleDraweeView=(SimpleDraweeView)row.findViewById(R.id.member_icon);
        TextView name=(TextView)row.findViewById(R.id.member_name);
        simpleDraweeView.setImageURI(FacebookTool.getSamllIconUrl(members.get(position).getFacebookId()));
        name.setText(members.get(position).getFullname());
        row.setOnClickListener(v -> {
            Intent intent=new Intent(context, ProfileActivity.class);
            intent.putExtra(ProfileActivity.USER_ID,members.get(position).getUserId());
            context.startActivity(intent);
        });
        return row;
    }
}
