package com.example.halloworld.Interface;

import com.example.halloworld.Model.User;

public interface GetUserCallback {

    public abstract  void done (Boolean issuccessful , User returnedUser);
}
