package com.example.julie.applestoapples;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class WaitingActivity extends AppCompatActivity {
    String groupId;
    int playerId;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);


        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupID");
        playerId = intent.getIntExtra("playerID", 0);
        String submittedCard = intent.getStringExtra("submittedCard");

        TextView displayGroupId = (TextView) findViewById(R.id.wait_groupView);
        displayGroupId.setText("GroupID: " + groupId);
        TextView banner = (TextView) findViewById(R.id.wait_bannerView);
        banner.setText("You submitted: " );
        TextView cardSelected = (TextView) findViewById(R.id.wait_cardSelected);
        cardSelected.setText(submittedCard);

        JSONObject resp = getGame();
        try {


            if (resp.getBoolean("status")) {

                timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        JSONObject res = getGame();
                        try {
                            if (res.getBoolean("status") == false) {
                                timer.purge();
                                timer.cancel();
                                finish();
//                                Intent wait = new Intent(getApplicationContext(), ResultsActivity.class);
//                                wait.putExtra("groupID", groupId);
//                                wait.putExtra("playerID", playerId);
//                                startActivity(wait);
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                    }
                };
                timer.scheduleAtFixedRate(task, 0, 10000);

            }
            else {
                //GO TO RESULTS PAGE
                Intent wait = new Intent(getApplicationContext(), ResultsActivity.class);
                wait.putExtra("groupID", groupId);
                wait.putExtra("playerID", playerId);
                startActivity(wait);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

}
