package com.arctouch.codechallenge.details;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.arctouch.codechallenge.R;
import com.arctouch.codechallenge.model.Genre;
import com.arctouch.codechallenge.model.Movie;

public class DetailsActivity extends AppCompatActivity {

    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mMovie = getIntent().getParcelableExtra("movie");

        TextView tv_Title = findViewById(R.id.tv_Title);
        tv_Title.setText(mMovie.title);

        TextView tv_Overview = findViewById(R.id.tv_Overview);
        tv_Overview.setText(mMovie.overview);

        TextView tv_Genres = findViewById(R.id.tv_Genrea);
        String genres = "";
        for (Genre genre : mMovie.genres) {
            genres += genre.name + ", ";
        }
        tv_Genres.setText(genres.contains(",") ? genres.substring(0, genres.length() - 2) : "");

        TextView tv_ReleaseDate = findViewById(R.id.tv_ReleaseDate);
        tv_ReleaseDate.setText(mMovie.releaseDate);
    }
}
