package entertainment.githubsearchlist;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationInterceptor implements Interceptor {
    private static final String LOG_TAG = AuthenticationInterceptor.class.getSimpleName();
    private Context context;

    AuthenticationInterceptor(Context context) {
        this.context = context;
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request authenticationRequest = originalRequest.newBuilder()
                .header("TestHeader", "fail")
                .build();

        Response origResponse = chain.proceed(authenticationRequest);
        //origResponse.body().close();
        if (isNetworkAvailable()) {
            int maxAge = 60*60; // read from cache for 1 minute
            return origResponse.newBuilder()
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build();
        } else {
            int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
            return origResponse.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build();
        }

        // server should give us a 403, since the header contains 'fail'
        /*if (origResponse.code() == 403) {
            Log.i(LOG_TAG, "errror getting 403");

            // start a new synchronous network call to Auth0
           // String newIdToken = fetchNewIdTokenFromAuth0(refreshToken);

            // make a new request with the new id token
            Request newAuthenticationRequest = originalRequest.newBuilder()
                    .header("TestHeader", "succeed")
                    .build();

            // try again
            Response newResponse = chain.proceed(newAuthenticationRequest);

            // hopefully we now have a status of 200
           // newResponse.body().close();
            return newResponse;
        } else {
            return origResponse;
        }*/
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
