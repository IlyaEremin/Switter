package ru.sunsoft.switter;

import java.util.ArrayList;
import java.util.List;

import oauth.signpost.OAuth;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.app.Fragment;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;

public class TaskFragment extends Fragment {
    /**
     * Callback interface through which the fragment will report the task's
     * progress and results back to the Activity.
     */
    static interface TaskCallbacks {
        void onPreExecute();

        void onCancelled();

        void onPostExecute(List<twitter4j.Status> result);

        void onError(String errorMessage);
    }

    private TaskCallbacks mCallbacks;
    private LoadUserTimeline mTask;
    private long id;
    private int userStatusesCount;
    private List<Status> tweetList;
    private boolean mRunning;

    /**
     * Hold a reference to the parent Activity so we can report the task's
     * current progress and results. The Android framework will pass us a
     * reference to the newly created Activity after each configuration change.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (TaskCallbacks) activity;
    }

    public boolean isRunning() {
        return mRunning;
    }

    /**
     * This method will only be called once when the retained Fragment is first
     * created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        tweetList = new ArrayList<Status>();
        this.id = getArguments().getLong("userId");
        loadMoreTweets(1);
    }

    public void loadMoreTweets(int page) {
        if(!isRunning()){
            mTask = new LoadUserTimeline(page);
            mTask.execute();
        }
    }

    /**
     * Set the callback to null so we don't accidentally leak the Activity
     * instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public List<Status> getTweetList() {
        return tweetList;
    }

    public void clearTweets() {
        tweetList.clear();
    }

    public void cancelDownload() {
        if(isRunning()){
            mTask.cancel(true);
        }
    }

    /**
     * A dummy task that performs some (dumb) background work and proxies
     * progress updates and results back to the Activity.
     * 
     * Note that we need to check if the callbacks are null in each method in
     * case they are invoked after the Activity's and Fragment's onDestroy()
     * method have been called.
     */
    private class LoadUserTimeline extends
            AsyncTask<Void, Void, List<twitter4j.Status>> {

        private int page;
        private String errorMessage = "";

        @Override
        protected void onPreExecute() {
            mRunning = true;
            if (mCallbacks != null) {
                mCallbacks.onPreExecute();
            }
        }

        public LoadUserTimeline(int page) {
            this.page = page;
        }

        /**
         * Note that we do NOT call the callback object's methods directly from
         * the background thread, as this could result in a race condition.
         */
        @Override
        protected List<twitter4j.Status> doInBackground(Void... ignore) {
            if (!NetworkUtils.isNetAvailable((Activity) mCallbacks)) {
                cancel(true);
                return null;
            }
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences((Activity) mCallbacks);
            List<twitter4j.Status> statuses = new ArrayList<twitter4j.Status>();
            try {
                if (TwitterUtils.isAuthenticated(prefs)) {
                    String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
                    String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET,
                            "");

                    AccessToken a = new AccessToken(token, secret);
                    Twitter twitter = new TwitterFactory().getInstance();
                    twitter.setOAuthConsumer(Constants.CONSUMER_KEY,
                            Constants.CONSUMER_SECRET);
                    twitter.setOAuthAccessToken(a);
                    try {
                        if (id == -1) {
                            id = twitter.getId();
                        }
                        statuses = twitter
                                .getUserTimeline(id, new Paging(page));

                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                    userStatusesCount = statuses.get(0).getUser()
                            .getStatusesCount();
                } else {
                    Intent i = new Intent(
                            ((Activity) mCallbacks).getApplicationContext(),
                            PrepareRequestTokenActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            } catch (TwitterException e) {
                errorMessage = e.getErrorMessage();
            }
            return statuses;
        }

        @Override
        protected void onCancelled() {
            mRunning = false;
            if (mCallbacks != null) {
                mCallbacks.onCancelled();
            }
        }

        @Override
        protected void onPostExecute(List<twitter4j.Status> result) {
            mRunning = false;
            if (mCallbacks != null) {
                if(!errorMessage.isEmpty()){
                    mCallbacks.onError(errorMessage);
                }
                tweetList.addAll(result);
                mCallbacks.onPostExecute(result);
            }

        }
    }

    public int getUserCount() {
        return userStatusesCount;
    }
}
