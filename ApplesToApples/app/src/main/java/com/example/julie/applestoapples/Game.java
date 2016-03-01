package com.example.julie.applestoapples;

import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Evannnnn on 2/28/16.
 */
public class Game {
    Boolean mIfJudge;
    Boolean GameInProgress;
    Player mPlayer;
    String judgeName;
    String greenCard;
    String winner;
    String winningCard;


    public Game(Player player){
        this.mPlayer = player;
        Log.i("Game", "player:" + mPlayer.mUsername);
        Log.i("Game", "PlayerID: " + player.mPlayerID);
        Log.i("Game", "Username: " + player.mUsername);
        Log.i("Game", "GroupID: " + player.mGroupID);
        Log.i("Game", "Score: " + player.mScore);
        for(int i = 0; i < player.mCards.size(); i++)
            Log.i("Game", "Card: " + player.mCards.get(i).mID + ", " + player.mCards.get(i).mName);
    }

    //TODO update player's cards
    public void updateGame(JSONObject response){
    try {
        if (response.getBoolean("status")) {
            //Game is in progress
            GameInProgress = true;
            greenCard = response.getString("GreenCard");
            if (response.getBoolean("judge?")) {
                mIfJudge = true;

            } else {
                mIfJudge = false;
                judgeName = response.getString("CurrentJudgeName");
            }
        } else {
            //Round has ended
            GameInProgress = false;
            winner = response.getString("Winner");
            winningCard = response.getString("WinningCard");
        }
    }catch(Exception e){
        e.printStackTrace();
    }
        return;
    }
}
