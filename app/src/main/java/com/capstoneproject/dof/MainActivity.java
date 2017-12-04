package com.capstoneproject.dof;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.net.ConnectivityManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.capstoneproject.dof.adapter.BidListingAdapter;
import com.capstoneproject.dof.constants.Constant;
import com.capstoneproject.dof.constants.DatabaseConstant;
import com.capstoneproject.dof.constants.SharedPreferenceConstant;
import com.capstoneproject.dof.fragment.BidFragment;
import com.capstoneproject.dof.fragment.MyListingFragment;
import com.capstoneproject.dof.model.Item;

import com.capstoneproject.dof.model.Message;
import com.capstoneproject.dof.model.User;
import com.capstoneproject.dof.utility.ListUpdater;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;


public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private int messageCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMessageCount();
        ListUpdater.updateListingStatus();
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.list);
        tabLayout.getTabAt(1).setIcon(R.drawable.gravel);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.menu_messages);
        MenuItemCompat.setActionView(item, R.layout.badge_layout);
        RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);
        final TextView tv = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_textview);


        getMessageCount();
        tv.setText(String.valueOf(messageCount));
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("dof","count clicked");
                Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                finish();
                startActivity(intent);
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_change_profile) {
            Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
            finish();
            startActivity(intent);
        }else if(id == R.id.action_change_password){
            Intent intent = new Intent(MainActivity.this, ChangePasswordActivity.class);
            finish();
            startActivity(intent);
        }else if(id == R.id.action_support){
            showSupportDialog();
        }else if(id == R.id.action_logout){
            SharedPreferences prefs = getSharedPreferences(SharedPreferenceConstant.PREFS_NAME,0);
            prefs.edit().clear().commit();

            Constant.USER_INFO = null;

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();
            startActivity(intent);
        }
        else if(id == R.id.menu_messages){
            Log.i("dof","item clicked");
                Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                finish();
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        private static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch(position){
                case 0:
                {
                    fragment = new MyListingFragment();
                    Bundle args = new Bundle();
                    args.putInt(ARG_SECTION_NUMBER, position);
                    fragment.setArguments(args);

                    break;
                }
                case 1:
                {
                    fragment = new BidFragment();
                    Bundle args = new Bundle();
                    args.putInt(ARG_SECTION_NUMBER, position);
                    fragment.setArguments(args);
                    break;
                }
                default:
                    try {
                        throw new IllegalArgumentException("Invalid fragment number: " + position);
                    }catch(Exception e)
                    {
                        Log.i("dof","error"+e.getMessage());
                    }
            }


            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:

                    return "My Listing";
                case 1:
                    return "Bid";

            }
            return null;
        }
    }
    private User getUserDetails(){
        SharedPreferences prefs = getSharedPreferences(SharedPreferenceConstant.PREFS_NAME,MODE_PRIVATE);
        String json = prefs.getString(SharedPreferenceConstant.USER_DATA,null);
        User user = Constant.GSON.fromJson(json, User.class);
        return user;
    }

    private void getMessageCount(){

        DatabaseConstant.DATABASE_REFERENCE.child(Constant.encodeKey(Constant.USER_INFO.getEmail())).child("bidding")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                            if(child.hasChild("message")){

                                for(DataSnapshot ch : child.child("message").getChildren())
                                {

                                    Message message = ch.getValue(Message.class);
                                    if(message.getStatus().equals("New")){
                                         messageCount++;
                                    }
                                    break;
                                }

                            };

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("dof",databaseError.getMessage());
                    }

                });



    }

//    public void updateListingStatus(){
//
//
//
//        DatabaseConstant.DATABASE_REFERENCE.addListenerForSingleValueEvent(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot ref: dataSnapshot.getChildren()) {
//
//                    for (DataSnapshot child: dataSnapshot.child(ref.getKey()).child("listing").getChildren()) {
//                        Item currentItem = child.getValue(Item.class);
//                        if (currentItem.getStatus().equals("Active")) {
//
//                            String status = null;
//
//                            String listingDate = currentItem.getDate() + " " + currentItem.getTime();
//                            String myFormat = "dd/MM/yyyy hh:mm a";
//                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.CANADA);
//                            Date date = new Date();
//                            String today = sdf.format(date);
//
//                            try {
//                                Date d1 = sdf.parse(listingDate);
//                                Date d2 = sdf.parse(today);
//                                Log.i("dof", "Date1" + d1);
//                                Log.i("dof", "Date2" + d2);
//                                if (d1.compareTo(d2) >= 0) {
//                                    status = "Active";
//                                } else {
//                                    status = "Inactive";
//                                }
//
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//
//                            if (status.equals("Inactive")) {
//                                Item postChangeItem = currentItem;
//                                postChangeItem.setStatus(status);
//                                Map<String, Object> postValues = postChangeItem.toMap();
//                                DatabaseConstant.DATABASE_REFERENCE.child(ref.getKey()).child("listing").child(postChangeItem.getId()).updateChildren(postValues);
//                            }
//
//                        }
//
//                    }
//
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e("dof",databaseError.getMessage());
//            }
//
//        });

//        DatabaseConstant.DATABASE_REFERENCE.addListenerForSingleValueEvent(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String email = Constant.encodeKey(Constant.USER_INFO.getEmail());
//                if (dataSnapshot.hasChild(email)){
//
//                    for (DataSnapshot child: dataSnapshot.child(email).child("listing").getChildren()) {
//                        Item currentItem = child.getValue(Item.class);
//                        if(currentItem.getStatus().equals("Active")){
//
//                            String status = null;
//
//                            String listingDate = currentItem.getDate() + " "+currentItem.getTime();
//                            String myFormat = "dd/MM/yyyy hh:mm a";
//                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.CANADA);
//                            Date date = new Date();
//                            String today = sdf.format(date);
//
//                            try {
//                                Date d1 = sdf.parse(listingDate);
//                                Date d2 = sdf.parse(today);
//                                Log.i("dof","Date1"+d1);
//                                Log.i("dof","Date2"+d2);
//                                if(d1.compareTo(d2)>=0){
//                                    status = "Active";
//                                }else{
//                                    status = "Inactive";
//                                }
//
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//
//                            if(status.equals("Inactive")){
//                                Item postChangeItem = currentItem;
//                                postChangeItem.setStatus(status);
//                                Map<String, Object> postValues = postChangeItem.toMap();
//                                DatabaseConstant.DATABASE_REFERENCE.child(email).child("listing").child(postChangeItem.getId()).updateChildren(postValues);
//                            }
//
//                        }
//
//                    }
//
//                }else{
//                    Toast.makeText(getApplicationContext(), "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e("dof",databaseError.getMessage());
//            }
//
//        });
 //   }

    @Override
    public void onBackPressed() {

    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.i("dof","error"+messageCount);


    }


    private void showSupportDialog(){
        Dialog myDialog;
        myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.support_dialog);
        myDialog.setCancelable(true);
        myDialog.setTitle(R.string.support_title);
        myDialog.show();
    }




}
