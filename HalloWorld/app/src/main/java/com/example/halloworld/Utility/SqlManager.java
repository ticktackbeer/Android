package com.example.halloworld.Utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.halloworld.Interface.GetUserCallback;
import com.example.halloworld.Model.User;
import com.example.halloworld.Enum.UserState;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class SqlManager {
    ProgressDialog progressDialog;
    public static final int CONNECTION_TIMEOUT = 1* 15;
    String Res;
    String selectStatement;

    public SqlManager (Context context){
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
    }

    public void storeUserDataInBackground(User user, GetUserCallback userCallback){
        progressDialog.show();
        new StoreUserDataAsyncTask(user,userCallback).execute();
    }

    public void fetchUserDataInBackground(User user, GetUserCallback callback){
        progressDialog.show();
        new FetchUserDataAsyncTask(user,callback).execute();
    }


    public class StoreUserDataAsyncTask extends AsyncTask<Void,Void,Void> {
        User user;
        GetUserCallback userCallback;
        Boolean isSuccessful=false;

        public StoreUserDataAsyncTask(User user, GetUserCallback userCallback){
        this.user = user;
        this.userCallback=userCallback;
        setStatement(user);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Connection con = new ConnectionClass().Connection();

                Statement statement = con.createStatement();
                statement.setQueryTimeout(CONNECTION_TIMEOUT);
                // result=1 --> hat funktioniert , result=0 --> fehler
                int  result =statement.executeUpdate(selectStatement);
                if(result==1){
                    isSuccessful=true;
                }
                if(con!=null){
                    con.close();
                }
            }
            catch (Exception e){
                String error = e.getMessage();
                isSuccessful=false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            userCallback.done(isSuccessful,null);
            super.onPostExecute(aVoid);
        }

        private void setStatement(User user){

            UserState state = checkUserExist(user.getEmail());
            if(state== UserState.userDoNotExist){
                selectStatement= "Insert Into User (Name,UserName,Email,age,Password,provider,nickname,userToken) values ('" +
                        user.getName()+"','"+
                        user.getUserName()+"','"+
                        user.getEmail()+"',"+
                        user.getAge()+",'"+
                        user.getPassword()+",'" +
                        user.getProvider()+",'" +
                        user.getNickname()+",'" +
                        user.getUserToken()+"')";
            }else{

             selectStatement=   "UPDATE User SET " +
                     "ContactName = 'Alfred Schmidt', " +
                     "City= 'Frankfurt' " +
                     "City= 'Frankfurt' " +
                     "City= 'Frankfurt' " +
                     "City= 'Frankfurt' " +
                     "City= 'Frankfurt' " +
                     "WHERE email ='"+ user.getEmail()+"'";

            }

        }

    }

    public class FetchUserDataAsyncTask extends AsyncTask<Void,Void, User> {
        User user;
        GetUserCallback userCallback;
        Boolean isSuccessful=false;
        public FetchUserDataAsyncTask(User user, GetUserCallback userCallback){
            this.user = user;
            this.userCallback=userCallback;
            setStatement(user.getEmail(), user.getPassword());
        }

        @Override
        protected User doInBackground(Void... voids) {
            User returnedUser = null;
            try{
                Connection con = new ConnectionClass().Connection();
                Statement statement = con.createStatement();
                statement.setQueryTimeout(CONNECTION_TIMEOUT);


                //ResultSet rs = statement.executeQuery("Select * from UserName='"+ name +"'");
                ResultSet rs = statement.executeQuery(selectStatement);
                if(rs.next()){
                    returnedUser= new User(rs.getString("name"),
                            rs.getString("userName"),
                            rs.getString("email"),
                            rs.getInt("age"),
                            rs.getString("password"),
                            rs.getString("provider"),
                            rs.getString("nickname"),
                            rs.getString("usertoken")
                            );
                    isSuccessful=true;
                }else{
                    returnedUser = null;
                    isSuccessful=false;
                }
                if(con!= null){
                    con.close();
                }
            }catch (Exception e){
                e.printStackTrace();
                isSuccessful=false;
            }
            return returnedUser;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            progressDialog.dismiss();
            userCallback.done(isSuccessful, returnedUser);
            super.onPostExecute(user);
        }

        private void setStatement(String email,String password){

            selectStatement= "Select * from User where "+
                    "email='"+ email+ "'"+ " and "+
                    "password='"+ password+"'";
        }
    }



        public UserState checkUserExist(String email) {
            Boolean userExist = true;
            UserState state;
            setCheckStatement(email);
            try {
                Connection con = new ConnectionClass().Connection();
                Statement statement = con.createStatement();
                statement.setQueryTimeout(CONNECTION_TIMEOUT);


                //ResultSet rs = statement.executeQuery("Select * from UserName='"+ name +"'");
                ResultSet rs = statement.executeQuery(selectStatement);
                if (rs.next()) {
                    email = rs.getString("email");
                    state = UserState.userExist;
                } else {
                    email = null;
                    state = UserState.userDoNotExist;
                }
                if(con!= null){
                    con.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                state = UserState.sqlExepction;
                email = null;
            }
            return  state;
        }

        private void setCheckStatement (String email){

            selectStatement= "Select Email from User where "+
                    "email='"+ email+ "'";
        }

    public void refreshUserToken(String email,String userToken) {
        Boolean userExist = true;
        UserState state;
        setRefreshUserTokenStatement(email,userToken);
        try {
            Connection con = new ConnectionClass().Connection();
            Statement statement = con.createStatement();
            statement.setQueryTimeout(CONNECTION_TIMEOUT);


            //ResultSet rs = statement.executeQuery("Select * from UserName='"+ name +"'");
            ResultSet rs = statement.executeQuery(selectStatement);
            if(con!= null){
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setRefreshUserTokenStatement(String email,String userToken){

        selectStatement=   "UPDATE User SET " +
                "userToken = '"+userToken+"' " +
                "WHERE email ='"+email+"'";
    }

}




