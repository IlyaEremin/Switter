package ru.sunsoft.switter;

public class Tweet {

    private String message, userName, date, userpicUri;
    
    public Tweet(String userName, String message, String date, String userPic){
        this.userName = userName;
        this.message = message;
        this.date = date;
        this.userpicUri = userPic;
    }

    public String getMessage() {
        return message;
    }

    public String getUserName() {
        return userName;
    }

    public String getDate() {
        return date;
    }
    
    public String getUserpicUri(){
        return userpicUri;
    }

    
}
