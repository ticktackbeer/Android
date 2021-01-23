package com.example.halloworld.Model;

public class FriendRequest{
   String email;
   String nickName;
   public FriendRequest(String email,String nickName){

       this.email= email;
       this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
