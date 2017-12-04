package com.capstoneproject.dof.adapter;

/**
 * Created by mandeepkaur on 2017-11-26.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstoneproject.dof.BidDescriptionActivity;
import com.capstoneproject.dof.CardDetailActivity;
import com.capstoneproject.dof.ListingDetailActivity;
import com.capstoneproject.dof.PaymentActivity;
import com.capstoneproject.dof.R;
import com.capstoneproject.dof.constants.Constant;
import com.capstoneproject.dof.model.Bid;
import com.capstoneproject.dof.model.Item;
import com.capstoneproject.dof.model.Message;
import com.capstoneproject.dof.model.PaymentCard;
import com.capstoneproject.dof.utility.ImageEncoderDecoder;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.capstoneproject.dof.utility.ImageEncoderDecoder.decodeFromFirebaseBase64;


/**
 * Created by mandeepkaur on 2017-11-14.
 */

public class MessageListAdapter extends BaseAdapter{

    List<Message> list;
    Context context;
    private static LayoutInflater inflater=null;


    public MessageListAdapter(Context context, List<Message> list){

        List<Message> newlist = new ArrayList<>();
        for(int  i = list.size()-1; i>=0;i--){
            newlist.add(list.get(i));
        }
        this.list = newlist;
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
        TextView recName, recPhone, pickupAdd, delvAdd, dateTime,posterContact;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Holder holder=new Holder();
        View view;
        view = inflater.inflate(R.layout.message_list_item, null);

        holder.recName = (TextView) view.findViewById(R.id.receiver_name);
        holder.recPhone = (TextView) view.findViewById(R.id.receiver_phone);
        holder.pickupAdd = (TextView) view.findViewById(R.id.pickup_address);
        holder.delvAdd = (TextView) view.findViewById(R.id.delivery_address);
        holder.dateTime = (TextView) view.findViewById(R.id.date_time);
        holder.posterContact = (TextView) view.findViewById(R.id.poster_contact);

        holder.posterContact.setText("Poster Contact - "+list.get(position).getPosterContact());
        holder.recName.setText("Receiver name - "+list.get(position).getReceiverName());
        holder.recPhone.setText("Receiver Phone - "+list.get(position).getReceiverPhone());
        holder.pickupAdd.setText("Pick Up Address - "+list.get(position).getPickupAddress());
        holder.delvAdd.setText("Delivery Address - "+list.get(position).getDeliveryAddress());
        holder.dateTime.setText("Expected delivery date and time - "+list.get(position).getDeliveryDateTime());

        return view;
    }



}