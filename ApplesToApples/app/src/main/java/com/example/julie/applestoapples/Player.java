package com.example.julie.applestoapples;

import java.util.List;

/**
 * Created by Evannnnn on 2/28/16.
 */
public class Player {
    String mUsername;
    int mPlayerID;
    String mGroupID;
    int mScore;
    List<Card> mCards = null;

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

}
