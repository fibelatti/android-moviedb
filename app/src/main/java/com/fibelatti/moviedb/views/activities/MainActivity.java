package com.fibelatti.moviedb.views.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.fibelatti.moviedb.R;
import com.fibelatti.moviedb.presenters.IMainPresenter;
import com.fibelatti.moviedb.presenters.MainPresenter;
import com.fibelatti.moviedb.views.adapters.MoviesAdapter;
import com.fibelatti.moviedb.views.extensions.RecyclerTouchListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity
        extends AppCompatActivity {

    private Context context;
    private IMainPresenter presenter;
    private MoviesAdapter moviesAdapter;

    private Subscription searchSubscription;

    private boolean isLoading;
    int pastVisibleItems, visibleItemCount, totalItemCount;

    //region layout bindings
    @BindView(R.id.root_layout)
    CoordinatorLayout rootLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.recycler_view_movies)
    RecyclerView recyclerViewMovies;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        presenter = MainPresenter.createPresenter(this);
        moviesAdapter = new MoviesAdapter(this);

        presenter.onCreate();

        setUpLayout();
        setUpRecyclerView();
        setUpValues();

        refreshData();
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

        if (!searchSubscription.isUnsubscribed()) searchSubscription.unsubscribe();
    }

    private void setUpLayout() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }

    private void setUpValues() {
        setTitle(getString(R.string.main_title));
    }

    private void setUpRecyclerView() {
        swipeRefresh.setOnRefreshListener(this::refreshData);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerViewMovies.setLayoutManager(linearLayoutManager);
        recyclerViewMovies.setItemAnimator(new DefaultItemAnimator());
        recyclerViewMovies.setAdapter(moviesAdapter);

        recyclerViewMovies.addOnItemTouchListener(new RecyclerTouchListener.Builder(this)
                .setOnItemTouchListener((view, position) -> presenter.goToMovie(moviesAdapter.getMovieByIndex(position)))
                .build());

        recyclerViewMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = linearLayoutManager.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();

                if (!isLoading && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                    fetchNextPage();
                }
            }
        });
    }

    private void refreshData() {
        if (searchSubscription != null && !searchSubscription.isUnsubscribed())
            searchSubscription.unsubscribe();

        searchSubscription = presenter.refresh()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> {
                    moviesAdapter.clearMovieList();

                    swipeRefresh.setRefreshing(false);
                    isLoading = true;
                    moviesAdapter.showLoadingItem();
                })
                .doOnCompleted(() -> {
                    isLoading = false;
                    moviesAdapter.hideLoadingItem();
                    moviesAdapter.notifyDataSetChanged();
                })
                .subscribe(
                        movie -> moviesAdapter.addMovieToList(movie),
                        throwable -> Log.e("Error", throwable.getMessage())
                );
    }

    private void fetchNextPage() {
        if (searchSubscription != null && !searchSubscription.isUnsubscribed())
            searchSubscription.unsubscribe();

        searchSubscription = presenter.getNextPage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> {
                    isLoading = true;
                    moviesAdapter.showLoadingItem();
                })
                .doOnCompleted(() -> {
                    isLoading = false;
                    moviesAdapter.hideLoadingItem();
                    moviesAdapter.notifyDataSetChanged();
                })
                .subscribe(
                        movie -> moviesAdapter.addMovieToList(movie),
                        throwable -> Log.e("Error", throwable.getMessage())
                );
    }
}
