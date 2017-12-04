package com.capstoneproject.dof.constants;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by mandeepkaur on 2017-11-20.
 */

public class DatabaseConstant {
    public static final DatabaseReference DATABASE_REFERENCE = FirebaseDatabase.getInstance().getReference().child("user");

}