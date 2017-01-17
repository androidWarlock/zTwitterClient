package com.ahmedmabrook.ztwitterclient.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.ahmedmabrook.ztwitterclient.Models.Abstract.Entity;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by zMabrook on 16/1/2017.
 */

public class GetFollowersResponse extends Entity implements Parcelable {
    private String next_cursor;
    private String previous_cursor;

    @SerializedName("users")
    private ArrayList<Follower> followers;

    protected GetFollowersResponse(Parcel in) {
        next_cursor = in.readString();
        previous_cursor = in.readString();
        followers = new ArrayList<>();
        in.readTypedList(followers, Follower.CREATOR);
    }

    public GetFollowersResponse() {
        followers = new ArrayList<>();

    }

    public ArrayList<Follower> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<Follower> followers) {
        this.followers = followers;
    }

    public String getNext_cursor() {
        return next_cursor;
    }

    public void setNext_cursor(String next_cursor) {
        this.next_cursor = next_cursor;
    }

    public String getPrevious_cursor() {
        return previous_cursor;
    }

    public void setPrevious_cursor(String previous_cursor) {
        this.previous_cursor = previous_cursor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GetFollowersResponse> CREATOR = new Creator<GetFollowersResponse>() {
        @Override
        public GetFollowersResponse createFromParcel(Parcel in) {
            return new GetFollowersResponse(in);
        }

        @Override
        public GetFollowersResponse[] newArray(int size) {
            return new GetFollowersResponse[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(next_cursor);
        dest.writeString(previous_cursor);
        dest.writeTypedList(getFollowers());
    }

}
