package inspiringbits.me.cleanscene.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import inspiringbits.me.cleanscene.R;
import inspiringbits.me.cleanscene.model.TimelineElement;

/**
 * Created by Ivan on 2017/8/25.
 */

public class TimelineAdapter extends BaseAdapter {

    private List<TimelineElement> timelineElementList;
    private Context context;

    public TimelineAdapter(List<TimelineElement> timelineElementList, Context context) {
        this.timelineElementList = timelineElementList;
        if (timelineElementList==null){
            this.timelineElementList=new ArrayList<TimelineElement>();
        }
        this.context = context;
    }

    public List<TimelineElement> getTimelineElementList() {
        return timelineElementList;
    }

    public void setTimelineElementList(List<TimelineElement> timelineElementList) {
        this.timelineElementList = timelineElementList;
        if (timelineElementList==null){
            this.timelineElementList=new ArrayList<TimelineElement>();
        }
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return timelineElementList.size();
    }

    @Override
    public Object getItem(int position) {
        return timelineElementList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row=null;
        switch (timelineElementList.get(position).getType()){
            case TimelineElement.TIMELINE_TYPE_REPORT:
                row=inflater.inflate(R.layout.timeline_new_report,parent,false);
                TextView title=(TextView)row.findViewById(R.id.timeline_title);
                TextView time=(TextView)row.findViewById(R.id.timeline_time);
                TextView location=(TextView)row.findViewById(R.id.timeline_location);
                title.setText(timelineElementList.get(position).getTitle());
                time.setText(timelineElementList.get(position).getTime());
                location.setText(timelineElementList.get(position).getLocation());
                break;
            case TimelineElement.TIMELINE_TYPE_VOLUNTEER_ACTIVITY:
                row=inflater.inflate(R.layout.timeline_volunteering,parent,false);
                TextView titleV=(TextView)row.findViewById(R.id.timeline_title);
                TextView timeV=(TextView)row.findViewById(R.id.timeline_time);
                TextView locationV=(TextView)row.findViewById(R.id.timeline_location);
                titleV.setText(timelineElementList.get(position).getTitle());
                timeV.setText(timelineElementList.get(position).getTime());
                locationV.setText(timelineElementList.get(position).getLocation());
                break;
        }
        return row;
    }

    public void addElement(TimelineElement element){
        this.timelineElementList.add(element);
    }
}
