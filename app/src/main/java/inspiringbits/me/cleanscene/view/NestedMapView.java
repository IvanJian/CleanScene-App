package inspiringbits.me.cleanscene.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;

/**
 * Created by Ivan on 2017/8/12.
 * This solution is from Emily Sooryum on stackoverflow
 * https://stackoverflow.com/questions/6546108/mapview-inside-a-scrollview
 */

public class NestedMapView extends MapView {
    public NestedMapView(Context context) {
        super(context);
    }

    public NestedMapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public NestedMapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public NestedMapView(Context context, GoogleMapOptions googleMapOptions) {
        super(context, googleMapOptions);
    }

    @Override
    public boolean dispatchTouchEvent (MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // Disallow ScrollView to intercept touch events.
                this.getParent().requestDisallowInterceptTouchEvent(true);
                break;

            case MotionEvent.ACTION_UP:
                // Allow ScrollView to intercept touch events.
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }

        // Handle MapView's touch events.
        super.dispatchTouchEvent(ev);
        return true;
    }
}
