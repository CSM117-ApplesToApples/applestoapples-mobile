package com.example.julie.applestoapples;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Evannnnn on 2/28/16.
 */
public class Card implements Parcelable{
    int mID;
    String mName;

    public Card(){}

    public Card(int id, String name){
        mID = id;
        mName = name;
    }

    protected Card(Parcel in) {
        mID = in.readInt();
        mName = in.readString();
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mID);
        dest.writeString(mName);
    }

}
