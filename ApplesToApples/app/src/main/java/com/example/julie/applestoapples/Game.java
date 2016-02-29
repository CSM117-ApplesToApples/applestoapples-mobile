package com.example.julie.applestoapples;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Evannnnn on 2/28/16.
 */
public class Game {
    Boolean mIfJudge;
    Player mPlayer;
    String mGroupID;

    public Game(String username, String groupID){
        Log.i("Game", "New Game");
        this.mPlayer = new Player(username);
        this.mGroupID = groupID;
        Log.i("Game", "player:" + mPlayer.mUsername);
        Log.i("Game", "groupID:" + this.mGroupID);
    }
}
