package com.ahmedmabrook.ztwitterclient.Views.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.ahmedmabrook.ztwitterclient.R;
import com.ahmedmabrook.ztwitterclient.Utils.Twitter.TwitterClientHelper;
import com.ahmedmabrook.ztwitterclient.Views.Activities.Abstract.TwitterClientActivity;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

import static com.ahmedmabrook.ztwitterclient.Utils.Configs.TWITTER_KEY;
import static com.ahmedmabrook.ztwitterclient.Utils.Configs.TWITTER_SECRET;

public class LoginActivity extends TwitterClientActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "xW9JpChXvbCo5ig4uEecHloBr";
    private static final String TWITTER_SECRET = "wAt8gB1spodqzAeyp1lkR1KPVDfntcVokLpGVXhx48JyUifthN";

    @BindView(R.id.twitterLoginButton)
    TwitterLoginButton twitterLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkUserSession();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        twitterLoginButton.setText(R.string.login_with_twitter);
        twitterLoginButton.setCallback(twitterLoginCallback);

    }

    private void checkUserSession() {
        if (TwitterClientHelper.checkIfUserLoggedIn())
            navigateToMainPage();
    }

    private void navigateToMainPage() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    Callback<TwitterSession> twitterLoginCallback = new Callback<TwitterSession>() {
        @Override
        public void success(Result<TwitterSession> result) {
            navigateToMainPage();
        }

        @Override
        public void failure(TwitterException exception) {
            Toast.makeText(LoginActivity.this, getString(R.string.login_failed_msg), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }
}

