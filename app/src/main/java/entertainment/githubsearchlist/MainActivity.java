package entertainment.githubsearchlist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.Observable;
import io.reactivex.Observer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String URL = "https://api.github.com/";
    private ArrayList<User> list = new ArrayList<>();
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getUsersOfGithub();
    }

    private void getUsersOfGithub() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Log.i(LOG_TAG, "retrofit, " + retrofit);

        RetrofitClient apiService = retrofit.create(RetrofitClient.class);

        Observable<List<User>> observableList = apiService.getUsers();
        Log.i(LOG_TAG, "observable, " + observableList);


        observableList
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap(new Function<List<User>, Observable<User>>() {
                    @Override
                    public Observable<User> apply(@NonNull List<User> users) throws Exception {

                        return Observable.fromIterable(users);
                    }
                }).concatMap(new Function<User, Observable<User>>() {
                        @Override
                        public Observable<User> apply(User user) throws Exception {
                            return apiService.getUserFollowers(user.user).subscribeOn(Schedulers.io());
                        }
                 })
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(LOG_TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(User user) {
                        Log.i(LOG_TAG, "onNext: ," + user.getUser() + " " + user.getFollowers());
                        list.add(user);
                    }

                    @Override
                    public void onError(Throwable e) {
                          Log.e(LOG_TAG, "error");
                    }

                    @Override
                    public void onComplete() {
                        Log.e(LOG_TAG, "All users emitted!");

                        displayListOnRecyclerView(list);
                    }

                });
    }

    private void displayListOnRecyclerView(ArrayList<User> list) {
        if (list.isEmpty()) {
            return;
        }


    }

}
