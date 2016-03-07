package com.example.julie.applestoapples;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Intent;
import org.json.JSONObject;
import org.w3c.dom.Text;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ResultsActivity extends AppCompatActivity {

    String groupId = "";
    int playerId;
    String greenCard;
    String redCard;
    String winner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupID");
        playerId = intent.getIntExtra("playerID", 0);


        TextView displayGroupId = (TextView) findViewById(R.id.groupIdView);
        displayGroupId.setText("GroupID: " + groupId);


        JSONObject resp = getGame();
        try {

            TextView green = (TextView) findViewById(R.id.resultGreen);
            //green.setText(resp.getString("GreenCard"));


                setResults(resp);
                TextView banner = (TextView) findViewById(R.id.banner);
                banner.setText("The winner is ");
                TextView winner = (TextView) findViewById(R.id.banner_winner);
                winner.setText(winner + "!");
                TextView red = (TextView) findViewById(R.id.resultRed);
                red.setText(redCard);
                TextView resultStatus = (TextView) findViewById(R.id.resultView);
                resultStatus.setVisibility(View.GONE);
                Button newGame = (Button) findViewById(R.id.new_game_button);
                newGame.setVisibility(View.VISIBLE);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setResults(JSONObject res){
        try {
            System.out.println(res);
            if (res.getBoolean("status")== false) {
                redCard = res.getString("WinningCard");
                winner = res.getString("Winner");

            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return;
    }



    public JSONObject getGame(){
        Log.i("ResultsActivity", "getGame");
        String url = "http://dev.mrerickruiz.com/ata/" +
                "game?groupID=" + groupId +
                "&playerID=" + playerId;

        JSONObject resp = null;
        try{
            resp =  new HttpThread().execute(url).get(10, TimeUnit.SECONDS);

        }catch(Exception e){
            e.printStackTrace();
        }
        //System.out.println(resp);
        return resp;
    }

    public void startNewGame(View v){

        JSONObject resp = null;
        String url = "http://dev.mrerickruiz.com/ata/" +
                "newgame?groupID=" + groupId + "&playerID=" + playerId;
        System.out.println(url);
        try{
            resp =  new HttpThread().execute(url).get(10, TimeUnit.SECONDS);
        }catch(Exception e){
            e.printStackTrace();
        }

        System.out.println(resp);
        finish();
        return;
    }
}
