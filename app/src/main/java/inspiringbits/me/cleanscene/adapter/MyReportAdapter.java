package inspiringbits.me.cleanscene.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import inspiringbits.me.cleanscene.R;
import inspiringbits.me.cleanscene.model.ReportModel;

/**
 * Created by Ivan on 2017/8/19.
 */

public class MyReportAdapter extends BaseAdapter {

    List<ReportModel> reportModelList;
    Context context;

    public MyReportAdapter(List<ReportModel> reportModelList, Context context) {
        this.reportModelList = reportModelList;
        this.context = context;
    }

    public List<ReportModel> getReportModelList() {
        return reportModelList;
    }

    public void setReportModelList(List<ReportModel> reportModelList) {
        this.reportModelList = reportModelList;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return reportModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return reportModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row;
        row = inflater.inflate(R.layout.my_report_grid, parent, false);
        SimpleDraweeView image=(SimpleDraweeView)row.findViewById(R.id.my_report_image);
        TextView reportIdTxt=(TextView)row.findViewById(R.id.my_report_id);
        TextView timeTxt=(TextView)row.findViewById(R.id.my_report_time);
        reportIdTxt.setText("Report ID: "+reportModelList.get(position).getReportId());
        timeTxt.setText(reportModelList.get(position).getDate()+" "+reportModelList.get(position).getTime());
        String uris=reportModelList.get(position).getPhoto();
        String fUri=uris.split("\\*")[0];
        image.setImageURI(Uri.parse(context.getString(R.string.base_img_url)+"/"+fUri));
        return row;
    }


}
