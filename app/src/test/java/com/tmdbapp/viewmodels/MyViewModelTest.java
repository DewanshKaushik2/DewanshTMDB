package com.tmdbapp.viewmodels;

import junit.framework.TestCase;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.tmdbapp.api.MovieAPI;
import com.tmdbapp.data.DataRepository;
import com.tmdbapp.models.MovieModel;
import com.tmdbapp.utils.network.NetworkUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class MyViewModelTest {

    @Mock
    private DataRepository mockRepository;

    @Mock
    private MovieAPI mockMovieAPI;

    @Mock
    private SharedPreferences mockPreferences;

    @Mock
    private SharedPreferences.Editor mockEditor;

    @Mock
    private Context mockContext;

    @Mock
    private MutableLiveData<List<MovieModel>> mockPagedList;

    private MyViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this); // Initialize mocks

        // Mocking the SharedPreferences behavior
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPreferences);
        when(mockPreferences.edit()).thenReturn(mockEditor);

        // Create the ViewModel with mocked dependencies
        viewModel = new MyViewModel(mockRepository);
        viewModel.movieAPI = mockMovieAPI;

        // Mock the network check
        mockNetworkAvailability(true);
    }

    private void mockNetworkAvailability(boolean isAvailable) {
        // Mocking NetworkUtils to simulate network availability
        mockStatic(NetworkUtils.class);
        when(NetworkUtils.isNetworkAvailable(mockContext)).thenReturn(isAvailable);
    }

    @Test
    public void testGetPopularMovies_WhenNetworkIsAvailable() {
        // Setup mock repository and data
        List<MovieModel> movieList = new ArrayList<>();
        MovieModel movie = new MovieModel();
        movie.setTitle("Test Movie");
        movieList.add(movie);

        // When repository is called, return mocked data
        when(mockRepository.getMovies(anyInt())).thenReturn(Observable.just(movieList));

        // Observe LiveData
        viewModel.getPopularMovies(mockContext).observeForever(new Observer<List<MovieModel>>() {
            @Override
            public void onChanged(List<MovieModel> movieModels) {
                assertNotNull(movieModels);
                assertEquals(1, movieModels.size());
                assertEquals("Test Movie", movieModels.get(0).getTitle());
            }
        });

        // Trigger API call
        viewModel.getPopularMovies(mockContext);
    }

    @Test
    public void testGetPopularMovies_WhenNetworkIsUnavailable() {
        // Simulate network unavailability
        mockNetworkAvailability(false);

        // Observe LiveData for firstTime (should be true if it's the first time)
        final MutableLiveData<Boolean> firstTimeLiveData = viewModel.isFirstTime();
        firstTimeLiveData.observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                assertTrue(aBoolean);  // Ensure firstTime is true if no network
            }
        });

        // Simulate calling the API when network is unavailable
        viewModel.getPopularMovies(mockContext);

        // Verify that the `firstTime` flag is being set from preferences
        verify(mockPreferences).getBoolean(anyString(), eq(true));
    }

    @Test
    public void testGetLastUpdate() {
        // Mock the shared preferences to return a specific last update date
        String lastUpdate = "2024-12-17 12:34:56";
        when(mockPreferences.getString(anyString(), anyString())).thenReturn(lastUpdate);

        // Get the last update date from ViewModel
        String result = viewModel.getLastUpdate();

        // Ensure the last update date is returned correctly
        assertEquals(lastUpdate, result);
    }

    @Test
    public void testGetLastUpdate_WhenNetworkIsAvailable() {
        // Simulate network being available
        mockNetworkAvailability(true);

        // Check if the result is empty (since network is available)
        String result = viewModel.getLastUpdate();
        assertEquals("", result);  // Should return an empty string if network is available
    }

    @Test
    public void testGetPopularMoviesOnline_ActiveFlow() {
        // Simulate network being available
        mockNetworkAvailability(true);

        // Simulate a movie response from the MovieAPI
        List<MovieModel> movieList = new ArrayList<>();
        MovieModel movie = new MovieModel();
        movie.setTitle("Test Movie");
        movieList.add(movie);

        // Mock API call
        when(mockMovieAPI.getPopular(anyString(), anyString(), anyInt())).thenReturn(Observable.just(movieList));

        // Call the method and observe the results
        viewModel.getPopularMoviesOnline();

        // Verify that the repository method `insertMovie` is called for each movie
        verify(mockRepository).insertMovie(any(MovieModel.class));
    }

    @Test
    public void testFirstTimeFlag() {
        // Check that the firstTime flag is initially set as true
        final MutableLiveData<Boolean> firstTimeLiveData = viewModel.isFirstTime();
        firstTimeLiveData.observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                assertTrue(aBoolean);
            }
        });

        // Simulate first-time user scenario and call the method
        viewModel.getPopularMoviesOnline();
    }

    @Test
    public void testRefreshingFlag() {
        // Check that the refreshing flag is set correctly
        final MutableLiveData<Boolean> refreshingLiveData = viewModel.isRefreshing();
        refreshingLiveData.observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                assertFalse(aBoolean);  // Ensure refreshing is false after fetching movies
            }
        });

        // Simulate network request
        viewModel.getPopularMoviesOnline();
    }
}
