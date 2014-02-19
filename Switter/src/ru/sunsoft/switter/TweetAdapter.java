package ru.sunsoft.switter;

import java.util.List;

import twitter4j.Status;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class TweetAdapter extends ArrayAdapter<Status> {

    private final Context context;
    // private List<Status> tweetList;
    onTweetClickListener tweetListener;
    ImageLoader imageLoader = ImageLoader.getInstance();

    interface onTweetClickListener {
        void onTweetClick(Status tweet);
    }

    public TweetAdapter(Context context, List<Status> list) {
        super(context, R.layout.twit_view, list);
        this.context = context;
        try {
            tweetListener = (onTweetClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnArticleSelectedListener");
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder = null;
        View rowView = convertView;
        final Status currentTweet = getItem(position).isRetweet() ? getItem(
                position).getRetweetedStatus() : getItem(position);

        if (rowView == null) {
            mHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.twit_view, parent, false);

            mHolder.tvMessage = (TextView) rowView
                    .findViewById(R.id.tvTwitMessage);
            mHolder.tvTime = (TextView) rowView.findViewById(R.id.tvTime);
            mHolder.tvUsername = (TextView) rowView
                    .findViewById(R.id.tvUserName);
            mHolder.userPic = (ImageView) rowView.findViewById(R.id.ivUserpic);
            rowView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) rowView.getTag();
        }

        mHolder.tvMessage.setText(currentTweet.getText());
        mHolder.tvTime.setText(currentTweet.getCreatedAt().toString());
        mHolder.tvUsername.setText(currentTweet.getUser().getName());

        DisplayImageOptions.Builder b = new DisplayImageOptions.Builder();
        b.cacheInMemory(true);

        imageLoader.displayImage(currentTweet.getUser()
                .getBiggerProfileImageURL(), mHolder.userPic, b.build());
        rowView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                tweetListener.onTweetClick(currentTweet);
            }
        });

        return rowView;
    }

    private static class ViewHolder {
        TextView tvMessage, tvTime, tvUsername;
        ImageView userPic;
    }

}
