package com.tmdbapp;

import junit.framework.TestCase;

import android.app.Application;

import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class TMDBApplicationTest {

    @Mock
    private Application mockApplication;

    private TMDBApplication app;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        app = Robolectric.setupApplication(TMDBApplication.class); // Initialize the app in Robolectric
    }

    @Test
    public void testJobManagerInitialization() {
        // Verify that the JobManager is initialized
        assertNotNull("JobManager should be initialized", TMDBApplication.JOB_MANAGER);
    }

    @Test
    public void testJobManagerConfiguration() {
        // Verify that the JobManager was initialized with the correct configuration
        JobManager jobManager = TMDBApplication.JOB_MANAGER;

        // Retrieve the configuration using reflection (JobManager's internal configuration)
        Configuration configuration = getJobManagerConfiguration(jobManager);

        // Verify that the configuration values are set correctly
        assertNotNull(configuration);
        assertEquals(1, configuration.getMinConsumerCount());
        assertEquals(3, configuration.getMaxConsumerCount());
        assertEquals(3, configuration.getLoadFactor());
    }

    private Configuration getJobManagerConfiguration(JobManager jobManager) {
        // Access the JobManager's internal Configuration object via reflection
        try {
            // Use reflection to access the 'configuration' field from JobManager
            java.lang.reflect.Field field = JobManager.class.getDeclaredField("configuration");
            field.setAccessible(true);
            return (Configuration) field.get(jobManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
