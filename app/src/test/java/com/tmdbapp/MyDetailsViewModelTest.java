package com.tmdbapp;

import androidx.lifecycle.Observer;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.tmdbapp.data.DataRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.ArgumentCaptor;
import  com.tmdbapp.viewmodels.MyDetailsViewModel;
import static org.mockito.Mockito.*;

import kotlinx.coroutines.ExperimentalCoroutinesApi;

@ExperimentalCoroutinesApi
public class MyDetailsViewModelTest {

    @Mock
    public DataRepository mockRepository;

    private MyDetailsViewModel viewModel;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        viewModel = new MyDetailsViewModel(mockRepository);
    }

    @Test
    public void testSetMovie() {
        // Arrange
        int movieId = 123;
        MovieModel mockMovie = new MovieModel();
        mockMovie.setId(movieId);

        MutableLiveData<MovieModel> movieLiveData = new MutableLiveData<>();
        movieLiveData.setValue(mockMovie);

        // Mock the repository method to return a LiveData instance
        when(mockRepository.getMovie(movieId)).thenReturn(movieLiveData);

        // Act
        LiveData<MovieModel> movieLiveDataFromViewModel = viewModel.setMovie(movieId);

        // Observe and capture the movie model
        ArgumentCaptor<Observer<MovieModel>> captor = ArgumentCaptor.forClass(Observer.class);
        movieLiveDataFromViewModel.observeForever(captor.capture());
        captor.getValue().onChanged(mockMovie);

        // Assert that the movie model was set correctly
        verify(mockRepository).getMovie(movieId);
        assert(captor.getValue()).onChanged(mockMovie);
    }

    @Test
    public void testUpdateMovieFavorite() {
        // Arrange
        int movieId = 123;
        boolean favorite = true;

        // Act
        viewModel.updateMovieFavorite(movieId, favorite);

        // Assert
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockRepository).updateMovieFavorite(movieId, favorite);
        verify(viewModel.getFavoriteMessage()).setValue(MovieModel.FAVORITE_ADDED);

        // Capture the message for assertion
        viewModel.getFavoriteMessage().observeForever(captor.capture());
        assert captor.getValue().equals(MovieModel.FAVORITE_ADDED);
    }

    @Test
    public void testUpdateMovieFavorite_Remove() {
        // Arrange
        int movieId = 123;
        boolean favorite = false;

        // Act
        viewModel.updateMovieFavorite(movieId, favorite);

        // Assert
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockRepository).updateMovieFavorite(movieId, favorite);
        verify(viewModel.getFavoriteMessage()).setValue(MovieModel.FAVORITE_REMOVED);

        // Capture the message for assertion
        viewModel.getFavoriteMessage().observeForever(captor.capture());
        assert captor.getValue().equals(MovieModel.FAVORITE_REMOVED);
    }
}

