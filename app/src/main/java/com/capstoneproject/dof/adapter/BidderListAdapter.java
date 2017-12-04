package com.capstoneproject.dof.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstoneproject.dof.PaymentActivity;
import com.capstoneproject.dof.R;
import com.capstoneproject.dof.constants.Constant;
import com.capstoneproject.dof.model.Bid;
import com.capstoneproject.dof.model.Item;
import com.capstoneproject.dof.utility.ImageEncoderDecoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by mandeepkaur on 2017-11-14.
 */

public class BidderListAdapter extends BaseAdapter{

    List<Bid> list;
    List<String> bidderName;
    List<String> bidderImage;
    Item item;
    Context context;
    int count = 0;
    private static LayoutInflater inflater=null;

    public BidderListAdapter(Context context, List<Bid> list, List<String> bidderName, List<String> bidderImage, Item item ){

        List<Bid> newlist = new ArrayList<>();
        for(int  i = list.size()-1; i>=0;i--){
            newlist.add(list.get(i));
        }
        this.list = list;
        this.bidderName = bidderName;
        this.bidderImage = bidderImage;
        this.item = item;
        this.context = context;
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
        TextView bidder_name, bid_amount_charged, bid_status;
        ImageView user_profile_image;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Holder holder=new Holder();
        View view;
        view = inflater.inflate(R.layout.bidder_list_item, null);

        holder.bidder_name = (TextView) view.findViewById(R.id.bidder_name);
        holder.bid_amount_charged = (TextView) view.findViewById(R.id.bid_amount_charged);
        holder.bid_status = (TextView) view.findViewById(R.id.bid_status);
        holder.user_profile_image=(ImageView) view.findViewById(R.id.bidder_profile_image);
//
        holder.bidder_name.setText(" "+bidderName.get(position));
//        holder.pickupAdd.setText(list.get(position).getPickUpAddress());
//        holder.deliveryAdd.setText(list.get(position).getDeliveryAddress());
        holder.bid_amount_charged.setText("$"+String.valueOf(list.get(position).getBidAmount()));

        if(list.get(position).getStatus().equals("Pending")){
            holder.bid_status.setText("");
        }else if(list.get(position).getStatus().equals("Confirmed")){
            count++;
            holder.bid_status.setText(list.get(position).getStatus());
        }
        if(bidderImage.get(position) != ""){
            try {
                Bitmap imageBitmap = ImageEncoderDecoder.decodeFromFirebaseBase64(bidderImage.get(position));
                holder.user_profile_image.setImageBitmap(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            holder.user_profile_image.setImageResource(R.drawable.user);
            holder.user_profile_image.setColorFilter(Color.rgb(0, 128, 255));
        }

        if(count == 0 && item.getStatus().equals("Active")) {
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, PaymentActivity.class);
                    String bidderData = Constant.GSON.toJson(list.get(position));
                    intent.putExtra("bidder", bidderData);
                    String itemData = Constant.GSON.toJson(item);
                    intent.putExtra("itemData", itemData);
                    //((Activity)context).finish();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }
            });
        }
        return view;
    }



}