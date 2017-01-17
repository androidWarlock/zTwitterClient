package com.ahmedmabrook.ztwitterclient.Views.Activities;

import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmedmabrook.ztwitterclient.Database.DatabaseHelper;
import com.ahmedmabrook.ztwitterclient.Models.Follower;
import com.ahmedmabrook.ztwitterclient.Models.GetFollowersResponse;
import com.ahmedmabrook.ztwitterclient.R;
import com.ahmedmabrook.ztwitterclient.Utils.Helpers.GsonHelper;
import com.ahmedmabrook.ztwitterclient.Utils.Language;
import com.ahmedmabrook.ztwitterclient.Utils.Network;
import com.ahmedmabrook.ztwitterclient.Utils.Twitter.TwitterClientHelper;
import com.ahmedmabrook.ztwitterclient.Views.Activities.Abstract.TwitterClientActivity;
import com.ahmedmabrook.ztwitterclient.Views.Adapters.FollowersRecyclerViewAdapter;
import com.ahmedmabrook.ztwitterclient.Views.CustomViews.EmptyRecyclerView;
import com.google.gson.JsonElement;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;

public class HomeActivity extends TwitterClientActivity {

    @BindView(R.id.followersRecyclerView)
    EmptyRecyclerView recyclerView;

    @BindView(R.id.followerSwipeRefreshLayout)
    SwipeRefreshLayout followerSwipeRefreshLayout;

    @BindView(R.id.loadingProgressBar)
    ProgressBar loadingProgressBar;

    @BindView(R.id.noFollowersTextView)
    TextView noFollowersTextView;

    @BindView(R.id.trloadingBar)
    TextView loadingBar;

    @BindView(R.id.noconnection)
    TextView noconnection;

    @BindView(R.id.changeLanguage)
    Button changeLanguageButton;

    LinearLayoutManager linearLayoutManager;
    ArrayList<Follower> mFollowers;
    FollowersRecyclerViewAdapter mAdapter;
    final static int PAGESIZE = 30;
    String cursor = "-1";
    private int mFirstVisibleItem, mVisibleItemCount, mTotalItemCount, mLastFirstVisibleItem;
    public boolean isLoading = false;
    private static boolean gotToEnd = false;
    GetFollowersResponse response;
    private DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Language.loadLanguage(this);

        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        changeLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Language.changeLanguage(HomeActivity.this);
            }
        });
        dbHelper = new DatabaseHelper(this);

        linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setEmptyView(noFollowersTextView);

        followerSwipeRefreshLayout.setOnRefreshListener(swipRefreshListener);

        startLoadingFollowers();


    }


    private void startLoadingFollowers() {
        showLoadingBar();

        if (Network.isConnectedToInternet(this)) {
            isLoading =true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Response<JsonElement> res = TwitterClientHelper.GetFollowers(TwitterClientHelper.GetCurrentUserId(), PAGESIZE, cursor);
                    if (res.isSuccessful()) {
                        response = GsonHelper.parseUserFollowersResponse(res.body());
                        updateDatabase(response);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                isLoading = false;
                                mFollowers = response.getFollowers();

                                mAdapter = new FollowersRecyclerViewAdapter(HomeActivity.this, response.getFollowers());
                                recyclerView.setAdapter(mAdapter);
                                recyclerView.setHasFixedSize(true);
                                hideLoadingBar();

                                recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                        super.onScrolled(recyclerView, dx, dy);
                                        mVisibleItemCount = recyclerView.getChildCount();
                                        mTotalItemCount = linearLayoutManager.getItemCount();
                                        mFirstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                                        //checking if no more results to get
                                        if (gotToEnd) {
                                            return;
                                        }

                                        int lastInScreen = mFirstVisibleItem + mVisibleItemCount;

                                        //check if got to last item and not loading and the item count is bigger than default:20
                                        if ((lastInScreen == mTotalItemCount) && !(isLoading) && (mTotalItemCount >= 30) && !gotToEnd) {
                                            cursor = response.getNext_cursor();
                                            loadMoreFollowers();

                                        }

                                    }
                                });

                            }
                        });
                    } else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(HomeActivity.this, getString(R.string.error_msg), Toast.LENGTH_SHORT).show();
                                hideLoadingBar();

                            }
                        });
                    }


                }
            }).start();

        } else {
            loadOffline();
            toastAndShowNoConnectionText();
            hideLoadingBar();

        }


    }

    private void loadMoreFollowers() {

        if (Network.isConnectedToInternet(this)) {
            isLoading = true;
            showLoadingBar();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    final retrofit2.Response<JsonElement> res = TwitterClientHelper.GetFollowers(TwitterClientHelper.GetCurrentUserId(), PAGESIZE, cursor);
                    if (res !=null && res.isSuccessful()) {
                        response = GsonHelper.parseUserFollowersResponse(res.body());
                        updateDatabase(response);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                mFollowers.addAll(response.getFollowers());

                                mAdapter.add(response.getFollowers());
                                hideLoadingBar();
                                isLoading = false;

                            }
                        });
                    } else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(HomeActivity.this, getString(R.string.error_msg), Toast.LENGTH_SHORT).show();
                                hideLoadingBar();
                                isLoading = false;

                            }
                        });
                    }
                }
            }).start();
        } else {

            toastAndShowNoConnectionText();
            isLoading = false;


        }

    }

    private void updateDatabase(final GetFollowersResponse response) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Dao<Follower, Integer> followersDao = dbHelper.getFollowerDao();
                    followersDao.callBatchTasks(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            if (response != null && response.getFollowers() != null) {
                                for (Follower f : response.getFollowers())
                                    followersDao.createOrUpdate(f);
                            }
                            return null;
                        }
                    });


                } catch (Exception ex) {
                    Log.e("save followers", ex.toString());
                }
            }
        }).start();

    }

    private void loadOffline() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Dao<Follower, Integer> followersDao = dbHelper.getFollowerDao();
                    int totalNumber = (int) followersDao.countOf();
                    QueryBuilder<Follower, Integer> builder = followersDao.queryBuilder();

                    if (response == null){
                        response = new GetFollowersResponse();
                        builder.offset((long) response.getFollowers().size()).limit((long) PAGESIZE);

                    }else {
                        builder.offset((long) response.getFollowers().size()).limit((long) PAGESIZE);
                    }

                    response.setFollowers((ArrayList<Follower>) followersDao.query(builder.prepare()));

                    if ((response.getFollowers().size() + PAGESIZE) >= totalNumber)
                        response.setNext_cursor("0");
                    else
                        response.setNext_cursor("-1");


                    if (mAdapter == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mFollowers = response.getFollowers();

                                mAdapter = new FollowersRecyclerViewAdapter(HomeActivity.this, response.getFollowers());
                                recyclerView.setAdapter(mAdapter);
                                recyclerView.setHasFixedSize(true);
                                hideLoadingBar();

                                recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                        super.onScrolled(recyclerView, dx, dy);
                                        mVisibleItemCount = recyclerView.getChildCount();
                                        mTotalItemCount = linearLayoutManager.getItemCount();
                                        mFirstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                                        //checking if no more results to get
                                        if (gotToEnd) {
                                            return;
                                        }

                                        int lastInScreen = mFirstVisibleItem + mVisibleItemCount;

                                        //check if got to last item and not loading and the item count is bigger than default:30
                                        if ((lastInScreen == mTotalItemCount) && !(isLoading) && (mTotalItemCount >= 30) && !gotToEnd) {
                                            cursor = response.getNext_cursor();
                                            isLoading = true;
                                            loadOffline();

                                        }

                                    }
                                });
                            }
                        });

                    } else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mFollowers.addAll(response.getFollowers());

                                mAdapter.add(response.getFollowers());
                                hideLoadingBar();
                                isLoading = false;
                            }
                        });

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(HomeActivity.this, getString(R.string.error_msg), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).start();


    }


    private SwipeRefreshLayout.OnRefreshListener swipRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            cursor = "-1";

            if (Network.isConnectedToInternet(HomeActivity.this)) {

                startLoadingFollowers();

            } else {

                toastAndShowNoConnectionText();


            }
        }
    };

    private void toastAndShowNoConnectionText() {
        Toast.makeText(HomeActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        noconnection.setVisibility(View.VISIBLE);
        hideLoadingBar();
    }

    private void showLoadingBar() {
        loadingBar.setText("Loading...");
        loadingBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        loadingBar.setVisibility(View.VISIBLE);
        loadingBar
                .animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(1));
    }

    private void hideLoadingBar() {
        loadingBar.setText("Loading...");
        loadingBar
                .animate()
                .translationY(loadingBar.getHeight())
                .setInterpolator(new AccelerateInterpolator(1));
        loadingBar.setVisibility(View.GONE);

        if (followerSwipeRefreshLayout.isRefreshing())
            followerSwipeRefreshLayout.setRefreshing(false);

    }

}
