package com.shawon.yousuf.retrofitdemo.activity;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.shawon.yousuf.retrofitdemo.BuildConfig;
import com.shawon.yousuf.retrofitdemo.R;
import com.shawon.yousuf.retrofitdemo.adapter.MoviesAdapter;
import com.shawon.yousuf.retrofitdemo.model.Movie;
import com.shawon.yousuf.retrofitdemo.model.MoviesResponse;
import com.shawon.yousuf.retrofitdemo.rest.ApiClient;
import com.shawon.yousuf.retrofitdemo.rest.ApiInterface;
import com.shawon.yousuf.retrofitdemo.util.VerticalSpaceItemDecoration;

import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    // Your api key
    private final static String API_KEY = BuildConfig.API_KEY;

    MoviesAdapter moviesAdapter;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.my_recycler_view)
    RecyclerView myRecyclerView;


    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (API_KEY.isEmpty()) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.api_key_missing_message), Toast.LENGTH_SHORT).show();
            return;
        }

        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(dpToPx(8)));


        getMovieList();

    }

    private void getMovieList() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<MoviesResponse> call = apiService.getTopRatedMovies(API_KEY);
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                int statusCode = response.code();
                Log.d(TAG, "status Code: " + statusCode);

                if (response.isSuccessful()) {

                    progressBar.setVisibility(View.INVISIBLE);

                    // raw().cacheResponse() and raw().networkResponse() can't be used in if else
                    // cause there can be a scenario where both not null

                    if (response.raw().cacheResponse()!= null) {
                        Log.i(TAG, "Response form cache");
                    }

                    if (response.raw().networkResponse() != null) {
                        Log.i(TAG, "Response from network");
                    }

                    List<Movie> movies = response.body().getResults();
                    Log.d(TAG, "Number of movies received: " + movies.size());

                    moviesAdapter = new MoviesAdapter(movies, R.layout.list_item_movie, getApplicationContext());
                    myRecyclerView.setAdapter(moviesAdapter);

                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "Request failed", Toast.LENGTH_SHORT).show();
                }



            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                Toast.makeText(MainActivity.this, "Request failed", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
