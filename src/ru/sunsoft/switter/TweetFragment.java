package ru.sunsoft.switter;

import com.nostra13.universalimageloader.core.ImageLoader;

import twitter4j.Status;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetFragment extends Fragment {
    
    interface onTweetClickListener {
        void onTweetClick(Status tweet);
    }
    
    onTweetClickListener tweetListener;
    private Status status;
    private String userName, userPicLink, time, message;
    
    static ImageLoader imageLoader = ImageLoader.getInstance();

    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            tweetListener = (onTweetClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnArticleSelectedListener");
        }
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        userName = getArguments().getString("userName");
        userPicLink = getArguments().getString("userPicLink");
        time = getArguments().getString("time");
        message = getArguments().getString("message");
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rowView = inflater.inflate(R.layout.twit_view, container, false);
        
        
        if(status != null){
            ((TextView) rowView.findViewById(R.id.tvTwitMessage)).setText(status.getText());
            ((TextView) rowView.findViewById(R.id.tvUserName)).setText(status.getUser().getName());
            ((TextView) rowView.findViewById(R.id.tvTime)).setText(status.getCreatedAt().toString());
            ImageView userPic = ((ImageView)rowView.findViewById(R.id.ivUserpic));
            imageLoader.displayImage(status.getUser()
                    .getBiggerProfileImageURL(), userPic); 
            rowView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    tweetListener.onTweetClick(status);
                }
            });
            
        }
        return rowView;        
    }

}
