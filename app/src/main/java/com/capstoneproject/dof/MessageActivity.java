package com.capstoneproject.dof;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.capstoneproject.dof.adapter.MessageListAdapter;
import com.capstoneproject.dof.constants.Constant;
import com.capstoneproject.dof.constants.DatabaseConstant;
import com.capstoneproject.dof.model.Bid;
import com.capstoneproject.dof.model.Item;
import com.capstoneproject.dof.model.Message;
import com.capstoneproject.dof.utility.ListUpdater;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {

    ListView mMessageList;
    List<Message> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMessageList = (ListView) findViewById(R.id.message_list);
        DatabaseConstant.DATABASE_REFERENCE.child(Constant.encodeKey(Constant.USER_INFO.getEmail())).child("bidding")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                            if(child.hasChild("message")) {
                                Message message = null;
                                for (DataSnapshot ch : child.child("message").getChildren()) {
                                    message = ch.getValue(Message.class);
                                    list.add(message);
                                    if (message.getStatus().equals("New")) {
                                        DatabaseConstant.DATABASE_REFERENCE.child(Constant.encodeKey(Constant.USER_INFO.getEmail())).child("bidding")
                                        .child(child.getKey()).child("message").child(ch.getKey()).child("status").setValue("Old");

                                    }
                                    break;
                                }

                            }


                        }

                        MessageListAdapter adapter = new MessageListAdapter(getApplicationContext(),list);
                        mMessageList.setAdapter(adapter);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // showErrorDialog(databaseError.getMessage());
                    }

                });


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ListUpdater.updateListingStatus();
        Intent intent = new Intent(MessageActivity.this, MainActivity.class);
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


