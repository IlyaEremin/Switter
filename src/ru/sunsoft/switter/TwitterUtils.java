package ru.sunsoft.switter;

import oauth.signpost.OAuth;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.content.SharedPreferences;

public class TwitterUtils {
    
    public static boolean isAuthenticated(SharedPreferences prefs) throws TwitterException {

        String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
        String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");

        AccessToken a = new AccessToken(token, secret);
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(Constants.CONSUMER_KEY,
                Constants.CONSUMER_SECRET);
        twitter.setOAuthAccessToken(a);

        try {
            twitter.getId();
            return true;
        } catch (TwitterException e) {
            System.out.println(e.toString());
            System.out.println("XYU" + e.getErrorCode());
            if(e.getErrorCode() == 88){
                throw e;
            }
            return false;
        }
    }

    public static void sendTweet(SharedPreferences prefs, String msg)
            throws Exception {
        String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
        String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");

        AccessToken a = new AccessToken(token, secret);
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(Constants.CONSUMER_KEY,
                Constants.CONSUMER_SECRET);
        twitter.setOAuthAccessToken(a);
        twitter.updateStatus(msg);
    }

}
