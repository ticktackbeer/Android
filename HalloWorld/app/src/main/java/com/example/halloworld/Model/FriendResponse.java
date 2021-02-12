package com.example.halloworld.Model;

public class FriendResponse{
    String email;
    String nickName;

    public FriendResponse(String email,String nickName){

        this.email=email;
        this.nickName=nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
