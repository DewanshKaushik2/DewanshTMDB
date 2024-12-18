package com.tmdbapp.views;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.tmdbapp.R;
import com.tmdbapp.models.MovieModel;
import com.tmdbapp.viewmodels.MyDetailsViewModel;
import com.tmdbapp.viewmodels.ViewModelFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28) // Change SDK as needed
public class MovieDetailsActivityTest {

    @Mock
    private MyDetailsViewModel mockViewModel;

    @Spy
    private MovieDetailsActivity movieDetailsActivity;

    @InjectMocks
    private ViewModelFactory mockViewModelFactory;

    @Mock
    private LiveData<MovieModel> mockMovieLiveData;

    @Mock
    private MovieModel mockMovie;

    @Mock
    private Toast mockToast;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        // Initialize MovieDetailsActivity with Robolectric
        movieDetailsActivity = Robolectric.setupActivity(MovieDetailsActivity.class);

        // Set up mock ViewModel and data
        when(mockViewModel.setMovie(anyInt())).thenReturn(mockMovieLiveData);
        when(mockMovie.getTitle()).thenReturn("Movie Title");
        when(mockMovie.getReleaseDate()).thenReturn("2024-12-17");
        when(mockMovie.getOverview()).thenReturn("This is an overview.");
        when(mockMovie.getVoteAverage()).thenReturn(7.5f);
    }

    @Test
    public void testOnCreate_shouldSetUpUI() {
        // Simulate the activity creation
        movieDetailsActivity.onCreate(null);

        // Verify UI components are set correctly
        assertNotNull(movieDetailsActivity.findViewById(R.id.movie_title));
        assertNotNull(movieDetailsActivity.findViewById(R.id.rating));
        assertNotNull(movieDetailsActivity.findViewById(R.id.poster_image));

        // Check if the movie title is set
        assertEquals("Movie Title", movieDetailsActivity.movieTitle.getText().toString());

        // Check if the rating is displayed correctly
        assertEquals("7.5", movieDetailsActivity.rating.getText().toString());
    }

    @Test
    public void testLoadMovie_shouldUpdateUIWithMovieData() {
        // Prepare the observer
        Observer<MovieModel> observer = mock(Observer.class);
        movieDetailsActivity.myDetailsViewModel.setMovie(1).observe(movieDetailsActivity, observer);

        // Simulate loading of movie data
        movieDetailsActivity.loadMovie(mockMovie);

        // Verify that the UI is updated with the movie data
        verify(observer).onChanged(mockMovie);
        assertEquals("Movie Title", movieDetailsActivity.movieTitle.getText().toString());
        assertEquals("7.5", movieDetailsActivity.rating.getText().toString());
        assertEquals("This is an overview.", movieDetailsActivity.overviewDescription.getText().toString());
    }

    @Test
    public void testDisplayFavoriteToast_shouldShowToast() {
        movieDetailsActivity.displayFavoriteToast("Added to favorites");

        // Verify that the Toast is shown
        verify(mockToast).show();
    }

    @Test
    public void testOnBackPressed_shouldFinishActivity() {
        movieDetailsActivity.onBackPressed();
        assertTrue(movieDetailsActivity.isFinishing());
    }

    @Test
    public void testBookmarkClick_shouldUpdateFavoriteStatus() {
        // Mock movie as not favorite initially
        when(mockMovie.isFavorite()).thenReturn(false);

        // Trigger the favorite button click
        movieDetailsActivity.bookmarkFavorite.performClick();

        // Verify the movie's favorite status is toggled
        verify(mockViewModel).updateMovieFavorite(mockMovie.getId(), true);
    }

    @Test
    public void testPicassoImageLoading() {
        // Create a Bitmap mock for Picasso
        Bitmap mockBitmap = mock(Bitmap.class);

        // Mock Picasso's behavior
        doAnswer(invocation -> {
            Picasso.LoadedFrom from = mock(Picasso.LoadedFrom.class);
            movieDetailsActivity.target.onBitmapLoaded(mockBitmap, from);
            return null;
        }).when(mockViewModel).loadImage(anyString());

        // Check if Picasso's target is invoked and the background is set
        assertNotNull(movieDetailsActivity.target);
    }

    @Test
    public void testRecyclerViewAdapter() {
        // Mock the movie cast data
        String[] cast = new String[]{"actor1", "actor2"};
        when(mockMovie.getActorsFullPosterPaths()).thenReturn(cast);

        // Trigger loading of movie details
        movieDetailsActivity.loadMovie(mockMovie);

        // Verify the RecyclerView is populated with the correct cast
        RecyclerView.Adapter adapter = movieDetailsActivity.recyclerView.getAdapter();
        assertNotNull(adapter);
        assertEquals(2, adapter.getItemCount());
    }
}