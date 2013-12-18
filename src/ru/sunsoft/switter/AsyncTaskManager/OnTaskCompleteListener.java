package ru.sunsoft.switter.AsyncTaskManager;

public interface OnTaskCompleteListener {
    // Notifies about task completeness
    void onTaskComplete(Task task);
}