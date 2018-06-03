package entertainment.githubsearchlist;


import android.util.Log;

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
        //origResponse.body().close();

        // server should give us a 403, since the header contains 'fail'
        if (origResponse.code() == 403) {
            String refreshToken = "abcd"; // you got this from Auth0 when logging in
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
        }
    }
}
