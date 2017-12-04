package com.capstoneproject.dof.utility;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.capstoneproject.dof.LoginActivity;
import com.capstoneproject.dof.MainActivity;
import com.capstoneproject.dof.constants.Constant;
import com.capstoneproject.dof.constants.DatabaseConstant;
import com.capstoneproject.dof.constants.SharedPreferenceConstant;
import com.capstoneproject.dof.model.PaymentCard;
import com.capstoneproject.dof.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static android.R.attr.password;

/**
 * Created by mandeepkaur on 2017-11-23.
 */

public class CardDetailChecker {
    private static PaymentCard card;
    public static PaymentCard checkCard(final String id){

        DatabaseConstant.DATABASE_REFERENCE.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("card")){
                    card = dataSnapshot.child(id).child("card").getValue(PaymentCard.class);
                }else{
                    card = null;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //showErrorDialog(databaseError.getMessage());
            }

        });
        return card;
    }
}
