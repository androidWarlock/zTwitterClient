package com.ahmedmabrook.ztwitterclient.Views.Activities;

import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmedmabrook.ztwitterclient.Models.Follower;
import com.ahmedmabrook.ztwitterclient.Models.GetFollowersResponse;
import com.ahmedmabrook.ztwitterclient.R;
import com.ahmedmabrook.ztwitterclient.Utils.Helpers.GsonHelper;
import com.ahmedmabrook.ztwitterclient.Utils.Network;
import com.ahmedmabrook.ztwitterclient.Utils.Twitter.TwitterClientHelper;
import com.ahmedmabrook.ztwitterclient.Views.Activities.Abstract.TwitterClientActivity;
import com.ahmedmabrook.ztwitterclient.Views.Adapters.FollowersRecyclerViewAdapter;
import com.ahmedmabrook.ztwitterclient.Views.CustomViews.EmptyRecyclerView;
import com.google.gson.JsonElement;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    LinearLayoutManager linearLayoutManager;
    ArrayList<Follower> mFollowers;
    FollowersRecyclerViewAdapter mAdapter;
    final static int PAGESIZE = 30;
    int visibleItemCount;
    int totalItemCount;
    int pastVisiblesItems;
    String cursor= "-1";
    public boolean infiniteScrollingLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        linearLayoutManager = new LinearLayoutManager(this);

        showLoadingBar();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setEmptyView(noFollowersTextView);


        if (Network.isConnectedToInternet(this)) {
            final retrofit2.Response<JsonElement> res = TwitterClientHelper.GetFollowers(TwitterClientHelper.GetCurrentUserId(), PAGESIZE, cursor);
            if (res.isSuccessful()) {
                final GetFollowersResponse response = GsonHelper.parseUserFollowersResponse(res.body());

                mAdapter = new FollowersRecyclerViewAdapter(HomeActivity.this, response.getFollowers());
                recyclerView.setAdapter(mAdapter);
                recyclerView.setHasFixedSize(true);
                hideLoadingBar();



            }else{
                Toast.makeText(this, getString(R.string.error_msg), Toast.LENGTH_SHORT).show();
            }

        }
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

    }




}
