package com.capstoneproject.dof;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.capstoneproject.dof.adapter.BidderListAdapter;
import com.capstoneproject.dof.constants.Constant;
import com.capstoneproject.dof.constants.DatabaseConstant;
import com.capstoneproject.dof.constants.SharedPreferenceConstant;
import com.capstoneproject.dof.model.Bid;
import com.capstoneproject.dof.model.Item;
import com.capstoneproject.dof.model.PaymentCard;
import com.capstoneproject.dof.model.User;
import com.capstoneproject.dof.utility.ImageEncoderDecoder;
import com.capstoneproject.dof.utility.ListUpdater;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.R.attr.password;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static com.capstoneproject.dof.R.id.login;

public class BidDescriptionActivity extends AppCompatActivity {

    private TextView mItemName, mItemDesc, mItemValue, mPickupAddress, mDeliveryAddress, mDateTime;
    private EditText mBidAmount, name, cardNumber, cvv, expiryDate;
    private ImageView mImageView;
    private Button mBidButton;
    private CheckBox mPrivacyCB;

    //constants
    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_description);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mImageView = (ImageView) findViewById(R.id.item_image);
        mItemName = (TextView) findViewById(R.id.item_name);
        mItemDesc = (TextView) findViewById(R.id.item_desc);
        mItemValue = (TextView) findViewById(R.id.item_value);
        mPickupAddress = (TextView) findViewById(R.id.item_pickup_address);
        mDeliveryAddress = (TextView) findViewById(R.id.item_delivery_address);
        mDateTime = (TextView) findViewById(R.id.item_date_time);
        mBidAmount = (EditText) findViewById(R.id.bid_amount);
        mBidButton = (Button) findViewById(R.id.bid_button);

        Bundle bundle = getIntent().getExtras();
        String itemData = bundle.getString("item");
        final Item item = Constant.GSON.fromJson(itemData, Item.class);

        mItemName.setText(item.getName());
        mItemValue.setText(String.valueOf(item.getValue()));
        mItemDesc.setText(item.getDescription());
        mPickupAddress.setText(item.getPickUpAddress());
        mDeliveryAddress.setText(item.getDeliveryAddress());
        mDateTime.setText(item.getDate()+" "+item.getTime());
        if(item.getImageUrl().equals("No Image Available")){
            mImageView.setImageResource(R.drawable.noimage);
        }else {
            try {
                Bitmap imageBitmap = ImageEncoderDecoder.decodeFromFirebaseBase64(item.getImageUrl());
                mImageView.setImageBitmap(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        mBidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBidButton.setError(null);
                String amount = mBidAmount.getText().toString();

                boolean cancel = false;
                View focusView = null;

                if (!isBidAmountValid(amount)) {
                    mBidAmount.setError(getString(R.string.error_field_required));
                    focusView = mBidAmount;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {
                  //  mBidButton.setEnabled(false);
                    DatabaseConstant.DATABASE_REFERENCE.child(Constant.encodeKey(Constant.USER_INFO.getEmail()))
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild("card")){
                                        getBiddingDetails(item);
                                    }else{
                                        callPaymentDialog(item);
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // showErrorDialog(databaseError.getMessage());
                                }

                            });
                }
            }
        });
    }

    private void addBidding(final Bid bid){

            DatabaseConstant.DATABASE_REFERENCE.child(Constant.encodeKey(Constant.USER_INFO.getEmail())).child("bidding").child(bid.getId())
                    .setValue(bid, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    String bidKey = bid.getId();
                    if (databaseError == null) {
                        DatabaseConstant.DATABASE_REFERENCE.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String userId = bid.getPosterId();
                                DatabaseConstant.DATABASE_REFERENCE.child(userId).child("listing").child(bid.getItemId()).child("bidder")
                                        .child(bid.getId()).setValue(bid, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError == null) {
                                            Intent intent = new Intent(BidDescriptionActivity.this, MainActivity.class);
                                            finish();
                                            startActivity(intent);
                                            Toast.makeText(getApplicationContext(), "Bid submitted successfully.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            DatabaseConstant.DATABASE_REFERENCE.child(Constant.encodeKey(Constant.USER_INFO.getEmail())).child("bidding")
                                                    .child(bid.getId()).removeValue();

                                            Toast.makeText(getApplicationContext(), "Something went wrong. Try Again!", Toast.LENGTH_SHORT).show();    }
                                            Intent intent = new Intent(BidDescriptionActivity.this, MainActivity.class);
                                            finish();
                                            startActivity(intent);
                                    }

                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // showErrorDialog(databaseError.getMessage());
                            }

                        });


                    } else {

                        Toast.makeText(getApplicationContext(), "Something went wrong. Try Again!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(BidDescriptionActivity.this, MainActivity.class);
                        finish();
                        startActivity(intent);
                    }

                }

            });

    }


    private void callPaymentDialog(final Item item)
    {
        Dialog myDialog;
        myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.activity_card_detail);
        myDialog.setCancelable(true);

        name = (EditText) myDialog.findViewById(R.id.card_name);
        cardNumber = (EditText) myDialog.findViewById(R.id.card_number);
        cvv = (EditText) myDialog.findViewById(R.id.card_cvv);
        expiryDate = (EditText) myDialog.findViewById(R.id.card_expiry_date);
        mPrivacyCB = (CheckBox) myDialog.findViewById(R.id.privacy_policy);

        Button saveButton = (Button) myDialog.findViewById(R.id.save_card);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePaymentDetails(item);
            }
        });


        myDialog.show();

    }

    private boolean savePaymentDetails(Item item){
        name.setError(null);
        cardNumber.setError(null);
        cvv.setError(null);
        expiryDate.setError(null);
        mPrivacyCB.setError(null);

        // Store values at the time of the login attempt.
        String cardName = name.getText().toString();
        String cardNo = cardNumber.getText().toString();
        String cardCvv = cvv.getText().toString();
        String cardED = expiryDate.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //checks for valid name

        if(!isCardNameValid(cardName)){
            name.setError(getString(R.string.error_field_required));
            focusView = name;
            cancel = true;
        }

        // Check for a valid card number.
        if (!isCardNumberValid(cardNo)) {
            cardNumber.setError(getString(R.string.error_field_required));
            focusView = cardNumber;
            cancel = true;
        }
        //checks for valid cvv

        if(!isCvvValid(cardCvv)){
            cvv.setError(getString(R.string.error_field_required));
            focusView = cvv;
            cancel = true;
        }

        // Check for a valid expiryDate
        if(!isExpiryDateValid(cardED)){
            expiryDate.setError(getString(R.string.error_field_required));
            focusView = expiryDate;
            cancel = true;
        }

        //check privacy checkbox checked
        if(!mPrivacyCB.isChecked()){
            mPrivacyCB.setError(getString(R.string.error_field_required));
            focusView = mPrivacyCB;
            cancel = true;
        }


        if (cancel) {
            focusView.requestFocus();
        } else {
            PaymentCard paymentCard = new PaymentCard(cardName, cardNo, cardCvv, cardED);
            DatabaseConstant.DATABASE_REFERENCE.child(Constant.encodeKey(Constant.USER_INFO.getEmail()))
                    .child("card").push().setValue(paymentCard);
            getBiddingDetails(item);

        }

        return false;

    }
    private boolean isCardNameValid(String cname){

        return cname.length() >= 3;
    }

    private boolean isCardNumberValid(String cnum){

        return cnum.length() == 16;
    }

    private boolean isCvvValid(String cCvv){

        return cCvv.length() == 3;
    }

    private boolean isExpiryDateValid(String ed){
        boolean flag;
        if(ed.isEmpty()) flag = false;
        else flag = true;

        return flag;
    }

    private void getBiddingDetails(Item item){
        String id = DatabaseConstant.DATABASE_REFERENCE.child(Constant.encodeKey(Constant.USER_INFO.getEmail())).child("bidding").push().getKey();
        String userId = Constant.USER_INFO.getEmail();
        String posterId = item.getUserId();
        String itemId = item.getId();
        double bidAmount = Double.parseDouble(mBidAmount.getText().toString());
        String status = "Pending";
        Bid bid = new Bid(id, userId, itemId, bidAmount, status, posterId);
        addBidding(bid);

    }

    private boolean isBidAmountValid(String amount ){
        double amt = Double.parseDouble(amount);
        return amt > 0;
    }

    private void updateDate() {
        String myFormat = "MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        expiryDate.setText(sdf.format(myCalendar.getTime()));
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ListUpdater.updateListingStatus();
        Intent intent = new Intent(BidDescriptionActivity.this, MainActivity.class);
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
