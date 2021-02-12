package com.example.halloworld.Model;

public class Friend {

    String email;
    String userToken;
    String nickName;

    public Friend(String email, String userToken, String nickName){

        this.email= email;
        this.userToken= userToken;
        this.nickName=nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
