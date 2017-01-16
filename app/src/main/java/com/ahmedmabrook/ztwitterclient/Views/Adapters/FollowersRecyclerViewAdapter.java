package com.ahmedmabrook.ztwitterclient.Views.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahmedmabrook.ztwitterclient.Models.Follower;
import com.ahmedmabrook.ztwitterclient.R;
import com.ahmedmabrook.ztwitterclient.Views.Activities.UserProfileActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.ahmedmabrook.ztwitterclient.Utils.Configs.FOLLOWER_EXTRA;

/**
 * Authored by Ahmed Mabrook - ahmed.mabrook@chestnut.com
 * On Jan 2017 .
 * FollowersRecyclerViewAdapter: Describtion goes here.
 */

public class FollowersRecyclerViewAdapter  extends RecyclerView.Adapter<FollowersRecyclerViewAdapter.ViewHolder> {

    ArrayList<Follower> mFollowers;
    private LayoutInflater mInflater = null;
    ViewHolder viewHolder;
    Context mContext;

    public FollowersRecyclerViewAdapter(Context mContext, ArrayList<Follower> mFollowers) {
        this.mFollowers = mFollowers;
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_follower, parent, false);
        viewHolder = new ViewHolder(view);

    return  viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        viewHolder.emptyViewHolder();
        Follower follower = mFollowers.get(position);

        if (follower.getProfileImageUrl() !=null){
            Glide.with(mContext)
                    .load(follower.getProfileImageUrl())
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.mipmap.avatar_template)
                    .into(viewHolder.avatarImageView);
        }

        if (follower.getName()!=null){
            viewHolder.followerNameTextView.setText(follower.getName());
        }

        if (follower.getScreenName()!=null){
            viewHolder.followerHandlerTextView.setText(follower.getScreenName());
        }

        if (follower.getBio()!=null){
            viewHolder.followerBioTextView.setVisibility(View.VISIBLE);
            viewHolder.followerBioTextView.setText(follower.getBio());
        }


    }

    @Override
    public int getItemCount() {
        if (mFollowers != null)
            return mFollowers.size();

        return 0;    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.avatarImageView)
        CircleImageView avatarImageView;

        @BindView(R.id.followerNameTextView)
        TextView followerNameTextView;

        @BindView(R.id.followerHandlerTextView)
        TextView followerHandlerTextView;

        @BindView(R.id.followerBioTextView)
        TextView followerBioTextView;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, UserProfileActivity.class);
                    intent.putExtra(FOLLOWER_EXTRA, mFollowers.get(getLayoutPosition()));
                    mContext.startActivity(intent);
                }
            });

        }

        private void emptyViewHolder() {
            followerNameTextView.setText("");
            followerHandlerTextView.setText("");
            followerBioTextView.setText("");

            avatarImageView.setImageBitmap(null);

            followerBioTextView.setVisibility(View.GONE);

        }


    }

}
