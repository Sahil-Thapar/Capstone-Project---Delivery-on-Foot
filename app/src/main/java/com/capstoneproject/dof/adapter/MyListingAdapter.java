package com.capstoneproject.dof.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.capstoneproject.dof.BidDescriptionActivity;
import com.capstoneproject.dof.ListingDetailActivity;
import com.capstoneproject.dof.R;
import com.capstoneproject.dof.constants.Constant;
import com.capstoneproject.dof.model.Item;
import com.capstoneproject.dof.utility.ImageEncoderDecoder;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.capstoneproject.dof.utility.ImageEncoderDecoder.decodeFromFirebaseBase64;


/**
 * Created by mandeepkaur on 2017-11-14.
 */

public class MyListingAdapter extends BaseAdapter{

        List<Item> list;
        Context context;
        private static LayoutInflater inflater=null;

        public MyListingAdapter(Context mainActivity, List<Item> list){

            List<Item> newlist = new ArrayList<>();
            for(int  i = list.size()-1; i>=0;i--){
                newlist.add(list.get(i));
            }
            this.list = newlist;
            context = mainActivity;
            inflater = ( LayoutInflater )context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
           return list.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class Holder
        {
            TextView item, pickupAdd, deliveryAdd, status, bidCount;
            ImageView img_listing;
        }
        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {

            Holder holder=new Holder();
            View view;
            view = inflater.inflate(R.layout.list_item, null);

            holder.item = (TextView) view.findViewById(R.id.list_item);
            holder.pickupAdd = (TextView) view.findViewById(R.id.pickup_address);
            holder.deliveryAdd = (TextView) view.findViewById(R.id.delivery_address);
            holder.status = (TextView) view.findViewById(R.id.listing_status);
            holder.bidCount = (TextView) view.findViewById(R.id.bid_count);
            holder.img_listing=(ImageView) view.findViewById(R.id.listing_image);

            holder.item.setText(list.get(position).getName());
            holder.pickupAdd.setText("From : "+list.get(position).getPickUpAddress());
            holder.deliveryAdd.setText("To : "+list.get(position).getDeliveryAddress());
            holder.status.setText(list.get(position).getStatus());
            if(list.get(position).getStatus().equals("Inactive")){
                holder.status.setTextColor(Color.RED);
            }
            if(list.get(position).getBidder()!=null) {
                holder.bidCount.setText(String.valueOf(list.get(position).getBidder().size()));
            }else{
                holder.bidCount.setText("0");
            }
            Log.i("dof","bidder no = ");
            if(list.get(position).getImageUrl().equals("No Image Available")) {
                holder.img_listing.setImageDrawable(context.getResources().getDrawable(R.drawable.noimage));
            }else{
                try {
                    Bitmap imageBitmap = ImageEncoderDecoder.decodeFromFirebaseBase64(list.get(position).getImageUrl());
                    holder.img_listing.setImageBitmap(imageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //holder.bidCount.setText(list.get(position).getStatus());
            //Picasso.with(context).load(imageId[position]).into(holder.img_listing);


            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, ListingDetailActivity.class);
                    String itemData = Constant.GSON.toJson(list.get(position));
                    intent.putExtra("item", itemData);
                    ((Activity)context).finish();
                    context.startActivity(intent);

                }
            });

//            view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
//                @Override
//                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
//
//                    MenuInflater inflater = ((Activity)context).getMenuInflater();
//                    inflater.inflate(R.menu.menu_context, contextMenu);
//
//                }
//            });
//
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    parent.showContextMenuForChild(view);
                    return false;
                }
            });



            return view;
        }



    }