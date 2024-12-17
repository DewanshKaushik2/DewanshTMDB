package com.tmdbapp.views;

import androidx.paging.DataSource;

import com.tmdbapp.models.MovieModel;

import java.util.List;

public class MovieDataSource extends DataSource.Factory<Integer, MovieModel> {

    private List<MovieModel> movieList;

    public MovieDataSource(List<MovieModel> movieList) {
        this.movieList = movieList;
    }

    @Override
    public DataSource<Integer, MovieModel> create() {
    //    return new MovieDataSourceImpl(movieList);
        return null;
    }
}
