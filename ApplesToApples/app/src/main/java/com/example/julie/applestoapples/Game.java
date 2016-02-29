package com.example.julie.applestoapples;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Evannnnn on 2/28/16.
 */
public class Game {
    Boolean mIfJudge;
    Player mPlayer;

    public Game(Player player){
        Log.i("Game", "New Game");
        this.mPlayer = player;
        Log.i("Game", "player:" + mPlayer.mUsername);
        Log.i("Game", "PlayerID: " + player.mPlayerID);
        Log.i("Game", "Username: " + player.mUsername);
        Log.i("Game", "GroupID: " + player.mGroupID);
        Log.i("Game", "Score: " + player.mScore);
        for(int i = 0; i < player.mCards.size(); i++)
            Log.i("Game", "Card: " + player.mCards.get(i).mID + ", " + player.mCards.get(i).mName);
    }
}
