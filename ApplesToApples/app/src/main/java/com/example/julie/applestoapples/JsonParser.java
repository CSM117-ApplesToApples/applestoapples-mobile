package com.example.julie.applestoapples;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evannnnn on 2/28/16.
 */
public class JsonParser {

    public Card readCard(JSONObject jsonObject) {
        Card ret = new Card();

        try{
            String cardsString = jsonObject.getString("Cards");
            cardsString = cardsString.substring(1, cardsString.length() - 1);
            String[] cards = cardsString.split("],");
            String cardLast = cards[cards.length-1];
            cardLast = cardLast.substring(2, cardLast.length() - 1);
            String[] card = cardLast.split(",");
            ret.mID = Integer.parseInt(card[0]);
            ret.mName = card[1].substring(2, card[1].length()-1);
            Log.i("JsonParser", "read Card: "+ret.mID+", "+ret.mName);

        }catch (Exception e){
            e.printStackTrace();
        }
        return ret;
    }

    public boolean readSuccess(JSONObject jsonObject) {
        boolean ret = false;
        try {
            ret = jsonObject.getBoolean("success");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public boolean readSuccess(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));

        try {
            reader.beginObject();
            if(reader.hasNext()){
                if(reader.nextName().equals("success"))
                    return reader.nextBoolean();
                return false;
            }
        }finally {
            reader.close();
        }
        return false;
    }

    public Player readPlayerInfo(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            Log.i("JsonParser", "readJsonStream");
            return readMessage(reader);
        } finally {
            reader.close();
        }
    }

    public Player readMessage(JsonReader reader) throws IOException {
        //Log.i("JsonParser", "readMessage");
        int playerID = -1;
        List<Card> cards = null;
        int score = -1;
        String groupID = null;
        String username = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("PlayerID")) {
                playerID = reader.nextInt();
                //Log.i("JsonParser", "readPlayerID: "+playerID);
            } else if (name.equals("Score")) {
                score = reader.nextInt();
                //Log.i("JsonParser", "readScore: "+ score);
            } else if (name.equals("GroupID")) {
                groupID = reader.nextString();
                //Log.i("JsonParser", "readGroupID: "+groupID);
            } else if (name.equals("Name")) {
                username = reader.nextString();
                //Log.i("JsonParser", "readUsername: "+username);
            } else if (name.equals("Cards"))
            {
                cards = readTuplesArray(reader);
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Player(playerID, username, groupID, score, cards);
    }

    public List readTuplesArray(JsonReader reader) throws IOException {
        //Log.i("JsonParser", "readTuplesArray");
        List<Card> cards = new ArrayList();

        reader.beginArray();
        while (reader.hasNext()) {
            cards.add(readCard(reader));
        }
        reader.endArray();
        return cards;
    }

    public Card readCard(JsonReader reader) throws IOException {
        //Log.i("JsonParser", "readCard");
        Card ret = new Card();
        reader.beginArray();
        ret.mID = reader.nextInt();
        ret.mName = reader.nextString();
        reader.endArray();

        return ret;
    }
}
