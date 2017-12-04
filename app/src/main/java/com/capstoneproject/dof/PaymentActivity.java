package com.capstoneproject.dof;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.capstoneproject.dof.constants.Constant;
import com.capstoneproject.dof.constants.DatabaseConstant;
import com.capstoneproject.dof.constants.SharedPreferenceConstant;
import com.capstoneproject.dof.model.Bid;
import com.capstoneproject.dof.model.Item;
import com.capstoneproject.dof.model.Message;
import com.capstoneproject.dof.model.PaymentCard;
import com.capstoneproject.dof.model.User;
import com.capstoneproject.dof.utility.ListUpdater;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

 /* Created by mandeepkaur on 2017-11-23.
 */

public class PaymentActivity extends AppCompatActivity {

    private EditText mFinalBidAmount, mReceiverName, mReceiverPhone;
    private EditText name, cardNumber, cvv, expiryDate;
    private CheckBox mPrivacyCB;
    private Button mPay;
    private Item item;
    private ImageView mInfo;
    private boolean cardSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mReceiverName = (EditText)findViewById(R.id.receiver_name);
        mReceiverPhone = (EditText)findViewById(R.id.receiver_phone);
        name = (EditText)findViewById(R.id.card_name);
        cardNumber = (EditText)findViewById(R.id.card_number);
        cvv = (EditText) findViewById(R.id.card_cvv);
        expiryDate = (EditText) findViewById(R.id.card_expiry_date);
        mPrivacyCB = (CheckBox)findViewById(R.id.privacy_policy);
        mFinalBidAmount = (EditText) findViewById(R.id.final_bid_amount);
        mPay = (Button) findViewById(R.id.action_pay);
        mInfo = (ImageView) findViewById(R.id.info);

        mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPrivacyDialog();
            }
        });

       // Button pay = (Button) findViewById(R.id.action_pay);
        Bundle bundle = getIntent().getExtras();
        String bidder = bundle.getString("bidder");
        String itemData = bundle.getString("itemData");
        final Bid bid = Constant.GSON.fromJson(bidder, Bid.class);
        item = Constant.GSON.fromJson(itemData, Item.class);

        double finalAmount = bid.getBidAmount() + (bid.getBidAmount()*5)/100;
        mFinalBidAmount.setText("$"+String.valueOf(finalAmount));

        DatabaseConstant.DATABASE_REFERENCE.child(Constant.encodeKey(Constant.USER_INFO.getEmail()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        PaymentCard card = null;
                        if (dataSnapshot.hasChild("card")){
                            for (DataSnapshot child: dataSnapshot.child("card").getChildren()) {
                               card = child.getValue(PaymentCard.class);
                                cardSaved = true;
                                break;
                            }


                                name.setText(card.getCardName());
                                cardNumber.setText(card.getCardNumber());
                                cvv.setText(card.getCvv());
                                expiryDate.setText(card.getExpiryDate());
                                name.setEnabled(false);
                                cardNumber.setEnabled(false);
                                cvv.setEnabled(false);
                                expiryDate.setEnabled(false);
                                mPrivacyCB.setChecked(true);
                                mPrivacyCB.setEnabled(false);

                        }
                        mPay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                savePaymentDetails(bid);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // showErrorDialog(databaseError.getMessage());
                    }

                });


    }
//    private void callPaymentDialog(final Bid bid)
//    {
//        Dialog myDialog;
//        myDialog = new Dialog(this);
//        myDialog.setContentView(R.layout.activity_card_detail);
//        myDialog.setCancelable(true);
//
//        name = (EditText) myDialog.findViewById(R.id.card_name);
//        cardNumber = (EditText) myDialog.findViewById(R.id.card_number);
//        cvv = (EditText) myDialog.findViewById(R.id.card_cvv);
//        expiryDate = (EditText) myDialog.findViewById(R.id.card_expiry_date);
//        mPrivacyCB = (CheckBox) myDialog.findViewById(R.id.privacy_policy);
//
//        Button saveButton = (Button) myDialog.findViewById(R.id.save_card);
//        saveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                savePaymentDetails(bid);
//            }
//        });
//        myDialog.show();
//
//    }

    private void showPrivacyDialog(){

        Dialog myDialog;
        myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.privacy_policy_layout);
        myDialog.setCancelable(true);
        myDialog.setTitle(R.string.terms_conditions);
        myDialog.show();
    }

    private boolean savePaymentDetails(Bid bid){
        name.setError(null);
        cardNumber.setError(null);
        cvv.setError(null);
        expiryDate.setError(null);
        mPrivacyCB.setError(null);
        mReceiverName.setError(null);
        mReceiverPhone.setError(null);

        // Store values at the time of the login attempt.
        String cardName = name.getText().toString();
        String cardNo = cardNumber.getText().toString();
        String cardCvv = cvv.getText().toString();
        String cardED = expiryDate.getText().toString();
        String recName = mReceiverName.getText().toString();
        String recPhone = mReceiverPhone.getText().toString();

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

        if(!cardSaved){

            //check privacy checkbox checked
            if(!mPrivacyCB.isChecked()){
                mPrivacyCB.setError(getString(R.string.error_field_required));
                focusView = mPrivacyCB;
                cancel = true;
            }

        }



        //check receiver name
        if(!isReceiverNameValid(recName)){
            mReceiverName.setError(getString(R.string.error_field_required));
            focusView = mReceiverName;
            cancel = true;
        }

        //check receiver phone
        if(!isPhoneValid(recPhone)){
            mReceiverPhone.setError(getString(R.string.error_field_required));
            focusView = mReceiverPhone;
            cancel = true;
        }


        if (cancel) {
            focusView.requestFocus();
        } else {

            Message message = new Message();
            message.setReceiverName(recName);
            message.setReceiverPhone(recPhone);
            message.setPickupAddress(item.getPickUpAddress());
            message.setDeliveryAddress(item.getDeliveryAddress());
            message.setDeliveryDateTime(item.getDate()+" "+item.getTime());
            message.setPosterContact(Constant.USER_INFO.getPhone());
            message.setStatus("New");

            if(!cardSaved) {
                PaymentCard paymentCard = new PaymentCard(cardName, cardNo, cardCvv, cardED);
                DatabaseConstant.DATABASE_REFERENCE.child(Constant.encodeKey(Constant.USER_INFO.getEmail()))
                        .child("card").push().setValue(paymentCard);
            }
            confirmPayment(bid, message);

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

    private boolean isReceiverNameValid(String rname){

        return rname.length() >= 3;
    }


    private boolean isPhoneValid(String phone){

        return phone.length() == 10;
    }


    private void confirmPayment(final Bid bid, final Message message){

        final Bid postChangeBid = bid;
        DatabaseConstant.DATABASE_REFERENCE.child(Constant.encodeKey(Constant.USER_INFO.getEmail())).child("listing").child(postChangeBid.getItemId())
                .child("status").setValue("Inactive");

        postChangeBid.setStatus("Confirmed");
        final Map<String, Object> postValues = postChangeBid.toMap();
        DatabaseConstant.DATABASE_REFERENCE.child(Constant.encodeKey(Constant.USER_INFO.getEmail())).child("listing").child(postChangeBid.getItemId())
                .child("bidder").child(postChangeBid.getId())
                .updateChildren(postValues, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError == null){
                    DatabaseConstant.DATABASE_REFERENCE.child(Constant.encodeKey(bid.getUserId())).child("bidding")
                            .child(postChangeBid.getId())
                            .updateChildren(postValues);

                    Log.i("dof","bid id"+postChangeBid.getId() +" "+postChangeBid.getItemId());
                    DatabaseConstant.DATABASE_REFERENCE.child(Constant.encodeKey(bid.getUserId())).child("bidding")
                            .child(postChangeBid.getId()).child("message").push().setValue(message);

                    Toast.makeText(getApplicationContext(),"Bidder Confirmed.",Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(getApplicationContext(),"Something went wrong. Try Again!",Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ListUpdater.updateListingStatus();
        Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
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