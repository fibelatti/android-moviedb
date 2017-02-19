package com.fibelatti.moviedb.views.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fibelatti.moviedb.BuildConfig;
import com.fibelatti.moviedb.Constants;
import com.fibelatti.moviedb.R;
import com.fibelatti.moviedb.apiInterfaces.MovieService;
import com.fibelatti.moviedb.models.Movie;
import com.fibelatti.moviedb.presenters.IMovieDetailPresenter;
import com.fibelatti.moviedb.presenters.MovieDetailPresenter;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MovieDetailActivity
        extends YouTubeBaseActivity
        implements YouTubePlayer.OnInitializedListener {
    public static final String TAG = MovieDetailActivity.class.getSimpleName();

    private Context context;
    private IMovieDetailPresenter presenter;

    private Subscription videoSubscription;

    private Movie movie;
    private String movieVideoId;

    private Animator currentAnimator;
    private int shortAnimationDuration;

    private YouTubePlayer youTubePlayer;
    private boolean isFullScreen;

    //region layout bindings
    @BindView(R.id.root_layout)
    CoordinatorLayout rootLayout;
    @BindView(R.id.image_view_movie_poster)
    ImageView moviePoster;
    @BindView(R.id.image_view_expanded)
    ImageView moviePosterExpanded;
    @BindView(R.id.button_back)
    ImageButton buttonBack;
    @BindView(R.id.layout_movie_info)
    LinearLayout layoutMovieInfo;
    @BindView(R.id.text_view_movie_name)
    TextView movieName;
    @BindView(R.id.text_view_movie_year)
    TextView movieYear;
    @BindView(R.id.text_view_movie_popularity)
    TextView moviePopularity;
    @BindView(R.id.text_view_plot)
    TextView plot;
    @BindView(R.id.text_view_genres)
    TextView genres;
    @BindView(R.id.youtube_player_view)
    YouTubePlayerView youTubePlayerView;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        presenter = MovieDetailPresenter.createPresenter(this);

        presenter.onCreate();

        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        movie = fetchDataFromIntent(savedInstanceState);

        setUpLayout();
        setUpValues();
        searchVideos();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();

        if (!videoSubscription.isUnsubscribed()) videoSubscription.unsubscribe();
    }

    @Override
    public void onBackPressed() {
        if (moviePosterExpanded.getVisibility() == View.VISIBLE) {
            moviePosterExpanded.setVisibility(View.GONE);
        } else if (isFullScreen) {
            youTubePlayer.setFullscreen(false);
        } else {
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Constants.INTENT_EXTRA_MOVIE, movie);
    }

    private void setUpLayout() {
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
    }

    private Movie fetchDataFromIntent(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            return (Movie) savedInstanceState.getSerializable(Constants.INTENT_EXTRA_MOVIE);
        } else if (getIntent().hasExtra(Constants.INTENT_EXTRA_MOVIE)) {
            return (Movie) getIntent().getSerializableExtra(Constants.INTENT_EXTRA_MOVIE);
        }

        return null;
    }

    private void setUpValues() {
        Picasso.with(context)
                .load(MovieService.BASE_POSTER_URL + movie.getPosterPath())
                .into(moviePoster);

        Picasso.with(context)
                .load(MovieService.BASE_POSTER_URL + movie.getPosterPath())
                .into(moviePosterExpanded);

        movieName.setText(movie.getTitle());
        movieYear.setText(context.getString(R.string.main_hint_movie_release_date, movie.getReleaseDate().substring(0, 4)));
        moviePopularity.setText(String.format("%.2f", movie.getPopularity()));
        plot.setText(movie.getOverview());
        genres.setText(movie.getGenresConcatenated());
    }

    private void searchVideos() {
        if (videoSubscription != null && !videoSubscription .isUnsubscribed())
            videoSubscription .unsubscribe();

        videoSubscription = presenter.getVideo(movie.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        searchVideoResult -> {
                            movieVideoId = searchVideoResult.getKey();
                            youTubePlayerView.initialize(BuildConfig.YOUTUBE_API_KEY, this);
                        },
                        throwable -> Log.e(getString(R.string.generic_log_error, TAG, "searchVideos"), throwable.getMessage())
                );
    }

    @OnClick(R.id.button_back)
    public void backToMovieList() {
        finish();
    }

    @OnClick(R.id.image_view_movie_poster)
    public void handlePosterClick() {
        zoomImageFromThumb(moviePoster);
    }

    @OnClick(R.id.layout_movie_info)
    public void handleInfoClick() {
        zoomImageFromThumb(moviePoster);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        Log.e(getString(R.string.generic_log_error, TAG, "onInitializationFailure"), "Failed to initialize YouTube.");
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (null == player) return;
        if (!wasRestored) {
            youTubePlayer = player;
            youTubePlayer.cueVideo(movieVideoId);
            youTubePlayer.setOnFullscreenListener(_isFullScreen -> isFullScreen = _isFullScreen);
        }

        youTubePlayerView.setVisibility(View.VISIBLE);
    }

    private void zoomImageFromThumb(final View thumbView) {
        if (currentAnimator != null) currentAnimator.cancel();

        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.root_layout).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        moviePosterExpanded.setVisibility(View.VISIBLE);

        moviePosterExpanded.setPivotX(0f);
        moviePosterExpanded.setPivotY(0f);

        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(moviePosterExpanded, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(moviePosterExpanded, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(moviePosterExpanded, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(moviePosterExpanded,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;

        final float startScaleFinal = startScale;
        moviePosterExpanded.setOnClickListener(view -> {
            if (currentAnimator != null) currentAnimator.cancel();

            AnimatorSet set1 = new AnimatorSet();
            set1.play(ObjectAnimator
                    .ofFloat(moviePosterExpanded, View.X, startBounds.left))
                    .with(ObjectAnimator
                            .ofFloat(moviePosterExpanded,
                                    View.Y, startBounds.top))
                    .with(ObjectAnimator
                            .ofFloat(moviePosterExpanded,
                                    View.SCALE_X, startScaleFinal))
                    .with(ObjectAnimator
                            .ofFloat(moviePosterExpanded,
                                    View.SCALE_Y, startScaleFinal));
            set1.setDuration(shortAnimationDuration);
            set1.setInterpolator(new DecelerateInterpolator());
            set1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    thumbView.setAlpha(1f);
                    moviePosterExpanded.setVisibility(View.GONE);
                    currentAnimator = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    thumbView.setAlpha(1f);
                    moviePosterExpanded.setVisibility(View.GONE);
                    currentAnimator = null;
                }
            });
            set1.start();
            currentAnimator = set1;
        });
    }
}
