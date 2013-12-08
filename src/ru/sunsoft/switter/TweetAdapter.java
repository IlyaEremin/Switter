package ru.sunsoft.switter;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetAdapter extends ArrayAdapter<Tweet> {
    
    private final Context context;
    private List<Tweet> tweetList;

    public TweetAdapter(Context context, List<Tweet> list){
        super(context, R.layout.twit_view, list);
        this.context = context;
        this.tweetList = list;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.twit_view, parent, false);
        TextView tvMessage = (TextView) rowView.findViewById(R.id.tvTwitMessage);
        TextView tvTime = (TextView) rowView.findViewById(R.id.tvTime);
        TextView tvUsername = (TextView) rowView.findViewById(R.id.tvUserName);
        ImageView userPic = (ImageView) rowView.findViewById(R.id.ivUserpic);
        
        tvMessage.setText(tweetList.get(position).getMessage());
        tvTime.setText(tweetList.get(position).getDate());
        tvUsername.setText(tweetList.get(position).getUserName());
        
        ImageLoader imageLoader = ImageLoader.getInstance(); // Получили экземпляр
        imageLoader.init(ImageLoaderConfiguration.createDefault(context)); // Проинициализировали конфигом по умолчанию
        imageLoader.displayImage(tweetList.get(position).getUserpicUri(), userPic); // Запустили асинхронный показ картинки

        return rowView;
        
    }

}
