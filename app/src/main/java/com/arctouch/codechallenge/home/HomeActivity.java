package com.arctouch.codechallenge.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.arctouch.codechallenge.R;
import com.arctouch.codechallenge.api.TmdbApi;
import com.arctouch.codechallenge.base.BaseActivity;
import com.arctouch.codechallenge.data.Cache;
import com.arctouch.codechallenge.model.Genre;
import com.arctouch.codechallenge.model.Movie;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class HomeActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private boolean isFetchingMovies;
    private Integer currentPage = 1;
    private List<Movie> mMovieList = new ArrayList<>();
    private HomeAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        this.recyclerView = findViewById(R.id.recyclerView);
        this.progressBar = findViewById(R.id.progressBar);

        getMovies(1);
    }

    private void getMovies(final Integer page) {
        isFetchingMovies = true;

        api.upcomingMovies(TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    mMovieList = response.results;

                    for (Movie movie : mMovieList) {
                        movie.genres = new ArrayList<>();
                        for (Genre genre : Cache.getGenres()) {
                            if (movie.genreIds.contains(genre.id)) {
                                movie.genres.add(genre);
                            }
                        }
                    }

                    final LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();

                    if (adapter == null) {
                        adapter = new HomeAdapter(this, mMovieList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.appendMovies(mMovieList);
                    }

                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            if (dy > 0 && mMovieList.size() > 0) {
                                int totalItemCount = manager.getItemCount();
                                int visibleItemCount = manager.getChildCount();
                                int firstVisibleItem = manager.findFirstVisibleItemPosition();

                                if (firstVisibleItem + visibleItemCount >= totalItemCount / 2) {
                                    if (!isFetchingMovies) {
                                        progressBar.setVisibility(View.VISIBLE);
                                        getMovies(currentPage + 1);
                                    }
                                }
                            }
                        }
                    });

                    currentPage = page;
                    isFetchingMovies = false;
                    progressBar.setVisibility(View.GONE);
                });
    }

}
