package com.example.leeje.androidpresentsystem;

/**
 * Created by leeje on 2018-05-11.
 */

public class groupData {
    private String userName;
    private String message;

    public groupData(){}
    public groupData(String userName, String message){
        this.userName=userName;
        this.message=message;
    }
    public void setUserName(String userName){

        this.userName=userName;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }
}
