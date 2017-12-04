package com.capstoneproject.dof.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.author;

/**
 * Created by mandeepkaur on 2017-11-16.
 */

public class User {

    private String image;
    private String name;
    private String email;
    private String phone;
    private String password;
    private HashMap<String,Item> listing;
    private HashMap<String,Bid> bidding;

    public User(){

    }

    public User(String email, String name, String phone, String password){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;

    }

    public User(String image, String name, String email, String phone, String password, HashMap<String, Item> listing, HashMap<String, Bid> bidding) {
        this.image = image;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.listing = listing;
        this.bidding = bidding;
    }

    public User(String email, String name, String phone, String password, HashMap<String, Item> listing, HashMap<String, Bid> bidding) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.listing = listing;
        this.bidding = bidding;
    }

    public User(String email, String name, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public HashMap<String, Item> getListing() {
        return listing;
    }

    public void setListing(HashMap<String, Item> listing) {
        this.listing = listing;
    }


    public HashMap<String, Bid> getBidding() {
        return bidding;
    }

    public void setBidding(HashMap<String, Bid> bidding) {
        this.bidding = bidding;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("image",image);
        result.put("email", email);
        result.put("name", name);
        result.put("phone", phone);
//        result.put("password", password);
//        result.put("listing", listing);

        return result;
    }
}