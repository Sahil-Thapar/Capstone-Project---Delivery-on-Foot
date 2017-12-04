package com.capstoneproject.dof;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.capstoneproject.dof.constants.Constant;
import com.capstoneproject.dof.constants.DatabaseConstant;
import com.capstoneproject.dof.model.Item;
import com.capstoneproject.dof.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import static android.R.attr.name;


public class SignUpActivity extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mNameView;
    private EditText mPhoneView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private TextView mSignIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.register_email);
        mPasswordView = (EditText) findViewById(R.id.register_password);
        mConfirmPasswordView = (EditText) findViewById(R.id.register_confirm_password);
        mNameView = (AutoCompleteTextView) findViewById(R.id.register_name);
        mPhoneView = (EditText) findViewById(R.id.register_phone);
        mSignIn = (TextView) findViewById(R.id.action_sign_in);

//        Item item = new Item("i1","Item1",100.2,"baaa","accaa","aaab","aaba","Aaaa");
//        listing.add(item);

        // Keyboard sign in action
        mConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register_form_finished || id == EditorInfo.IME_NULL) {
                    attemptRegistration();
                    return true;
                }
                return false;
            }
        });

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);
            }
        });

    }

    // Executed when Sign Up button is pressed.
    public void signUp(View v) {
        attemptRegistration();
    }

    private void attemptRegistration() {

        // Reset errors displayed in the form.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPhoneView.setError(null);
        mNameView.setError(null);

        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String phone = mPhoneView.getText().toString();
        String confirmPassword = mConfirmPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if(!isNameValid(name)){
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }else if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }else if(!isPhoneValid(phone)){
            mPhoneView.setError(getString(R.string.error_invalid_phone));
            focusView = mPhoneView;
            cancel = true;
        }else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }else if(!isPasswordValid(password)){
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        else if(!isConfirmPasswordValid(password,confirmPassword)){
            mConfirmPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mConfirmPasswordView;
            cancel = true;
        }


        if (cancel) {
            focusView.requestFocus();
        } else {
            User user = new User(mEmailView.getText().toString(), mNameView.getText().toString(), mPhoneView.getText().toString(),
                    mPasswordView.getText().toString());
            writeNewUser(user);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");

    }

    private boolean isPasswordValid(String password) {

        return password.length()>4;
    }

    private boolean isPhoneValid(String phone){

        return phone.length() == 10;
    }

    private boolean isNameValid(String name){

        return name.length() >= 3;
    }

    private boolean isConfirmPasswordValid(String pass, String confirmPass){

        return pass.equals(confirmPass);
    }

    private void writeNewUser(User user) {
        String key = Constant.encodeKey(mEmailView.getText().toString());
        DatabaseConstant.DATABASE_REFERENCE.child(key).setValue(user);
        Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }

//    // TODO: Save the display name to Shared Preferences
//    private void saveDisplayName(){
//        String displayName = mNameView.getText().toString();
//        Log.d("dof","Author "+ displayName);
//        SharedPreferences prefs = getSharedPreferences(CHAT_PREFS,0);
//        prefs.edit().putString(DISPLAY_NAME_KEY,displayName).apply();
//    }


    // TODO: Create an alert dialog to show in case registration failed
    private void showErrorDialog(String message){
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        finish();
        startActivity(intent);
    }




}
