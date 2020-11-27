package com.example.halloworld.Model;

import com.example.halloworld.Enum.LoginType;

import java.io.Serializable;

public class User implements Serializable {

    private String name;
    private int age;
    private String userName;
    private String email;
    private String password;
    private String provider;
    private String nickname;
    private String userToken;

    public User() {

    }

    public User(String name, String userName, String email, int age, String password, String provider, String nickname, String userToken) {
        this.name = name;
        this.userName = userName;
        this.email = email;
        this.age = age;
        this.password = password;
        this.provider = provider;
        this.nickname= nickname;
        this.userToken=userToken;
    }

    public User(String email, String password) {
        this.name = "";
        this.userName = "";
        this.email = email;
        this.age = -1;
        this.password = password;
        this.provider = LoginType.email.toString();
        this.nickname ="";
        this.userToken= "";
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUserToken() { return userToken; }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

}
