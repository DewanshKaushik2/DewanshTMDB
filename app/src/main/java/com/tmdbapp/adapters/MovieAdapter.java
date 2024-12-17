package com.tmdbapp.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.tmdbapp.R;
import com.tmdbapp.api.APIClient;
import com.tmdbapp.data.DataRepository;
import com.tmdbapp.models.MovieModel;
import com.tmdbapp.views.MovieDetailsActivity;

import java.text.DecimalFormat;
import java.util.List;

//public class MovieAdapter extends PagedListAdapter<MovieModel, MovieAdapter.MovieViewHolder> {
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final int MAX_LENGTH = 25;
    private final Context context;
    private String fromWhere;
    DataRepository repository;

    public MovieAdapter(Context context, String fromWhere, DataRepository repository) {
        //   super(DIFF_CALLBACK);
        this.context = context;
        this.fromWhere = fromWhere;
        this.repository = repository;
    }

    @NonNull
    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.context).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(itemView);
    }

    List<MovieModel> list;

    public void submitList(List<MovieModel> myList) {
        list = myList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.MovieViewHolder holder, int position) {
        MovieModel item = list.get(position);

        if (item != null) holder.bindTo(item);
    }

    @Override
    public int getItemCount() {
        if (list == null)
            return 0;
        return list.size();
    }

    public void setFromWhere(String fromWhere) {
        this.fromWhere = fromWhere;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout listItem;
        private final ImageView poster;
        private final ImageView favorite;
        private final TextView title;
        private final TextView popularity;

        MovieViewHolder(View itemView) {
            super(itemView);

            this.listItem = itemView.findViewById(R.id.list_item_movie);
            this.title = itemView.findViewById(R.id.movie_title);
            this.popularity = itemView.findViewById(R.id.movie_popularity);
            this.poster = itemView.findViewById(R.id.item_movie_poster);
            this.favorite = itemView.findViewById(R.id.movie_favorite);
        }

        private void bindTo(MovieModel movie) {
            String poster = APIClient.getFullPosterPath(movie.getPosterPath());
            Picasso.get().load(poster).into(this.poster);

            String title = movie.getTitle().length() > MAX_LENGTH
                    ? (movie.getTitle().substring(0, MAX_LENGTH) + "...")
                    : movie.getTitle();
            this.title.setText(title);

            this.popularity.setText(String.valueOf(new DecimalFormat("##.##").format(movie.getPopularity())));
            this.favorite.setImageResource(movie.isFavorite() ? R.drawable.icon_favorite : R.drawable.icon_not_favorite);
            this.favorite.setColorFilter(ContextCompat.getColor(context, R.color.red_color));
            this.favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    repository.updateMovieFavorite(movie.getId(), !movie.isFavorite());
                    displayFavoriteToast(!movie.isFavorite()
                            ? MovieModel.FAVORITE_ADDED : MovieModel.FAVORITE_REMOVED);
                }
            });
            this.listItem.setOnClickListener(view -> {
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra("id", movie.getId());
                intent.putExtra("fromWhere", fromWhere);
                ActivityOptions transitionActivity =
                        ActivityOptions.makeSceneTransitionAnimation((Activity) context,
                                this.poster,
                                context.getResources().getString(R.string.poster_transition));
                context.startActivity(intent, transitionActivity.toBundle());
            });
        }
    }

    private Toast toastMessage;

    private void displayFavoriteToast(String message) {
        if (this.toastMessage != null)
            this.toastMessage.cancel();

        this.toastMessage = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        this.toastMessage.show();
    }

    private static final DiffUtil.ItemCallback<MovieModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<MovieModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull MovieModel oldItem,
                                       @NonNull MovieModel newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull MovieModel oldItem,
                                          @NonNull MovieModel newItem) {
            return oldItem == newItem;
        }
    };
}
