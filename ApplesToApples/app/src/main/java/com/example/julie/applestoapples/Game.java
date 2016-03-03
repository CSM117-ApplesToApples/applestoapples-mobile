package com.example.julie.applestoapples;

import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
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
    List<Card> SubmittedCards;
    Boolean canSelect;


    public Game(Player player){
        this.winner = "";
        this.winningCard = "";
        this.judgeName = "";
        this.greenCard = "";
        this.mIfJudge = false;
        this.GameInProgress = true;
        this.mPlayer = player;
        this.SubmittedCards = null;
        this.canSelect = false;
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
        greenCard = response.getString("GreenCard");
        if (response.getBoolean("status")) {
            //Game is in progress
            GameInProgress = true;
           if (response.getBoolean("judge?")) {
                mIfJudge = true;
                canSelect = response.getBoolean("canSelect");

            } else {
                mIfJudge = false;
                judgeName = response.getString("CurrentJudgeName");

               //TODO assign player.mCards for new round

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
