package com.ahmedmabrook.ztwitterclient.Views.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ahmedmabrook.ztwitterclient.R;
import com.ahmedmabrook.ztwitterclient.Views.Activities.Abstract.TwitterClientActivity;

public class UserProfileActivity extends TwitterClientActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
    }
}
