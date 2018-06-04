package entertainment.githubsearchlist;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationInterceptor implements Interceptor {
    private static final String LOG_TAG = AuthenticationInterceptor.class.getSimpleName();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request authenticationRequest = originalRequest.newBuilder()
                .header("TestHeader", "fail")
                .build();

        Response origResponse = chain.proceed(authenticationRequest);
        int maxAge = 60*60; // read from cache for 1hr
        return origResponse.newBuilder()
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build();

    }
}
