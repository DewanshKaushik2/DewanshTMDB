package com.tmdbapp.dump;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tmdbapp.R;
import com.tmdbapp.adapters.MovieAdapter;
import com.tmdbapp.data.DataRepository;
import com.tmdbapp.models.MovieModel;
import com.tmdbapp.viewmodels.MyViewModel;
import com.tmdbapp.viewmodels.ViewModelFactory;
import com.tmdbapp.views.BaseActivity;
import com.tmdbapp.views.MainActivity;

import java.util.List;
import java.util.stream.Collectors;

 class TempMainActivity extends BaseActivity {
    private MyViewModel viewModel;
    private LinearLayout linearLayoutLastUpdate;
    private TextView textViewLastUpdate;
    private TextView emptyList;
    //    private SwipeRefreshLayout pullToRefresh;
    String TAG = MainActivity.class.getSimpleName();
    SearchView search_view;
    private String typedQuery;
    public static final int QUERY_SUBMITTED = 1;

    private Handler queryHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == QUERY_SUBMITTED) {
                performSearch(typedQuery);
            }
        }
    };

    public void performSearch(String string) {
        Log.e(TAG, string);
        if (adapter == null) {
            return;
        }
        this.viewModel.getPopularMovies(this).observe(this, pagedList -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                PagedList<MovieModel> mylist = new ArrayList<>();
//                for(MovieModel paged:pagedList){
//                    mylist.add(paged);
//                }
                List<MovieModel> filteredNames = pagedList.stream()
                        .filter(name -> name.getTitle().toLowerCase().contains(string))
                        .collect(Collectors.toList());

/*                PagedList<MovieModel> filteredNames = new PagedList.Builder<>(
                        new MyDataSourceFactory(), // Replace with your data source factory
                        pagedList.getConfig()
                ).setInitialKey(0) // Optional, depending on your requirements
                        .build();

// Filter the list and create a new PagedList with filtered data
                List<MovieModel> filteredList = pagedList.stream()
                        .filter(name -> name.getTitle().toLowerCase().contains(string.toLowerCase()))
                        .collect(Collectors.toList());

// Create a new PagedList (assuming your data source supports filtering this way)
                PagedList<MovieModel> filteredPagedList = new PagedList.Builder<>(
                        new MyDataSourceFactory(filteredList), // Data source factory with filtered data
                        pagedList.getConfig()
                ).build();*/

                System.out.println("meranam" + filteredNames);
                adapter.submitList(filteredNames);

            }

        });

    }

    MovieAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        ViewModelFactory viewModelFactory = ViewModelFactory.createFactory(this);
        this.viewModel = new ViewModelProvider(this, viewModelFactory).get(MyViewModel.class);
        adapter = new MovieAdapter(this, "Popular", DataRepository.getInstance(getApplication()));
        this.viewModel.getPopularMovies(this).observe(this, pagedList -> {
            adapter.submitList(pagedList);
        });
        this.viewModel.isFirstTime().observe(this, this::displayFirstTimeNoConnection);
        this.viewModel.isRefreshing().observe(this, this::displayRefreshing);

        this.linearLayoutLastUpdate = findViewById(R.id.last_update);
        this.textViewLastUpdate = findViewById(R.id.list_last_update);
        this.emptyList = findViewById(R.id.empty_list);
        this.search_view = findViewById(R.id.search_view);

        RecyclerView recyclerView = findViewById(R.id.rv_movies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        this.setLastUpdate();

//        this.pullToRefresh = findViewById(R.id.refresh);
//        this.pullToRefresh.setOnRefreshListener(() -> {
//            this.viewModel.getPopularMoviesOnline();
//            this.setLastUpdate();
//        });
        search_view.setIconifiedByDefault(false);
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                if (!query.equals(typedQuery)) { // avoid a consecutive api request
                    performSearch(query);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                typedQuery = newText; // store the query

                queryHandler.removeMessages(QUERY_SUBMITTED);
                queryHandler.sendEmptyMessageDelayed(QUERY_SUBMITTED, 1000);

                return false;
            }
        });
    }

    private void setLastUpdate() {
        Log.e(TAG, "setLastUpdate");
        String lastUpdate = this.viewModel.getLastUpdate();
        this.textViewLastUpdate.setText(lastUpdate);
        this.linearLayoutLastUpdate.setVisibility(lastUpdate.isEmpty() ? View.GONE : View.VISIBLE);
    }

    public void displayFirstTimeNoConnection(boolean noConnection) {
        if (noConnection) this.emptyList.setText(R.string.no_connection);
        else this.emptyList.setText("");
    }

    private void displayRefreshing(boolean refreshing) {
//        if (!refreshing) pullToRefresh.setRefreshing(false);
    }
}