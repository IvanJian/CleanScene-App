package inspiringbits.me.cleanscene.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.AccessToken;

import inspiringbits.me.cleanscene.activity.MainActivity_2;

/**
 * Created by Ivan on 2017/8/25.
 */

public class FacebookTool {
    public static boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public static String getSamllIconUrl(String id){
        return "http://graph.facebook.com/"+id+"/picture?type=small";
    }

    public static String getLargeIconUrl(String id){
        return "http://graph.facebook.com/"+id+"/picture?type=large";
    }

    public static String getUserId(Context context){
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(MainActivity_2.USER_ID,"0");
    }
}
