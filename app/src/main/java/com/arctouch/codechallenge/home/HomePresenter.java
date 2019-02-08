package com.arctouch.codechallenge.home;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.arctouch.codechallenge.api.TmdbApi;
import com.arctouch.codechallenge.api.TmdbRetrofit;
import com.arctouch.codechallenge.data.Cache;
import com.arctouch.codechallenge.model.Genre;
import com.arctouch.codechallenge.model.Movie;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class HomePresenter {

    private HomeView mView;
    private Context mContext;
    private HomeAdapter adapter;
    private List<Movie> mMovieList = new ArrayList<>();
    private boolean isFetchingMovies;
    private int currentPage = 1;

    public HomePresenter(HomeView view, Context context) {
        mView = view;
        mContext = context;
    }

    public void getGenres() {
        TmdbRetrofit.api().genres(TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    Cache.setGenres(response.genres);
                });
    }

    public void getMovies(final int page) {
        isFetchingMovies = true;

        TmdbRetrofit.api().upcomingMovies(TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE, page)
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

                    final LinearLayoutManager manager = (LinearLayoutManager) mView.getRecyclerView().getLayoutManager();

                    if (adapter == null) {
                        adapter = new HomeAdapter(mContext, mMovieList);
                        mView.getRecyclerView().setAdapter(adapter);
                    } else {
                        adapter.appendMovies(mMovieList);
                    }

                    mView.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            if (dy > 0 && mMovieList.size() > 0) {
                                int totalItemCount = manager.getItemCount();
                                int visibleItemCount = manager.getChildCount();
                                int firstVisibleItem = manager.findFirstVisibleItemPosition();

                                if (firstVisibleItem + visibleItemCount >= totalItemCount / 2) {
                                    if (!isFetchingMovies) {
                                        mView.showProgress();
                                        getMovies(currentPage + 1);
                                    }
                                }
                            }
                        }
                    });

                    currentPage = page;
                    isFetchingMovies = false;
                    mView.hideProgress();
                });
    }

}
