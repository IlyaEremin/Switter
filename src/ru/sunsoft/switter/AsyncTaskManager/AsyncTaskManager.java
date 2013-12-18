package ru.sunsoft.switter.AsyncTaskManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

public final class AsyncTaskManager implements IProgressTracker,
        OnCancelListener {

    private final OnTaskCompleteListener mTaskCompleteListener;
    private final ProgressDialog mProgressDialog;
    private Task mAsyncTask;

    public AsyncTaskManager(Context context,
            OnTaskCompleteListener taskCompleteListener) {
        // Save reference to complete listener (activity)
        mTaskCompleteListener = taskCompleteListener;
        // Setup progress dialog
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(this);
    }

    public void setupTask(Task asyncTask) {
        // Keep task
        mAsyncTask = asyncTask;
        // Wire task to tracker (this)
        mAsyncTask.setProgressTracker(this);
        // Start task
        mAsyncTask.execute();
    }

    @Override
    public void onProgress(String message) {
        // Show dialog if it wasn't shown yet or was removed on configuration
        // (rotation) change
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
        // Show current message in progress dialog
        mProgressDialog.setMessage(message);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // Cancel task
        mAsyncTask.cancel(true);
        // Notify activity about completion
        mTaskCompleteListener.onTaskComplete(mAsyncTask);
        // Reset task
        mAsyncTask = null;
    }

    @Override
    public void onComplete() {
        // Close progress dialog
        mProgressDialog.dismiss();
        // Notify activity about completion
        mTaskCompleteListener.onTaskComplete(mAsyncTask);
        // Reset task
        mAsyncTask = null;
    }

    public Object retainTask() {
        // Detach task from tracker (this) before retain
        if (mAsyncTask != null) {
            mAsyncTask.setProgressTracker(null);
        }
        // Retain task
        return mAsyncTask;
    }

    public void handleRetainedTask(Object instance) {
        // Restore retained task and attach it to tracker (this)
        if (instance instanceof Task) {
            mAsyncTask = (Task) instance;
            mAsyncTask.setProgressTracker(this);
        }
    }

    public boolean isWorking() {
        // Track current status
        return mAsyncTask != null;
    }
}