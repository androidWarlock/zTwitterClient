package com.ahmedmabrook.ztwitterclient.Views.Activities;

import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmedmabrook.ztwitterclient.Models.Follower;
import com.ahmedmabrook.ztwitterclient.R;
import com.ahmedmabrook.ztwitterclient.Utils.Helpers.GsonHelper;
import com.ahmedmabrook.ztwitterclient.Utils.Network;
import com.ahmedmabrook.ztwitterclient.Utils.Twitter.TwitterClientHelper;
import com.ahmedmabrook.ztwitterclient.Views.Activities.Abstract.TwitterClientActivity;
import com.bumptech.glide.Glide;
import com.google.gson.JsonElement;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ahmedmabrook.ztwitterclient.Utils.Configs.FOLLOWER_EXTRA;

public class UserProfileActivity extends TwitterClientActivity {

    @BindView(R.id.userProfileMainNestedScrollView)
    NestedScrollView mScrollView;

    @BindView(R.id.tweetsLinearLayout)
    LinearLayout tweetsLinearLayout;

    @BindView(R.id.userBannerImageView)
    ImageView backgroundImageView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.userProfileProgressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.noconnection)
    TextView noconnection;

    Follower mFollower;
    Tweet[] tweets;

    final int PAGESIZE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);

        tweets = new Tweet[0];

        mFollower = getIntent().getParcelableExtra(FOLLOWER_EXTRA);

        loadProfile();


    }

    private void loadProfile() {
        if (mFollower == null) {
            Toast.makeText(this, R.string.error_msg, Toast.LENGTH_LONG).show();
            finish();
        } else {

            mToolbar.setTitleTextColor(getResources().getColor(R.color.tw__solid_white));
            if (mFollower.getName() != null)
                mToolbar.setTitle(mFollower.getName());

            if (mFollower.getScreenName() != null)
                mToolbar.setSubtitle("@"+mFollower.getScreenName());

            if (mFollower.getBannerBackgroundUrl() != null && mFollower.getBannerBackgroundUrl().isEmpty() == false) {
                Glide.with(this)
                        .load(mFollower.getBannerBackgroundUrl())
                        .placeholder(R.color.colorPrimaryDark)
                        .centerCrop()
                        .into(backgroundImageView);
            }


            loadLastTenTweets();
        }
    }


    private void loadLastTenTweets() {
        if (Network.isConnectedToInternet(this)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    retrofit2.Response<JsonElement> res = TwitterClientHelper.GetUserTimeline(Long.parseLong(mFollower.getId()), PAGESIZE);
                    if (res.isSuccessful()) {
                        tweets = GsonHelper.parseUserTimeLineResponse(res.body().toString());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mScrollView.setPadding(0, 0, 0, 0);
                                for (int i = 0; i < tweets.length; i++) {

                                    tweetsLinearLayout.addView(new TweetView(UserProfileActivity.this, tweets[i]));
                                }

                                if (tweets.length == 0) {
                                    Toast.makeText(UserProfileActivity.this, R.string.no_tweets, Toast.LENGTH_SHORT).show();
                                }

                                mProgressBar.setVisibility(View.GONE);
                            }
                        });


                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserProfileActivity.this, getString(R.string.error_msg), Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);
                            }
                        });


                    }
                }
            }).start();


        } else {
            toastAndShowNoConnectionText();
        }
    }

    private void toastAndShowNoConnectionText() {

        Toast.makeText(UserProfileActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        noconnection.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);

    }
}
