package com.capstoneproject.dof;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.capstoneproject.dof.constants.Constant;
import com.capstoneproject.dof.constants.DatabaseConstant;
import com.capstoneproject.dof.constants.SharedPreferenceConstant;
import com.capstoneproject.dof.model.User;
import com.capstoneproject.dof.utility.ImageEncoderDecoder;
import com.capstoneproject.dof.utility.ListUpdater;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.util.Map;

import static com.capstoneproject.dof.AddNewListingActivity.REQUEST_IMAGE_CAPTURE;

public class UserProfileActivity extends AppCompatActivity {
    // Constants
    static final int REQUEST_IMAGE_CAPTURE = 1;

    //UI Reference
    private EditText mEmailView, mNameView, mPhoneView;
    private Button mUpdateProfile;
    private ImageView mImageView;
    private String mImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEmailView = (EditText) findViewById(R.id.user_email);
        mNameView = (EditText) findViewById(R.id.user_name);
        mPhoneView = (EditText) findViewById(R.id.user_phone);
        mImageView = (ImageView) findViewById(R.id.user_image);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        mUpdateProfile = (Button) findViewById(R.id.update_profile);

        final User user =  getUserDetails();
        mEmailView.setText(user.getEmail());
        mNameView.setText(user.getName());
        mPhoneView.setText(user.getPhone());
        if(user.getImage() != null){
            try {
                Bitmap imageBitmap = ImageEncoderDecoder.decodeFromFirebaseBase64(user.getImage());
                mImageView.setImageBitmap(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPhoneView.setError(null);
                mNameView.setError(null);

                String name = mNameView.getText().toString();
                String phone = mPhoneView.getText().toString();

                boolean cancel = false;
                View focusView = null;

                if (!isNameValid(name)) {
                    mNameView.setError(getString(R.string.error_field_required));
                    focusView = mNameView;
                    cancel = true;
                }
                //checks for valid phone

                if (!isPhoneValid(phone)) {
                    mPhoneView.setError(getString(R.string.error_field_required));
                    focusView = mPhoneView;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {
                    mUpdateProfile.setEnabled(false);

                    final User postChangeUser = new User(mEmailView.getText().toString(), mNameView.getText().toString(), mPhoneView.getText().toString());
                    postChangeUser.setImage(mImageUrl);
                    Map<String, Object> postValues = postChangeUser.toMap();
                    DatabaseConstant.DATABASE_REFERENCE.child(Constant.encodeKey(user.getEmail())).updateChildren(postValues, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError == null){


                                SharedPreferences prefs = getSharedPreferences(SharedPreferenceConstant.PREFS_NAME,0);
                                String json = Constant.GSON.toJson(postChangeUser);
                                prefs.edit().putString(SharedPreferenceConstant.USER_DATA,json).apply();

                                Constant.USER_INFO = postChangeUser;

                                Toast.makeText(getApplicationContext(),"Profile Updated Successfully.",Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(getApplicationContext(),"Something went wrong. Try Again!",Toast.LENGTH_SHORT).show();
                            }
                            Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                            finish();
                            startActivity(intent);
                        }
                    });

                }

            }
        });


    }
    private void dispatchTakePictureIntent() {

        if ( Build.VERSION.SDK_INT >= 23){

///            if ( ContextCompat.checkSelfPermission( this, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED ) {
///                requestPermissions(new String[]{Manifest.permission.CAMERA},
///                      REQUEST_IMAGE_CAPTURE);
///
///            }else{
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);

          //  }

        }else{
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_IMAGE_CAPTURE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);

            } else {

                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();

            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
            mImageUrl = ImageEncoderDecoder.encodeBitmapAndSaveToFirebase(imageBitmap);
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            mImageView.setImageBitmap(imageBitmap);
            mImageUrl = ImageEncoderDecoder.encodeBitmapAndSaveToFirebase(imageBitmap);
        }
    }

    private User getUserDetails(){
        SharedPreferences prefs = getSharedPreferences(SharedPreferenceConstant.PREFS_NAME,MODE_PRIVATE);
        String json = prefs.getString(SharedPreferenceConstant.USER_DATA,null);
        User user = Constant.GSON.fromJson(json, User.class);
        return user;
    }

    private boolean isPhoneValid(String phone){

        return phone.length() == 10;
    }

    private boolean isNameValid(String name){

        return name.length() >= 3;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ListUpdater.updateListingStatus();
        Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
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
