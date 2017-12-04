package com.capstoneproject.dof;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.capstoneproject.dof.constants.Constant;
import com.capstoneproject.dof.constants.DatabaseConstant;
import com.capstoneproject.dof.model.Item;
import com.capstoneproject.dof.utility.ImageEncoderDecoder;
import com.capstoneproject.dof.utility.ListUpdater;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;



public class AddNewListingActivity extends AppCompatActivity {

    // Constants
    static final int REQUEST_IMAGE_CAPTURE = 1;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_PICKUP = 2;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_DELIVERY = 3;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyDqtQZFGoTXyW5NvXkSDKpjRmyH1dIcjYs";


    // UI references.
    private ImageView mImageView;
    private EditText mItemName, mItemDesc, mItemValue, mDeliveryAddress, mDate, mTime;
    private AutoCompleteTextView mPickupAddress;
    private Button mAddNewListing;
    private String mImageUrl;
    private  PlaceAutocompleteFragment mAutocompleteFragment;

    //variables

    Calendar myCalendar = Calendar.getInstance();
    ArrayList<Item> listing = new ArrayList<Item>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_listing);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mImageView = (ImageView) findViewById(R.id.item_image);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        mItemName = (EditText) findViewById(R.id.item_name);
        mItemDesc = (EditText) findViewById(R.id.item_desc);
        mItemValue = (EditText) findViewById(R.id.item_value);
        mPickupAddress = (AutoCompleteTextView) findViewById(R.id.pickup_address);
        mDeliveryAddress = (EditText) findViewById(R.id.delivery_address);
        mDate = (EditText) findViewById(R.id.date);
        mTime = (EditText) findViewById(R.id.time);
        mAddNewListing = (Button) findViewById(R.id.add_new_listing);

        mPickupAddress.setInputType(InputType.TYPE_NULL);
        mDeliveryAddress.setInputType(InputType.TYPE_NULL);
        mDate.setInputType(InputType.TYPE_NULL);
        mTime.setInputType(InputType.TYPE_NULL);
        mPickupAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findPickupPlace();
            }
        });

        mDeliveryAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findDeliveryPlace();
            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }


        };

        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hour);
                myCalendar.set(Calendar.MINUTE, minute);
                updateTime();
            }
        };


        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddNewListingActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(AddNewListingActivity.this, time, myCalendar
                        .get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE),true).show();
            }
        });

        mAddNewListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addNewListing();
            }
        });

    }

    private void updateDate() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.CANADA);

        mDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateTime() {
        String myFormat = "hh:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.CANADA);

        mTime.setText(sdf.format(myCalendar.getTime()));
    }


    private void addNewListing() {

        String id = DatabaseConstant.DATABASE_REFERENCE.child(Constant.encodeKey(Constant.USER_INFO.getEmail())).child("listing").push().getKey();
        String name = mItemName.getText().toString();
        String value = mItemValue.getText().toString();
        String desc = mItemDesc.getText().toString();
        String pickUpAddress = mPickupAddress.getText().toString();
        String deliveryAddress = mDeliveryAddress.getText().toString();
        String date = mDate.getText().toString();
        String time = mTime.getText().toString();
        String status = "Active";


        // Reset errors displayed in the form.
        mItemName.setError(null);
        mItemDesc.setError(null);
        mItemValue.setError(null);
        mPickupAddress.setError(null);
        mDeliveryAddress.setError(null);
        mDate.setError(null);
        mTime.setError(null);

        boolean cancel = false;
        View focusView = null;


        if(TextUtils.isEmpty(name)){
            mItemName.setError(getString(R.string.error_field_required));
            focusView = mItemName;
            cancel = true;
        }else if(TextUtils.isEmpty(value)){
            mItemValue.setError(getString(R.string.error_field_required));
            focusView = mItemValue;
            cancel = true;
        }else if(!TextUtils.isEmpty(value) && !isItemValue(value)){
            mItemValue.setError(getString(R.string.error_item_value));
            focusView = mItemValue;
            cancel = true;
        }else if(TextUtils.isEmpty(desc)){
            mItemDesc.setError(getString(R.string.error_field_required));
            focusView = mItemDesc;
            cancel = true;
        }else if(TextUtils.isEmpty(pickUpAddress)){
            mPickupAddress.setError(getString(R.string.error_field_required));
            focusView = mPickupAddress;
            cancel = true;
        }else if(TextUtils.isEmpty(deliveryAddress)){
            mDeliveryAddress.setError(getString(R.string.error_field_required));
            focusView = mDeliveryAddress;
            cancel = true;
        }else if(TextUtils.isEmpty(date)){
            mDate.setError(getString(R.string.error_field_required));
            focusView = mDate;
            cancel = true;
        }else if(TextUtils.isEmpty(time)){
            mTime.setError(getString(R.string.error_field_required));
            focusView = mTime;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if(TextUtils.isEmpty(mImageUrl)){
                mImageUrl = "No Image Available";
            }

            mAddNewListing.setEnabled(false);
            Item item = new Item(id, mImageUrl, name, Double.parseDouble(value), desc, pickUpAddress, deliveryAddress, date, time, status);

            listing.add(item);

            DatabaseConstant.DATABASE_REFERENCE.child(Constant.encodeKey(Constant.USER_INFO.getEmail())).child("listing")
                    .child(item.getId()).setValue(item, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError == null){
                        Toast.makeText(getApplicationContext(),"Listing posted successfully",Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(getApplicationContext(),"Something went wrong. Try Again!",Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(AddNewListingActivity.this, MainActivity.class);
                    finish();
                    startActivity(intent);
                }
            });
        }
    }

    private void dispatchTakePictureIntent() {

        if ( Build.VERSION.SDK_INT >= 23){

            if ( ContextCompat.checkSelfPermission( this, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED ) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_IMAGE_CAPTURE);

            }else{
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);

            }

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

                Toast.makeText(this, "profile permission denied", Toast.LENGTH_LONG).show();

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

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_PICKUP) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                mPickupAddress.setText(place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("dof", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_DELIVERY) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                mDeliveryAddress.setText(place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i("dof", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ListUpdater.updateListingStatus();

        Intent intent = new Intent(AddNewListingActivity.this, MainActivity.class);
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

    private void findPickupPlace() {

        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_PICKUP);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e("dof","error");
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("dof","error");
        }

    }
    private void findDeliveryPlace() {

        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_DELIVERY);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e("dof","error");
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("dof","error");
        }

    }

    private boolean isItemValue(String val){
        double itemVal = Double.parseDouble(val);
        return itemVal > 0;
    }
}