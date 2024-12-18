package com.tmdbapp.views;

import junit.framework.TestCase;

import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.tmdbapp.R;
import com.tmdbapp.adapters.MovieAdapter;
import com.tmdbapp.models.MovieModel;
import com.tmdbapp.viewmodels.MyViewModel;
import com.tmdbapp.viewmodels.ViewModelFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {

    private MainActivity activity;

    @Mock
    private MyViewModel mockViewModel;

    @Mock
    private MovieAdapter mockAdapter;

    @Mock
    private MutableLiveData<List<MovieModel>> mockPagedList;

    private List<MovieModel> movieList;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        movieList = new ArrayList<>();
        movieList.add(new MovieModel(1, "Movie 1", "Overview 1"));
        movieList.add(new MovieModel(2, "Movie 2", "Overview 2"));

        activity = Robolectric.buildActivity(MainActivity.class).create().get(); // Create and launch activity

        ViewModelFactory mockFactory = mock(ViewModelFactory.class);
        when(mockFactory.create(MyViewModel.class)).thenReturn(mockViewModel);
        activity.viewModel = new ViewModelProvider(activity, mockFactory).get(MyViewModel.class);
        activity.adapter = mockAdapter;

        // Set up the mock data for the ViewModel
        mockPagedList.setValue(movieList);
        when(mockViewModel.getPopularMovies(activity)).thenReturn(mockPagedList);
    }

    @Test
    public void testActivityInitialization() {
        // Ensure that the views are correctly initialized
        assertNotNull(activity.findViewById(R.id.rv_movies));
        assertNotNull(activity.findViewById(R.id.search_view));
        assertNotNull(activity.findViewById(R.id.last_update));
        assertNotNull(activity.findViewById(R.id.empty_list));
    }

    @Test
    public void testRecyclerViewDisplaysMovies() {
        // Simulate getting a list of movies and setting it in the adapter
        RecyclerView recyclerView = activity.findViewById(R.id.rv_movies);
        recyclerView.setAdapter(mockAdapter);

        // Simulate the ViewModel returning a list of movies
        activity.viewModel.getPopularMovies(activity).observe(activity, movies -> {
            mockAdapter.submitList(movies);
        });

        // Verify that the adapter's submitList method is called with the movie list
        verify(mockAdapter).submitList(movieList);
    }

    @Test
    public void testSearchFunctionality() {
        // Simulate typing a search query
        String query = "Movie 1";
        activity.search_view.setQuery(query, false); // Simulate query submission

        // Check if performSearch() is called with the correct query
        activity.queryHandler.sendEmptyMessage(1); // Trigger handler to perform search

        // Verify that the search results are filtered correctly
        activity.viewModel.getPopularMovies(activity).observe(activity, pagedList -> {
            assertNotNull(pagedList);
            assertTrue(pagedList.size() == 1); // Only "Movie 1" should be in the list
        });
    }

    @Test
    public void testDisplayNoConnectionMessage() {
        // Simulate the ViewModel indicating no connection
        when(mockViewModel.isFirstTime()).thenReturn(new MutableLiveData<>(true));
        activity.viewModel.isFirstTime().observe(activity, this::displayFirstTimeNoConnection);

        // Verify that the "No connection" message is displayed
        TextView emptyListTextView = activity.findViewById(R.id.empty_list);
        assertEquals("No connection", emptyListTextView.getText().toString());
    }

    @Test
    public void testSetLastUpdate() {
        // Simulate the ViewModel providing the last update timestamp
        String lastUpdate = "Last Update: 2024-12-17";
        when(mockViewModel.getLastUpdate()).thenReturn(lastUpdate);

        // Call setLastUpdate method
        activity.setLastUpdate();

        // Verify that the last update text is set correctly
        TextView lastUpdateTextView = activity.findViewById(R.id.list_last_update);
        assertEquals(lastUpdate, lastUpdateTextView.getText().toString());
    }

    @Test
    public void testSearchQueryHandlerDebounce() {
        // Simulate typing a search query
        activity.search_view.setQuery("Movie", false);

        // Wait for debounce time and ensure the search method is not called before
        // Debounce handler will call performSearch after 1 second
        verify(mockViewModel, never()).getPopularMovies(any());
        activity.queryHandler.sendEmptyMessage(QUERY_SUBMITTED);

        // After the delay, ensure the performSearch is called
        verify(mockViewModel).getPopularMovies(activity);
    }
}
