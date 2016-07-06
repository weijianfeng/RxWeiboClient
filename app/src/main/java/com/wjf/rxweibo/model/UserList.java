package com.wjf.rxweibo.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

public class UserList {
	
    public ArrayList<User> userList;
    public String previous_cursor;
    public String next_cursor;
    public int total_number;
    
    public static UserList parse(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        
        UserList users = new UserList();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            users.previous_cursor = jsonObject.optString("previous_cursor", "0");
            users.next_cursor     = jsonObject.optString("next_cursor", "0");
            users.total_number    = jsonObject.optInt("total_number", 0);
            
            JSONArray jsonArray      = jsonObject.optJSONArray("users");
            if (jsonArray != null && jsonArray.length() > 0) {
                int length = jsonArray.length();
                users.userList = new ArrayList<User>(length);
                for (int ix = 0; ix < length; ix++) {
                	users.userList.add(User.parse(jsonArray.optJSONObject(ix)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return users;
    }
}
