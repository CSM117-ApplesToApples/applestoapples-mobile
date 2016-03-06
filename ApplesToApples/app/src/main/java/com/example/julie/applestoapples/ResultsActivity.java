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

    Timer timer;
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
        TextView banner = (TextView) findViewById(R.id.banner);
        TextView resultView = (TextView) findViewById(R.id.resultView);

        if (intent.getBooleanExtra("isJudge", true) == false) {

            String submitted = intent.getStringExtra("submittedCard");
            banner.setText("You submitted: ");
            TextView submittedCard = (TextView) findViewById(R.id.banner_winner);
            submittedCard.setText(submitted);
            resultView.setText("Waiting for judge's results... ");
        }
        else{

            banner.setText("Fetching results... ");
        }


        JSONObject resp = getGame();
        try {

            TextView green = (TextView) findViewById(R.id.resultGreen);
            green.setText(resp.getString("GreenCard"));

            if (resp.getBoolean("status")) {

                    timer = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            JSONObject res = getGame();
                            try {
                                if (res.getBoolean("status") == false) {
                                    timer.purge();
                                    setResults(res);
                                    displayResults();
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }

                        }
                    };
                    timer.scheduleAtFixedRate(task, 0, 10000);

            }
            else {
                setResults(resp);
                displayResults();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setResults(JSONObject res){
        try {
            if (res.getBoolean("status")) {
                redCard = res.getString("WinningCard");
                winner = res.getString("Winner");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return;
    }

    public void displayResults(){

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

    public void setNewGame(View v){

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
