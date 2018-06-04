package entertainment.githubsearchlist;

import io.reactivex.Observable;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface RetrofitClient {
    @GET("users")
    Observable<List<User>> getUsers();

    @GET("users/{username}")
    Observable<User> getUserFollowers(@Path("username") String user);
}
