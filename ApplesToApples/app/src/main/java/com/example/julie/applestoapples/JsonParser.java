package com.example.julie.applestoapples;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evannnnn on 2/28/16.
 */
public class JsonParser {

    public Player readJsonStream(InputStream in) throws IOException {
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
