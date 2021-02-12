package com.example.halloworld.Utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.halloworld.Enum.LoginType;
import com.example.halloworld.Model.User;

public class UserLocalStore {

    public static  String SP_NAME = "userDetails";
    SharedPreferences userLocalDatabase;

    public UserLocalStore (Context context){
        userLocalDatabase= context.getSharedPreferences(SP_NAME,0);
    }

    public void storeUserData(User user){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString("name", user.getName());
        spEditor.putString("username", user.getUserName());
        spEditor.putString("email", user.getEmail());
        spEditor.putInt("age", user.getAge());
        spEditor.putString("password", user.getPassword());
        spEditor.putString("provider", user.getProvider());
        spEditor.putString("usertoken", user.getUserToken());
        spEditor.putString("nickname", user.getNickname());
        spEditor.commit();
    }
    public User getLoggedInUser(){
        String name = userLocalDatabase.getString("name","");
        String username = userLocalDatabase.getString("username","");
        String email = userLocalDatabase.getString("email","");
        int age = userLocalDatabase.getInt("age",-1);
        String password = userLocalDatabase.getString("password","");
        String provider = userLocalDatabase.getString("provider","");
        String nickname = userLocalDatabase.getString("nickname","");
        String userToken = userLocalDatabase.getString("usertoken","");

        User storedUser =  new User(name,username,email,age,password,provider,nickname,userToken);
        return storedUser;
    }
    public void setUserLoggedIn(boolean loggedIn){
        SharedPreferences.Editor spEditor= userLocalDatabase.edit();
        spEditor.putBoolean("LoggedIn",loggedIn);
        spEditor.commit();
    }
    public boolean isUserLoggedIn(){
        if(userLocalDatabase.getBoolean("LoggedIn",false)== true){
            return true;
        }else{
            return false;
        }
    }
    public void clearUserData(){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }
    public void storeLogintype(LoginType loginType){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.remove("loginType");
        spEditor.putString("loginTyp",loginType.toString());
        spEditor.commit();
    }

    public LoginType getLoginTyp(){
        String loginTypString = userLocalDatabase.getString("loginTyp","");
        return Enum.valueOf(LoginType.class,loginTypString);
    }
}
