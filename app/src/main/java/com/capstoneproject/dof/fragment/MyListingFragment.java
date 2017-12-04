package com.capstoneproject.dof.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.solver.SolverVariable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.capstoneproject.dof.AddNewListingActivity;
import com.capstoneproject.dof.LoginActivity;
import com.capstoneproject.dof.MainActivity;
import com.capstoneproject.dof.R;
import com.capstoneproject.dof.UserProfileActivity;
import com.capstoneproject.dof.adapter.MyListingAdapter;
import com.capstoneproject.dof.constants.Constant;
import com.capstoneproject.dof.constants.DatabaseConstant;
import com.capstoneproject.dof.constants.SharedPreferenceConstant;
import com.capstoneproject.dof.model.Item;
import com.capstoneproject.dof.model.User;
import com.capstoneproject.dof.utility.ListUpdater;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;
import static android.R.attr.fragment;
import static android.R.attr.password;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.media.CamcorderProfile.get;
import static com.capstoneproject.dof.constants.Constant.MY_LISTING;

/**
 * Created by mandeepkaur on 2017-11-17.
 */

public class MyListingFragment extends Fragment {

    private int mNum;
    private ListView mListView;
    private Button mAddListingButton;
    private MyListingAdapter mAdapter;
    private List<Item> mList = new ArrayList<Item>();
    private int currentPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_listing, container, false);
        mListView = (ListView) v.findViewById(R.id.my_listing_list);
        registerForContextMenu(mListView);
        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ListUpdater.updateListingStatus();
                        if(mAdapter != null) {
                            mAdapter.notifyDataSetChanged();
                            Intent i = new Intent(getActivity(), MainActivity.class);
                            getActivity().finish();
                            startActivity(i);
                        }

                        refreshLayout.setRefreshing( false );
                    }
                }, 2000);

            }
        });

        DatabaseConstant.DATABASE_REFERENCE.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String email = Constant.encodeKey(Constant.USER_INFO.getEmail());
                if (dataSnapshot.hasChild(email)){

                    for (DataSnapshot child: dataSnapshot.child(email).child("listing").getChildren()) {
                        mList.add(child.getValue(Item.class));

                        mAdapter = new MyListingAdapter(getActivity(), mList);
                        mListView.setAdapter(mAdapter);
                    }

                }else{
                    Toast.makeText(getActivity(), "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("dof",databaseError.getMessage());
            }

        });

        mAddListingButton = (Button)v.findViewById(R.id.add_new_listing);
        mAddListingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddNewListingActivity.class);
                getActivity().finish();

                startActivity(intent);

            }
        });



        return v;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_context, menu);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        currentPosition = info.position;


    }
    @Override
    public boolean onContextItemSelected(MenuItem item){
        int id = item.getItemId();
        switch(id){
            case R.id.action_delete:
                List<Item> newlist = new ArrayList<>();
                for(int  i = mList.size()-1; i>=0;i--){
                    newlist.add(mList.get(i));
                }

                Item itemForDel = newlist.get(currentPosition);
                DatabaseConstant.DATABASE_REFERENCE.child(Constant.encodeKey(Constant.USER_INFO.getEmail())).child("listing")
                        .child(itemForDel.getId()).removeValue();
                mAdapter.notifyDataSetChanged();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().finish();
                startActivity(intent);
                Toast.makeText(getActivity(),"Listing Deleted Successfully", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onContextItemSelected(item);

        }

    }



}
