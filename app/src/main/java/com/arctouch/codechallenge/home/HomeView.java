package com.arctouch.codechallenge.home;

import android.support.v7.widget.RecyclerView;

public interface HomeView {

    void init();

    void showProgress();

    void hideProgress();

    RecyclerView getRecyclerView();

}
