package com.capstoneproject.dof.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.capstoneproject.dof.MainActivity;
import com.capstoneproject.dof.R;
import com.capstoneproject.dof.adapter.BidListingAdapter;
import com.capstoneproject.dof.adapter.MyListingAdapter;
import com.capstoneproject.dof.constants.Constant;
import com.capstoneproject.dof.constants.DatabaseConstant;
import com.capstoneproject.dof.model.Item;
import com.capstoneproject.dof.utility.ListUpdater;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.os.Build.VERSION_CODES.M;


/**
 * Created by mandeepkaur on 2017-11-17.
 */

public class BidFragment extends Fragment {

    private int mNum;
    private ListView mListView;
    private BidListingAdapter mAdapter;
    private List<Item> mBiddingList = new ArrayList<Item>();
    private List<String> userKeyList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bid, container, false);
        mListView = (ListView) v.findViewById(R.id.bid_list);

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ListUpdater.updateListingStatus();
                        refreshLayout.setRefreshing( false );
                        Intent i = new Intent(getActivity(), MainActivity.class);
                        getActivity().finish();
                        startActivity(i);

                    }
                }, 2000);

            }
        });


        DatabaseConstant.DATABASE_REFERENCE.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userKey = Constant.encodeKey(Constant.USER_INFO.getEmail());
                for (DataSnapshot ref: dataSnapshot.getChildren()) {
                    if(!(ref.getKey().equals(userKey))) {
                        userKeyList.add(ref.getKey());
                    }
                }
                Log.i("dof","list size"+userKeyList.size());
                for(int i = 0; i<userKeyList.size(); i++){

                    for (DataSnapshot child: dataSnapshot.child(Constant.encodeKey(userKeyList.get(i))).child("listing").getChildren()) {
                        if(child.getValue(Item.class).getStatus().equals("Active")) {
                            Item item = child.getValue(Item.class);
                            item.setUserId(userKeyList.get(i));
                            mBiddingList.add(item);
                        }

                    }

                }
                mAdapter = new BidListingAdapter(getActivity(), mBiddingList);
                mListView.setAdapter(mAdapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
               Log.e("dof",databaseError.getMessage());
            }

        });

        return v;
    }
}
