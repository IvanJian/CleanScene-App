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
}
