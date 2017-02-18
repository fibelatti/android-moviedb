package com.fibelatti.moviedb.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fibelatti.moviedb.R;
import com.fibelatti.moviedb.apiInterfaces.MovieService;
import com.fibelatti.moviedb.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private Context context;
    private List<Movie> movieList;

    private boolean isLoadingVisible = false;
    private int loadingItemIndex = -1;

    public class LoadingViewHolder
            extends RecyclerView.ViewHolder {
        @BindView(R.id.progress_bar)
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class MovieViewHolder
            extends RecyclerView.ViewHolder {
        @BindView(R.id.image_view_movie_cover)
        public ImageView movieCover;
        @BindView(R.id.text_view_movie_name)
        public TextView movieName;
        @BindView(R.id.text_view_movie_year)
        public TextView movieYear;
        @BindView(R.id.text_view_movie_genres)
        public TextView movieGenres;
        @BindView(R.id.text_view_movie_popularity)
        public TextView moviePopularity;

        public MovieViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public MoviesAdapter(Context context) {
        this.context = context;
        this.movieList = new ArrayList<>();
    }

    public void clearMovieList() {
        this.movieList.clear();
        notifyDataSetChanged();
    }

    public void addMovieToList(Movie movie) {
        this.movieList.add(movie);
    }

    public void showLoadingItem() {
        this.isLoadingVisible = true;
        this.movieList.add(null);
        this.loadingItemIndex = movieList.size() - 1;
        notifyItemInserted(this.loadingItemIndex);
    }

    public void hideLoadingItem() {
        if (isLoadingVisible) {
            this.isLoadingVisible = false;
            this.movieList.remove(this.loadingItemIndex);
            notifyDataSetChanged();
        }
    }

    public Movie getMovieByIndex(int index) {
        return movieList.get(index);
    }

    @Override
    public int getItemViewType(int position) {
        return movieList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return this.movieList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_movie, parent, false);
            return new MovieViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_loading, parent, false);
            return new LoadingViewHolder(itemView);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MovieViewHolder) {
            MovieViewHolder movieViewHolder = (MovieViewHolder) holder;
            Movie item = movieList.get(position);

            Picasso.with(context)
                    .load(MovieService.BASE_POSTER_URL + item.getPosterPath())
                    .into(movieViewHolder.movieCover);

            movieViewHolder.movieName.setText(item.getTitle());
            movieViewHolder.movieYear.setText(context.getString(R.string.main_hint_movie_release_date, item.getReleaseDate().substring(0, 4)));
            movieViewHolder.movieGenres.setText(item.getGenresConcatenated());
            movieViewHolder.moviePopularity.setText(context.getString(R.string.main_hint_movie_popularity_score, item.getPopularity()));
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }
}
