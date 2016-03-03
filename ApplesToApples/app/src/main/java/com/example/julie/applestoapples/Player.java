package com.example.julie.applestoapples;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Evannnnn on 2/28/16.
 */
public class Player implements Parcelable{
    String mUsername;
    int mPlayerID;
    String mGroupID;
    int mScore;
    List<Card> mCards = null;
    boolean isJudge = false;

    public Player(String username){
        mUsername = username;
    }

    public Player(int id, String username, String groupID, int score, List<Card> cards){
        mPlayerID = id;
        mUsername = username;
        mGroupID = groupID;
        mScore = score;
        mCards = cards;
    }

    protected Player(Parcel in) {
        mPlayerID = in.readInt();
        mUsername = in.readString();
        mGroupID = in.readString();
        mScore = in.readInt();
        mCards = in.createTypedArrayList(Card.CREATOR);
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mPlayerID);
        dest.writeString(mUsername);
        dest.writeString(mGroupID);
        dest.writeInt(mScore);
        dest.writeTypedList(mCards);
    }

    public boolean submitCard(String cardText){

        boolean ret = false;

        try{
        final String request = "http://dev.mrerickruiz.com/ata/submit?groupID="+mGroupID+"&playerID="+mPlayerID
                +"&cardText="+ URLEncoder.encode(cardText, "UTF-8");

            JSONObject resp = new HttpThread().execute(request).get(10, TimeUnit.SECONDS);
            JsonParser parser = new JsonParser();
            ret = parser.readSuccess(resp);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return ret;
    }

    public boolean selectCard(String cardText){
        boolean ret = false;

        try{
        final String request = "select?groupID="+mGroupID+"&playerID="+mPlayerID
                +"&cardText="+ URLEncoder.encode(cardText, "UTF-8");

            JSONObject resp = new HttpThread().execute(request).get(10, TimeUnit.SECONDS);
            JsonParser parser = new JsonParser();
            ret = parser.readSuccess(resp);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return ret;
    }
}
