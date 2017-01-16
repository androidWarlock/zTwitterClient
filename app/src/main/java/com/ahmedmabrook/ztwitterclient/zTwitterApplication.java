package com.ahmedmabrook.ztwitterclient;

import android.app.Application;

import com.ahmedmabrook.ztwitterclient.Utils.Twitter.TwitterClientHelper;

/**
 * Authored by Ahmed Mabrook - ahmed.mabrook@chestnut.com
 * On Jan 2017 .
 * zTwitterApplication: Describtion goes here.
 */

public class zTwitterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterClientHelper.initializeTwitter(this);

    }
}
