package com.capstoneproject.dof.constants;

import android.util.Log;

import com.capstoneproject.dof.model.Item;
import com.capstoneproject.dof.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by mandeepkaur on 2017-11-20.
 */

public class Constant {

    public static String CURRENT_USER_KEY;
    public static User USER_INFO;
    public static final Gson GSON = new Gson();

    public static List<Item> MY_LISTING;

    public static String encodeKey(String key){
        String s = key.replace(".","");
        return s;
    }


}
