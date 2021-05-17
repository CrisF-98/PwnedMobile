package com.example.thepwnedgame.users;

import android.content.Context;
import android.content.SharedPreferences;

public class UserManager {

    private static final String PREFERENCE_NAME = "user_profile";
    private static final String USERNAME = "username";
    private static final String EMAIL = "e-mail";
    private static final String USER_ID = "userId";

    public static void saveUserInfo(Context context, User user){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        sp.edit()
                .putString(USERNAME, user.getUsername())
                .putString(EMAIL, user.getEmail())
                .putInt(USER_ID, user.getUserId())
                .apply();
    }

    public static User getUserInfo(Context context){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return new User(sp.getString(USERNAME, null), sp.getInt(USER_ID, 0), sp.getString(EMAIL, null));
    }

    public static void deleteUserInfo(Context context){
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        sp.edit()
                .putString(USERNAME, null)
                .putString(EMAIL, null)
                .putInt(USER_ID, 0)
                .apply();
    }

}
