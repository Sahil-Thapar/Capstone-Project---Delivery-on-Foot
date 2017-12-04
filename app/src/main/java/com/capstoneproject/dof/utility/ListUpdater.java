package com.capstoneproject.dof.utility;

import android.util.Log;

import com.capstoneproject.dof.constants.DatabaseConstant;
import com.capstoneproject.dof.model.Item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Created by mandeepkaur on 2017-11-29.
 */

public class ListUpdater {

    public static void updateListingStatus() {


        DatabaseConstant.DATABASE_REFERENCE.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ref : dataSnapshot.getChildren()) {

                    for (DataSnapshot child : dataSnapshot.child(ref.getKey()).child("listing").getChildren()) {
                        Item currentItem = child.getValue(Item.class);
                        if (currentItem.getStatus().equals("Active")) {

                            String status = null;

                            String listingDate = currentItem.getDate() + " " + currentItem.getTime();
                            String myFormat = "dd/MM/yyyy hh:mm a";
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.CANADA);
                            Date date = new Date();
                            String today = sdf.format(date);

                            try {
                                Date d1 = sdf.parse(listingDate);
                                Date d2 = sdf.parse(today);
                                Log.i("dof", "Date1" + d1);
                                Log.i("dof", "Date2" + d2);
                                if (d1.compareTo(d2) >= 0) {
                                    status = "Active";
                                } else {
                                    status = "Inactive";
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (status.equals("Inactive")) {
                                Item postChangeItem = currentItem;
                                postChangeItem.setStatus(status);
                                Map<String, Object> postValues = postChangeItem.toMap();
                                DatabaseConstant.DATABASE_REFERENCE.child(ref.getKey()).child("listing").child(postChangeItem.getId()).updateChildren(postValues);
                            }

                        }

                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("dof", databaseError.getMessage());
            }

        });
    }
}
