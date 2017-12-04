package com.capstoneproject.dof;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.capstoneproject.dof.constants.Constant;
import com.capstoneproject.dof.constants.DatabaseConstant;
import com.capstoneproject.dof.constants.SharedPreferenceConstant;
import com.capstoneproject.dof.model.User;
import com.capstoneproject.dof.utility.ListUpdater;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import static android.R.attr.password;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText mOldPassword, mNewPassword, mConfirmNewPassword;
    private Button mChangePassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mOldPassword = (EditText) findViewById(R.id.old_password);
        mNewPassword = (EditText) findViewById(R.id.new_password);
        mConfirmNewPassword = (EditText) findViewById(R.id.confirm_new_password);
        mChangePassword = (Button) findViewById(R.id.action_change_password);

        final User user =  getUserDetails();

        mChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mOldPassword.setError(null);
                mNewPassword.setError(null);
                mConfirmNewPassword.setError(null);

                String oldPass = mOldPassword.getText().toString();
                final String newPass = mNewPassword.getText().toString();
                String confirmNewPass = mConfirmNewPassword.getText().toString();

                boolean cancel = false;
                View focusView = null;
                // Check for a valid old password
                if (TextUtils.isEmpty(oldPass)) {
                    mOldPassword.setError(getString(R.string.error_invalid_old_password));
                    focusView = mOldPassword;
                    cancel = true;
                }else if(!isOldPasswordValid(oldPass, user.getPassword())) {
                    mOldPassword.setError(getString(R.string.error_invalid_old_password));
                    focusView = mOldPassword;
                    cancel = true;
                }else if (TextUtils.isEmpty(newPass)) {
                    mNewPassword.setError(getString(R.string.error_invalid_password));
                    focusView = mNewPassword;
                    cancel = true;
                } else if( !isPasswordValid(newPass)){
                    mNewPassword.setError(getString(R.string.error_invalid_password));
                    focusView = mNewPassword;
                    cancel = true;
                } else if( !isConfirmPasswordValid(newPass, confirmNewPass)){
                    mConfirmNewPassword.setError(getString(R.string.error_invalid_confirm_password));
                    focusView = mConfirmNewPassword;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {
                        DatabaseConstant.DATABASE_REFERENCE.child(Constant.encodeKey(user.getEmail())).child("password").setValue(newPass, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    Constant.USER_INFO.setPassword(newPass);
                                    SharedPreferences prefs = getSharedPreferences(SharedPreferenceConstant.PREFS_NAME,0);
                                    String json = Constant.GSON.toJson(Constant.USER_INFO);
                                    prefs.edit().putString(SharedPreferenceConstant.USER_DATA,json).apply();

                                    Toast.makeText(getApplicationContext(), "Password Changed Successfully.", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(getApplicationContext(), "Something went wrong. Try Again!", Toast.LENGTH_SHORT).show();
                                }
                                Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                                finish();
                                startActivity(intent);
                            }
                        });



                }
            }
        });

    }

    private User getUserDetails(){
        SharedPreferences prefs = getSharedPreferences(SharedPreferenceConstant.PREFS_NAME,MODE_PRIVATE);
        String json = prefs.getString(SharedPreferenceConstant.USER_DATA,null);
        User user = Constant.GSON.fromJson(json, User.class);
        return user;
    }

    private boolean isPasswordValid(String password) {
        return  password.length()>4;
    }

    private boolean isOldPasswordValid(String pass, String actualPass){
        return pass.equals(actualPass);
    }

    private boolean isConfirmPasswordValid(String password, String cpassword){
        return cpassword.equals(password);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ListUpdater.updateListingStatus();
        Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
              onBackPressed();
                return true;
            default:
                super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}
