package ru.sunsoft.switter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import oauth.signpost.OAuth;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends ListActivity {
    private SharedPreferences prefs;
    private List<Tweet> tweetList;
    private final Handler mTwitterHandler = new Handler();

    final Runnable mUpdateTwitterNotification = new Runnable() {
        public void run() {
            Toast.makeText(getBaseContext(), "Tweet sent!", Toast.LENGTH_LONG)
                    .show();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        getActionBar().setDisplayShowTitleEnabled(false);
        new DownloadTimeline(this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newMessage:
                goToNewMessageScreen();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToNewMessageScreen() {
        Intent i = new Intent(this, NewMessageActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
    }

    private class DownloadTimeline extends
            AsyncTask<Void, Void, List<twitter4j.Status>> {

        Context context;

        public DownloadTimeline(Context context) {
            this.context = context;
        }

        @Override
        protected List<twitter4j.Status> doInBackground(Void... params) {

            String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
            String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");

            AccessToken a = new AccessToken(token, secret);
            Twitter twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer(Constants.CONSUMER_KEY,
                    Constants.CONSUMER_SECRET);
            twitter.setOAuthAccessToken(a);
            List<twitter4j.Status> statuses = null;
            try {
                statuses = twitter.getHomeTimeline();
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return statuses;
        }

        @Override
        protected void onPostExecute(List<twitter4j.Status> result) {
            tweetList = new ArrayList<Tweet>();
            for (twitter4j.Status status : result) {
                tweetList.add(new Tweet(status.getUser().getName(), status
                        .getText(), status.getCreatedAt().toString(), status.getUser().getBiggerProfileImageURL()));
            }
            TweetAdapter ta = new TweetAdapter(context, tweetList);
            setListAdapter(ta);
        }

    }

    private String getTweetMsg() {
        return "Tweeting from Android App at " + new Date().toLocaleString();
    }

    public void sendTweet() {
        Thread t = new Thread() {
            public void run() {

                try {
                    TwitterUtils.sendTweet(prefs, getTweetMsg());
                    mTwitterHandler.post(mUpdateTwitterNotification);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        };
        t.start();
    }

    private void clearCredentials() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        final Editor edit = prefs.edit();
        edit.remove(OAuth.OAUTH_TOKEN);
        edit.remove(OAuth.OAUTH_TOKEN_SECRET);
        edit.commit();
    }

}

// Здравствуйте, Илья.
// Спасибо за интерес к нашей вакансии.
// Посылаю Вам тестовое задание:
//
//
// Нужно сделать apk файлик со следующими скринами, например, сделать 3 экрана:
//
// * авторизация
// * экран с лентой авторизованного юзера
// TODO * экран со списком твитов юзера, по которому кликнули в ленте
// TODO + можно асинхронно подгружать аватарки в список
// + можно диалог с созданием нового твита
//
// Спасибо,
// Лейсан, Flatstack

