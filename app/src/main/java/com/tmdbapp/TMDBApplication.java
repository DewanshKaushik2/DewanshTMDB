package com.tmdbapp;

import android.app.Application;

import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;

public class TMDBApplication extends Application {
    public static JobManager JOB_MANAGER;

    @Override
    public void onCreate() {
        super.onCreate();

        configureJobManager();

    }
    private void configureJobManager() {
        JOB_MANAGER = new JobManager(this, new Configuration.Builder(this)
                .minConsumerCount(1)
                .maxConsumerCount(3)
                .loadFactor(3)
                .build());
    }



}
