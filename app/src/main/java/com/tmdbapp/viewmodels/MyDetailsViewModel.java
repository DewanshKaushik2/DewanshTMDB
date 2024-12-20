package com.tmdbapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.tmdbapp.data.DataRepository;
import com.tmdbapp.models.MovieModel;
//git remote add origin https://github.com/DewanshKaushik2/DewanshTMDB.git
//git remote set-url origin https://github.com/DewanshKaushik2/DewanshTMDB.git
//git remote add origin git@github.com:DewanshKaushik2/DewanshTMDB.git
// git@github.com:DewanshKaushik2/DewanshTMDB.git
public class MyDetailsViewModel extends ViewModel {
    private final DataRepository repository;
    private final MediatorLiveData<MovieModel> mediator = new MediatorLiveData<>();
    private final MutableLiveData<String> messageUpdate = new MutableLiveData<>();
    private LiveData<MovieModel> movieModelLiveData = null;

    public MyDetailsViewModel(DataRepository repository) {
        this.repository = repository;
    }

    public MediatorLiveData<MovieModel> setMovie(int id) {
        if (this.movieModelLiveData == null)
            this.mediator.addSource(this.loadMovie(id), mediator::setValue);
        return this.mediator;
    }

    private LiveData<MovieModel> loadMovie(int id) {
        this.movieModelLiveData = this.repository.getMovie(id);
        return this.movieModelLiveData;
    }

    public void updateMovieFavorite(int id, boolean favorite) {
        this.repository.updateMovieFavorite(id, favorite);
        this.messageUpdate.setValue(favorite ? MovieModel.FAVORITE_ADDED : MovieModel.FAVORITE_REMOVED);
    }

    public MutableLiveData<String> getFavoriteMessage() {
        return this.messageUpdate;
    }
}
