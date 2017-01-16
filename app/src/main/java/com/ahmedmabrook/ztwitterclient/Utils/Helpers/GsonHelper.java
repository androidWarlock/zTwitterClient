package com.ahmedmabrook.ztwitterclient.Utils.Helpers;

import com.ahmedmabrook.ztwitterclient.Models.GetFollowersResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.twitter.sdk.android.core.models.Tweet;


/**
 * Created by Bassem Samy on 10/21/2016.
 */

public class GsonHelper {
    public static GetFollowersResponse parseUserFollowersResponse(JsonElement json) {
        GetFollowersResponse response;
        Gson gson = new Gson();
        try {
            response = gson.fromJson(json, GetFollowersResponse.class);
        } catch (Exception ex) {
            return null;
        }
        return response;
    }

    public static Tweet[] parseUserTimeLineResponse(String json) {
        try {
            Gson gson
                    = new Gson();
            Tweet[] tweets = gson.fromJson(json, Tweet[].class);
            if (tweets != null)
                return tweets;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new Tweet[0];
    }
}
