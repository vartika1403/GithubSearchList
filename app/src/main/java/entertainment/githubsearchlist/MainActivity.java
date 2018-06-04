package entertainment.githubsearchlist;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.Observable;
import io.reactivex.Observer;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String URL = "https://api.github.com/";
    private ArrayList<User> list = new ArrayList<>();
    private GithubUserAdapter githubUserAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.i(LOG_TAG, "onCreate called");
        //getUsersOfGithub();
        User user = new User();
        user.setUser("Vats");
        user.setFollowers(50);
        User user1 = new User();
        user1.setUser("This");
        user1.setFollowers(30);
        list.add(user);
        list.add(user1);
        Collections.sort(list, new SortByFollower());
        displayListOnRecyclerView(list);
        getUsersOfGithub();
    }

    private void getUsersOfGithub() {
        OkHttpClient.Builder okhttpClientBuilder = new OkHttpClient.Builder();
        okhttpClientBuilder.addInterceptor(new AuthenticationInterceptor(MainActivity.this));
        File httpCacheDirectory = new File(getCacheDir(), "responses");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        okhttpClientBuilder.cache(cache);

        Retrofit retrofit = new Retrofit.Builder().client(okhttpClientBuilder.build())
                .baseUrl(URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Log.i(LOG_TAG, "retrofit, " + retrofit);

        final RetrofitClient apiService = retrofit.create(RetrofitClient.class);

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
                return apiService.getUserFollowers(user.user).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .debounce(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread());
            }
        }).subscribe(new Observer<User>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(LOG_TAG, "onSubscribe");
            }

            @Override
            public void onNext(User user) {
                Log.i(LOG_TAG, "onNext: ," + user.getUser() + " " + user.getFollowers());
                list.add(user);
                Collections.sort(list, new SortByFollower());
                githubUserAdapter.notifyDataSetChanged();
                // displayListOnRecyclerView(list);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(LOG_TAG, "error, " + e);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onComplete() {
                Log.i(LOG_TAG, "All users emitted!");
                progressBar.setVisibility(View.GONE);
            }

        });
/*new Consumer<User>() {
            @Override
            public void accept(User user) {

                Log.i(LOG_TAG, "user, " + user.getUser());
                list.add((user));
                githubUserAdapter.notifyDataSetChanged();
               // textViewList.setText(user.getUser());
            }
        });*/


/*
            @Override
            public void onNext(User user) {
                Log.i(LOG_TAG, "onNext, " + user.getUser());
                int position = list.indexOf(user);

                if (position == -1) {
                    // TODO - take action
                    // Ticket not found in the list
                    // This shouldn't happen
                    return;
                }

                list.add(user);
              //  githubUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(LOG_TAG, "error," + e);
            }

            @Override
            public void onComplete() {
             Log.i(LOG_TAG, "OnComplete called");
            }
        });*/

        /*Timer t = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, DisplayListActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("List", (ArrayList<? extends Parcelable>) list);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }
        };

        t.scheduleAtFixedRate(task, 0, 8000);*/

    /*    Intent intent = new Intent(MainActivity.this, DisplayListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("List", (ArrayList<? extends Parcelable>) list);
        intent.putExtras(bundle);
        startActivity(intent);*/
                /*.subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(LOG_TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(User user) {
                        Log.i(LOG_TAG, "onNext: ," + user.getUser() + " " + user.getFollowers());
                        list.add(user);
                       // githubUserAdapter.notifyDataSetChanged();
                       // displayListOnRecyclerView(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(LOG_TAG, "error");
                    }

                    @Override
                    public void onComplete() {
                        Log.i(LOG_TAG, "All users emitted!");
                        // Stuff that updates the UI
                      //  displayListOnRecyclerView(list);

                    }

                });*/
    }

    private void displayListOnRecyclerView(ArrayList<User> list) {
        Log.i(LOG_TAG, "displaye recyclerview");
        githubUserAdapter = new GithubUserAdapter(list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(githubUserAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // use this method when query submitted
                Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
                githubUserAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // use this method for auto complete search process
                Log.i(LOG_TAG, "newText, " + newText);
                githubUserAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //  handleIntent(intent);
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}