package com.capstoneproject.dof;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.capstoneproject.dof.adapter.BidderListAdapter;
import com.capstoneproject.dof.constants.Constant;
import com.capstoneproject.dof.constants.DatabaseConstant;
import com.capstoneproject.dof.model.Bid;
import com.capstoneproject.dof.model.Item;
import com.capstoneproject.dof.model.User;
import com.capstoneproject.dof.utility.ImageEncoderDecoder;
import com.capstoneproject.dof.utility.ListUpdater;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListingDetailActivity extends AppCompatActivity {

    private TextView  mItemName, mItemDesc, mItemValue, mPickupAddress, mDeliveryAddress, mDateTime;
    private ImageView mImageView;
    private ListView mBidderList;
    private BidderListAdapter mAdapter;
    private List<Bid> mList = new ArrayList<Bid>();
    private List<String> mBidderNameArr = new ArrayList<>();
    private List<String> mBidderImageArr = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_detail);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mImageView = (ImageView) findViewById(R.id.item_image);
        mItemName = (TextView) findViewById(R.id.item_name);
        mItemValue = (TextView) findViewById(R.id.item_value);
        mDateTime = (TextView) findViewById(R.id.item_date_time);
        mBidderList = (ListView) findViewById(R.id.bidder_list);

        Bundle bundle = getIntent().getExtras();
        String itemData = bundle.getString("item");
        final Item item = Constant.GSON.fromJson(itemData, Item.class);
        if(item.getImageUrl().equals("No Image Available")){
            mImageView.setImageResource(R.drawable.noimage);
        }else{
            try {
                Bitmap imageBitmap = ImageEncoderDecoder.decodeFromFirebaseBase64(item.getImageUrl());
                mImageView.setImageBitmap(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mItemName.setText(item.getName());
        mItemValue.setText(String.valueOf(item.getValue()));
        mDateTime.setText(item.getDate()+" "+item.getTime());

        DatabaseConstant.DATABASE_REFERENCE.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String email = Constant.encodeKey(Constant.USER_INFO.getEmail());
                if (dataSnapshot.hasChild(email)){
                    for (DataSnapshot child: dataSnapshot.child(email).child("listing").child(item.getId()).child("bidder").getChildren()) {

                        Bid i = child.getValue(Bid.class);
                        if(i != null) {
                            String bidderName = dataSnapshot.child(Constant.encodeKey(i.getUserId())).child("name").getValue().toString();
                            String bidderImage = (dataSnapshot.child(Constant.encodeKey(i.getUserId())).child("image").getValue()) != null ?
                                    (dataSnapshot.child(Constant.encodeKey(i.getUserId())).child("image").getValue()).toString() : "";

                            mList.add(i);
                            mBidderNameArr.add(bidderName);
                            mBidderImageArr.add(bidderImage);
                        }

                    }

                    mAdapter = new BidderListAdapter(getApplicationContext(), mList, mBidderNameArr, mBidderImageArr, item );
                    mBidderList.setAdapter(mAdapter);

                }else{
                    Toast.makeText(getApplicationContext(), "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("dof",databaseError.getMessage());
            }

        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ListUpdater.updateListingStatus();
        Intent intent = new Intent(ListingDetailActivity.this, MainActivity.class);
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
