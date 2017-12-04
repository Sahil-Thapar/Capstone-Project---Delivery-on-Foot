package com.capstoneproject.dof;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.capstoneproject.dof.constants.Constant;
import com.capstoneproject.dof.constants.DatabaseConstant;
import com.capstoneproject.dof.model.User;

public class CardDetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);
    }
}

//    public void savePaymentDetails(View v) {
//
//    }

//    private void attemptRegistration() {
//
//        // Reset errors displayed in the form.
//        mEmailView.setError(null);
//        mPasswordView.setError(null);
//        mPhoneView.setError(null);
//        mNameView.setError(null);
//
//        // Store values at the time of the login attempt.
//        String name = mNameView.getText().toString();
//        String email = mEmailView.getText().toString();
//        String password = mPasswordView.getText().toString();
//        String phone = mPhoneView.getText().toString();
//
//        boolean cancel = false;
//        View focusView = null;
//
//        //checks for valid name
//
//        if(!isNameValid(name)){
//            mNameView.setError(getString(R.string.error_field_required));
//            focusView = mNameView;
//            cancel = true;
//        }
//
//        // Check for a valid email address.
//        if (TextUtils.isEmpty(email)) {
//            mEmailView.setError(getString(R.string.error_field_required));
//            focusView = mEmailView;
//            cancel = true;
//        } else if (!isEmailValid(email)) {
//            mEmailView.setError(getString(R.string.error_invalid_email));
//            focusView = mEmailView;
//            cancel = true;
//        }
//        //checks for valid phone
//
//        if(!isPhoneValid(phone)){
//            mPhoneView.setError(getString(R.string.error_field_required));
//            focusView = mPhoneView;
//            cancel = true;
//        }
//
//        // Check for a valid password, if the user entered one.
//        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
//            mPasswordView.setError(getString(R.string.error_invalid_password));
//            focusView = mPasswordView;
//            cancel = true;
//        }
//
//
//        if (cancel) {
//            focusView.requestFocus();
//        } else {
//            User user = new User(mEmailView.getText().toString(), mNameView.getText().toString(), mPhoneView.getText().toString(),
//                    mPasswordView.getText().toString());
//            writeNewUser(user);
//        }
//    }
//
//    private boolean isEmailValid(String email) {
//        // You can add more checking logic here.
//        return email.contains("@") && email.contains(".");
//
//    }
//
//    private boolean isPasswordValid(String password) {
//        //TODO: Add own logic to check for a valid password
//        String confirmPasswordView = mConfirmPasswordView.getText().toString();
//        return confirmPasswordView.equals(password) && password.length()>4;
//    }
//
//    private boolean isPhoneValid(String phone){
//
//        return phone.length() == 10;
//    }
//
//    private boolean isNameValid(String name){
//
//        return name.length() >= 3;
//    }
//
//    private void writeNewUser(User user) {
//        String key = Constant.encodeKey(mEmailView.getText().toString());
//        DatabaseConstant.DATABASE_REFERENCE.child(key).setValue(user);
//        Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
//        startActivity(intent);
//    }
//
////    // TODO: Save the display name to Shared Preferences
////    private void saveDisplayName(){
////        String displayName = mNameView.getText().toString();
////        Log.d("dof","Author "+ displayName);
////        SharedPreferences prefs = getSharedPreferences(CHAT_PREFS,0);
////        prefs.edit().putString(DISPLAY_NAME_KEY,displayName).apply();
////    }
//
//
//    // TODO: Create an alert dialog to show in case registration failed
//    private void showErrorDialog(String message){
//        new AlertDialog.Builder(this)
//                .setTitle("Oops")
//                .setMessage(message)
//                .setPositiveButton(android.R.string.ok, null)
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .show();
//
//    }
//}
