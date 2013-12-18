package ru.sunsoft.switter;

import java.util.ArrayList;
import java.util.List;

import oauth.signpost.OAuth;
import ru.sunsoft.switter.NewTweetDialog.NewTweetDialogListener;
import ru.sunsoft.switter.TweetAdapter.onTweetClickListener;
import twitter4j.Status;
import android.app.FragmentManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Toast;

public class MainActivity extends ListActivity implements onTweetClickListener,
        NewTweetDialogListener, TaskFragment.TaskCallbacks {

    private SharedPreferences prefs;
    private TweetAdapter ta;
    private long id;
    private int page = 1;
    private int userStatusesCount;
    private TaskFragment mTaskFragment;
    private MenuItem refreshButton;

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

        FragmentManager fm = getFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag("task");

        if (getIntent() != null) {
            this.id = getIntent().getLongExtra("userId",
                    prefs.getLong("id", -1));
        }

        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setIcon(R.drawable.home);
        ta = new TweetAdapter(this, new ArrayList<Status>());

        if (mTaskFragment == null) {
            mTaskFragment = new TaskFragment();
            Bundle b = new Bundle();
            b.putLong("userId", id);
            mTaskFragment.setArguments(b);
            fm.beginTransaction().add(mTaskFragment, "task").commit();
        } else {
            ta.addAll(mTaskFragment.getTweetList());
            userStatusesCount = mTaskFragment.getUserCount();
        }

        setListAdapter(ta);

        getListView().setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount)
                        && !mTaskFragment.isRunning()) {
                    runOnUiThread(loadMoreListItems);
                }
            }
        });
    }

    // Runnable to load the items
    private Runnable loadMoreListItems = new Runnable() {
        @Override
        public void run() {

            if (page * 20 < userStatusesCount) {
                page++;
                mTaskFragment.loadMoreTweets(page);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        System.out.println("onCreateOptMenu");
        getMenuInflater().inflate(R.menu.main, menu);
        refreshButton = menu.findItem(R.id.refresh);
        if (mTaskFragment.isRunning()) {
            refreshButton.setIcon(R.drawable.cancel);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newMessage:
                goToNewMessageScreen();
                return true;
            case R.id.Exit:
                clearCredentials();
                return true;
            case R.id.refresh:
                onRefresh();
                return true;
            case android.R.id.home:
                openHomeTimeline();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onRefresh() {
        if (!mTaskFragment.isRunning()) {
            clearScreen();
            mTaskFragment.loadMoreTweets(1);
        } else {
            mTaskFragment.cancelDownload();
        }
    }

    private void redrawRefreshButton() {
        if (refreshButton == null)
            return;
        if (mTaskFragment.isRunning()) {
            refreshButton.setIcon(R.drawable.cancel);
        } else
            refreshButton.setIcon(R.drawable.refresh);
    }

    private void openHomeTimeline() {
        long authUserid = prefs.getLong("currentUserId", -1);
        if (authUserid == -1)
            return;
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("id", authUserid);
        startActivity(i);
    }

    private void goToNewMessageScreen() {
        new NewTweetDialog().show(getFragmentManager(), "tag");
    }

    private void clearCredentials() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        final Editor edit = prefs.edit();
        edit.remove(OAuth.OAUTH_TOKEN);
        edit.remove(OAuth.OAUTH_TOKEN_SECRET);
        edit.commit();
        clearScreen();
    }

    private void clearScreen() {
        ta.clear();
        ta.notifyDataSetChanged();
        page = 1;
        mTaskFragment.clearTweets();
    }

    @Override
    public void onTweetClick(Status tweet) {
        openUserTimeline(tweet.getUser().getId());
    }

    public void openUserTimeline(long id) {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.putExtra("userId", id);
        startActivity(i);
    }

    @Override
    public void onSend(final String message) {
        Thread t = new Thread() {
            public void run() {

                try {
                    TwitterUtils.sendTweet(prefs, message);
                    mTwitterHandler.post(mUpdateTwitterNotification);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        t.start();
    }

    @Override
    public void onPreExecute() {
        redrawRefreshButton();
    }

    @Override
    public void onCancelled() {
        redrawRefreshButton();
        Toast.makeText(this, getString(R.string.network_error),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPostExecute(List<twitter4j.Status> result) {
        redrawRefreshButton();
        userStatusesCount = mTaskFragment.getUserCount();
        ta.addAll(result);
        ta.notifyDataSetChanged();
    }

    @Override
    public void onError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
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
// * экран со списком твитов юзера, по которому кликнули в ленте
// + можно асинхронно подгружать аватарки в список
// + можно диалог с созданием нового твита

// проверка подключения к интернету
// проверить переходы по экранам
// проверять авторизацию перед экранной лентой
// анимации загрузки и проч
// TODO refactor
//
// Спасибо,
// Лейсан, Flatstack

