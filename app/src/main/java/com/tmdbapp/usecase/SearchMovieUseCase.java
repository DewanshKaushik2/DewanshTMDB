//package com.tmdbapp.usecase;
//
//public class SearchMovieUseCase extends BaseUseCase {
//
//    public interface SearchMovieUseCaseCallback extends BaseUseCaseCallback {
//        void onMoviesSearched(List<MovieEntity> movieEntities);
//    }
//
//    private String apiKey;
//    private String query;
//
//    public SearchMovieUseCase(String apiKey, String query, SearchMovieUseCaseCallback callback) {
//        super(callback);
//        this.apiKey = apiKey;
//        this.query = query;
//    }
//
//    @Override
//    public void onRun() throws Throwable {
//        API.http().search(apiKey, query, new Callback<SearchMovieResponse>() {
//            @Override
//            public void success(SearchMovieResponse searchMovieResponse, Response response) {
//                ((SearchMovieUseCaseCallback) callback).onMoviesSearched(searchMovieResponse.getResults());
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                if (error.getKind() == RetrofitError.Kind.NETWORK) {
//                    errorReason = NETWORK_ERROR;
//                } else {
//                    errorReason = error.getResponse().getReason();
//                }
//                onCancel();
//            }
//        });
//    }
//}
