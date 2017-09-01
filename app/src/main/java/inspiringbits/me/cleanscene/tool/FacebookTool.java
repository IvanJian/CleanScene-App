package inspiringbits.me.cleanscene.tool;

import com.facebook.AccessToken;

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
}
