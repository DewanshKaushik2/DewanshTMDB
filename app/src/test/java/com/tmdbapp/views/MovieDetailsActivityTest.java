package com.tmdbapp.views;

import junit.framework.TestCase;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.squareup.picasso.Picasso;
import com.tmdbapp.R;
import com.tmdbapp.models.MovieModel;
import com.tmdbapp.viewmodels.MyDetailsViewModel;
import com.tmdbapp.viewmodels.ViewModelFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class MovieDetailsActivityTest {

    private MovieDetailsActivity activity;

    @Mock
    private MyDetailsViewModel myDetailsViewModel;

    @Mock
    private MovieModel mockMovie;

    @Mock
    private Toast mockToast;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        activity = Robolectric.buildActivity(MovieDetailsActivity.class).create().get(); // Create and launch activity
        ViewModelFactory mockFactory = mock(ViewModelFactory.class);
        when(mockFactory.create(MyDetailsViewModel.class)).thenReturn(myDetailsViewModel);
        activity.myDetailsViewModel = new ViewModelProvider(activity, mockFactory).get(MyDetailsViewModel.class);
    }

    @Test
    public void testActivityInitialization() {
        // Check if the views are correctly initialized
        assertNotNull(activity.findViewById(R.id.scrollview_movie_details));
        assertNotNull(activity.findViewById(R.id.top_rated_back_button));
        assertNotNull(activity.findViewById(R.id.bookmark_favorite));
        assertNotNull(activity.findViewById(R.id.title_movie));
    }

    @Test
    public void testLoadMovieDetails() {
        // Mock the movie data
        int movieId = 123;
        String title = "Test Movie";
        String posterPath = "http://image.tmdb.org/t/p/w500/xyz.jpg";
        String backdropPath = "http://image.tmdb.org/t/p/w500/abc.jpg";
        boolean isFavorite = true;

        // Set up the mock movie data
        when(mockMovie.getId()).thenReturn(movieId);
        when(mockMovie.getTitle()).thenReturn(title);
        when(mockMovie.getPosterPath()).thenReturn(posterPath);
        when(mockMovie.getBackdropPath()).thenReturn(backdropPath);
        when(mockMovie.isFavorite()).thenReturn(isFavorite);

        // Set up ViewModel to return the mock movie
        MutableLiveData<MovieModel> movieLiveData = new MutableLiveData<>();
        movieLiveData.setValue(mockMovie);
        when(myDetailsViewModel.setMovie(movieId)).thenReturn(movieLiveData);

        // Simulate the movie loading
        activity.myDetailsViewModel.setMovie(movieId).observe(activity, movie -> {
            activity.loadMovie(movie); // Simulate the load movie method being called
        });

        // Verify UI elements are updated with mock movie data
        assertEquals(title, ((TextView) activity.findViewById(R.id.title_movie)).getText().toString());
        assertEquals(isFavorite ? R.drawable.ic_bookmark_red_24dp : R.drawable.ic_bookmark_black_24dp,
                ((ImageView) activity.findViewById(R.id.bookmark_favorite)).getDrawable());
    }

    @Test
    public void testFavoriteButtonClick() {
        // Set up movie data
        when(mockMovie.getId()).thenReturn(123);
        when(mockMovie.isFavorite()).thenReturn(false);

        // Set up the favorite button
        ImageView favoriteButton = activity.findViewById(R.id.bookmark_favorite);

        // Simulate click on the favorite button
        favoriteButton.performClick();

        // Verify the ViewModel's update method is called
        verify(myDetailsViewModel).updateMovieFavorite(123, true);
    }

    @Test
    public void testDisplayFavoriteToast() {
        // Set up the toast message
        String message = "Favorite Added";
        Toast toast = mock(Toast.class);
        activity.toastMessage = toast;

        // Simulate the favorite message from the ViewModel
        activity.displayFavoriteToast(message);

        // Verify the Toast is shown
        verify(toast).show();
        assertEquals(message, activity.toastMessage.getText());
    }

    @Test
    public void testBackButtonClick() {
        // Simulate clicking the back button
        activity.findViewById(R.id.back_button_movie_details).performClick();

        // Verify that the activity finishes
        assertTrue(activity.isFinishing());
    }

    @Test
    public void testLoadMovieWithEmptyBackdrop() {
        // Test the behavior when no backdrop is provided
        when(mockMovie.getBackdropPath()).thenReturn("");
        when(mockMovie.getPosterPath()).thenReturn("http://image.tmdb.org/t/p/w500/xyz.jpg");

        // Set up the mock movie data and load it
        MutableLiveData<MovieModel> movieLiveData = new MutableLiveData<>();
        movieLiveData.setValue(mockMovie);
        when(myDetailsViewModel.setMovie(123)).thenReturn(movieLiveData);

        // Simulate the movie loading
        activity.myDetailsViewModel.setMovie(123).observe(activity, movie -> {
            activity.loadMovie(movie);
        });

        // Check if the backdrop is loaded correctly
        // Add any assertions you might need to verify the behavior with an empty backdrop
    }
}
