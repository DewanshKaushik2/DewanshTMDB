package com.tmdbapp.viewmodels;

import junit.framework.TestCase;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import com.tmdbapp.data.DataRepository;
import com.tmdbapp.models.MovieModel;
import com.tmdbapp.viewmodels.MyDetailsViewModel;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import com.tmdbapp.data.DataRepository;
import com.tmdbapp.models.MovieModel;
import com.tmdbapp.viewmodels.MyDetailsViewModel;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MyDetailsViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule(); // Ensures LiveData works synchronously

    @Mock
    private DataRepository mockRepository;

    private MyDetailsViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        viewModel = new MyDetailsViewModel(mockRepository);
    }

    @Test
    public void testSetMovie() {
        // Prepare test data
        int movieId = 1;
        MovieModel movie = new MovieModel();
        movie.setId(movieId);
        movie.setTitle("Test Movie");

        // Mock the repository to return a LiveData containing the movie
        MutableLiveData<MovieModel> movieLiveData = new MutableLiveData<>();
        movieLiveData.setValue(movie);
        when(mockRepository.getMovie(movieId)).thenReturn(movieLiveData);

        // Observe the MediatorLiveData in the ViewModel
        Observer<MovieModel> movieObserver = mock(Observer.class);
        viewModel.setMovie(movieId).observeForever(movieObserver);

        // Verify that the movie LiveData is set properly
        verify(movieObserver).onChanged(movie);
    }

    @Test
    public void testUpdateMovieFavorite() {
        // Prepare test data
        int movieId = 1;
        boolean isFavorite = true;

        // Call the method to update the favorite status
        viewModel.updateMovieFavorite(movieId, isFavorite);

        // Verify that the repository method is called
        verify(mockRepository).updateMovieFavorite(movieId, isFavorite);

        // Verify that the message update LiveData contains the correct value
        assertEquals(MovieModel.FAVORITE_ADDED, viewModel.getFavoriteMessage().getValue());

        // Now test for removing the favorite
        isFavorite = false;
        viewModel.updateMovieFavorite(movieId, isFavorite);

        // Verify repository call
        verify(mockRepository).updateMovieFavorite(movieId, isFavorite);

        // Verify the updated message
        assertEquals(MovieModel.FAVORITE_REMOVED, viewModel.getFavoriteMessage().getValue());
    }

    @Test
    public void testSetMovieNull() {
        // Mock repository to return null or empty LiveData
        MutableLiveData<MovieModel> emptyLiveData = new MutableLiveData<>();
        when(mockRepository.getMovie(anyInt())).thenReturn(emptyLiveData);

        // Observe the MediatorLiveData in the ViewModel
        Observer<MovieModel> movieObserver = mock(Observer.class);
        viewModel.setMovie(1).observeForever(movieObserver);

        // Verify that the observer gets null when no movie data is returned
        verify(movieObserver).onChanged(null);
    }
}
