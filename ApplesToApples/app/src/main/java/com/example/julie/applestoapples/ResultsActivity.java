package com.example.julie.applestoapples;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ResultsActivity extends AppCompatActivity {
    Game mGame;
    Timer timer;
    String groupId = "";
    int playerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupID");
        playerId = intent.getIntExtra("playerID", 0);

        final String url = "http://dev.mrerickruiz.com/ata/" +
                "game?groupID=" + groupId +
                "&playerID=" + playerId;

        System.out.println(url);
//        getGame(url);
//
//        TextView displayGroupId = (TextView) findViewById(R.id.groupIdView);
//        displayGroupId.setText(groupId);
//
//        if(this.mGame.GameInProgress) {

//            timer = new Timer();
//            TimerTask task = new TimerTask() {
//                @Override
//                public void run() {
//                    boolean gameStatus = true;
//                    ResultsActivity.this.onRestart(url);
//                }
//            };
//            timer.scheduleAtFixedRate(task, 0, 100000);
//
//            //Show waiting for judge to pick winner
//            if(intent.getBooleanExtra("isJudge", false)){
//                TextView displayWinner = (TextView) findViewById(R.id.banner_winner);
//                displayWinner.setText("Fetching Results...");
//
//            }else{
//                TextView displayWinner = (TextView) findViewById(R.id.banner_winner);
//                displayWinner.setText("Waiting for Judge to choose the winner...");
//            }
//            TextView displayWinner = (TextView) findViewById(R.id.banner_winner);
//            displayWinner.setText(this.mGame.winner + " won!");
//
//            TextView displayGreen = (TextView) findViewById(R.id.greenCard);
//            displayGreen.setText(this.mGame.greenCard);
//
//            TextView displayRed = (TextView) findViewById(R.id.redCard);
//            displayRed.setText(this.mGame.winningCard);
//
//        }

//
//     else {
//
//            TextView displayWinner = (TextView) findViewById(R.id.banner_winner);
//            displayWinner.setText(this.mGame.winner + " won!");
//
//            TextView displayGreen = (TextView) findViewById(R.id.greenCard);
//            displayGreen.setText(this.mGame.greenCard);
//
//            TextView displayRed = (TextView) findViewById(R.id.redCard);
//            displayRed.setText(this.mGame.winningCard);
//
//        }
    }

    public void onRestart(String url){
        super.onRestart();
        getGame(url);


    }

    public void getGame(String url){


        JSONObject resp = null;

        try{
            resp =  new HttpThread().execute(url).get(10, TimeUnit.SECONDS);
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println(resp);
        this.mGame.updateGame(resp);
        return;
    }

    public void setNewGame(){
        if(groupId == "")
            return;
        JSONObject resp = null;
        String url = "http://dev.mrerickruiz.com/ata/" +
                "newgame?groupID=" + groupId;
        try{
            resp =  new HttpThread().execute(url).get(10, TimeUnit.SECONDS);
        }catch(Exception e){
            e.printStackTrace();
        }

        finish();
        return;
    }
}
