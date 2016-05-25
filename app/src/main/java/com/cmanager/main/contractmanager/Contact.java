package com.cmanager.main.contractmanager;

import android.net.Uri;

/**
 * Created by scode on 5/13/2016.
 */
public class Contact {
    private String _name,_phone,_email,_address;
    private Uri _imgUri;
    private int _id;

    public Contact(int id, String name, String phone, String email, String address, Uri imgUri){
        _id = id;
        _name=name;
        _phone=phone;
        _email=email;
        _address=address;
        _imgUri=imgUri;

    }

    public int getId(){
        return _id;
    }
    public String getName(){

        return _name;
    }
    public String getPhone(){

        return _phone;
    }
    public String getEmail(){

        return _email;
    }
    public String getAddress(){

        return _address;
    }
    public Uri getImgUri(){

        return _imgUri;
    }
}
