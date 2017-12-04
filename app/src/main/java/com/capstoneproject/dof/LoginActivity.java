package com.capstoneproject.dof;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.capstoneproject.dof.constants.Constant;
import com.capstoneproject.dof.constants.DatabaseConstant;
import com.capstoneproject.dof.constants.SharedPreferenceConstant;
import com.capstoneproject.dof.model.Item;
import com.capstoneproject.dof.model.User;
import com.capstoneproject.dof.utility.ListUpdater;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.name;


public class LoginActivity extends AppCompatActivity {

    // TODO: Add member variables here:

    //UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    //Firebase Instance
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences prefs = getSharedPreferences(SharedPreferenceConstant.PREFS_NAME,MODE_PRIVATE);
        String json = prefs.getString(SharedPreferenceConstant.USER_DATA,null);
        User user = Constant.GSON.fromJson(json, User.class);


        if (user != null)
        {
            ListUpdater.updateListingStatus();
            Constant.USER_INFO = user;
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity (intent);
            this.finishActivity (0);
        }

        mEmailView = (AutoCompleteTextView) findViewById(R.id.login_email);
        mPasswordView = (EditText) findViewById(R.id.login_password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login|| id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("user");

    }

    // Executed when Sign in button pressed
    public void signInExistingUser(View v)   {
        // TODO: Call attemptLogin() here
        attemptLogin();
    }

    // Executed when Register button pressed
    public void registerNewUser(View v) {
        Intent intent = new Intent(this, com.capstoneproject.dof.SignUpActivity.class);
        finish();
        startActivity(intent);
    }

    // TODO: Complete the attemptLogin() method
    private void attemptLogin() {
        mPasswordView.setError(null);
        mEmailView.setError(null);

        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();
        boolean cancel = false;
        View focusView = null;


        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }


        if (cancel) {
            focusView.requestFocus();
        } else {

            Toast.makeText(this, "Login in Progress.....", Toast.LENGTH_SHORT).show();
            DatabaseConstant.DATABASE_REFERENCE.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(Constant.encodeKey(email))){
                        User  userData = dataSnapshot.child(Constant.encodeKey(email)).getValue(User.class);
                        //Log.i("dof","image"+userData.getImage());
                        if(userData.getPassword().equals(password)){
                            Log.i("dof","user data"+userData.getEmail());
                            SharedPreferences prefs = getSharedPreferences(SharedPreferenceConstant.PREFS_NAME,0);
                            String json = Constant.GSON.toJson(userData);
                            prefs.edit().putString(SharedPreferenceConstant.USER_DATA,json).apply();

                            Constant.USER_INFO = userData;


                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            finish();
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(), "Invalid Passwword. Please try again!", Toast.LENGTH_SHORT).show();
                        }
                        Log.i("dof","found");
                    }else{
                        Toast.makeText(getApplicationContext(), "Invalid Email. Please try again!", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    showErrorDialog(databaseError.getMessage());
                }

            });
        }



    }

    // TODO: Show error on screen with an alert dialog

    private void showErrorDialog(String message){
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    private boolean isEmailValid(String email) {
        // You can add more checking logic here.
        return email.contains("@") ;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }


}