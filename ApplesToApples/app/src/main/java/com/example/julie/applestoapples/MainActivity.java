package com.example.julie.applestoapples;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;


public class MainActivity extends AppCompatActivity {

    Game mGame = null;
    Player player = null;
    private String groupId;
    private int playerId;
    private Timer timer;

    AlertDialog alertDialogScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent main = getIntent();
        player = main.getExtras().getParcelable("player");
        groupId = player.mGroupID;
        playerId = player.mPlayerID;
        this.mGame = new Game(player);

        TextView groupView = (TextView) findViewById(R.id.groupView);
        groupView.setText("GroupId: " + groupId);

        TextView scoreView = (TextView) findViewById(R.id.scoreView);
        scoreView.setText("Score: " + player.mScore);

        //Update and get Game attributes to display
        JSONObject resp = getGame();
        this.mGame.updateGame(resp);

        if(this.mGame.GameInProgress) {

            TextView currentGreenCard = (TextView) findViewById(R.id.greenCard);
            currentGreenCard.setText(this.mGame.greenCard);
            TextView banner = (TextView) findViewById(R.id.banner);
            final GridView grid = (GridView) findViewById(R.id.gridView);

            if (this.mGame.mIfJudge == false) {
                //PLAYER VIEW
                banner.setVisibility(View.GONE);
                TextView judge = (TextView) findViewById(R.id.judgeView);
                judge.setText("The judge is " + this.mGame.judgeName);
                grid.setVisibility(View.VISIBLE);
                grid.setAdapter(new ButtonAdapter(this.player.mCards, this, player));

            }
            else{
                //JUDGE VIEW
                banner.setText("You are the judge.");
                TextView bannerBottom = (TextView) findViewById(R.id.banner_two);
                bannerBottom.setText("Waiting for players' selections... ");

                timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            JSONObject resp = MainActivity.this.getGame();
                            if (resp.getBoolean("canSelect") == true) {
                                ArrayList<String> cards = parseCards(resp.getString("CardsSubmitted"));
                                if(cards.isEmpty()){
                                    System.out.println("No cards to display");
                                    return;
                                }else{

                                    System.out.println("Click to display cards chosen by players");
                                    updateJudgeView();
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                timer.scheduleAtFixedRate(task, 1000, 10000);
             }

            View.OnClickListener handler = new View.OnClickListener(){
                public void onClick(View v) {
                    switch (v.getId()) {

                        case R.id.scoreboard_button:
                            showPopUp();
                            break;
                    }
                }
            };

            findViewById(R.id.scoreboard_button).setOnClickListener(handler);

        }
        else{
            Intent resultsPage = new Intent(this, ResultsActivity.class );
            resultsPage.putExtra("isJudge", this.mGame.mIfJudge);
            resultsPage.putExtra("groupID", this.groupId);
            resultsPage.putExtra("playerID", this.playerId);
            startActivity(resultsPage);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRestart(){
        super.onRestart();
        System.out.println("restart main view");
        finish();
        Intent intent = getIntent();
        startActivity(intent);
    }

    public ArrayList<String> parseCards(String cards){

        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String,String>>(){}.getType();
        Map<String, String> map = gson.fromJson(cards, mapType);
        if(map == null)
            return new ArrayList<>();
        ArrayList<String> cardText = new ArrayList<>(map.values());


        return cardText;
    }


    public void updateJudgeView(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView banner = (TextView) findViewById(R.id.banner_two);
                banner.setVisibility(View.GONE);
                Button selectWinner = (Button) findViewById(R.id.select_winner_button);
                selectWinner.setVisibility(View.VISIBLE);
                selectWinner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.this.timer.purge();
                        MainActivity.this.timer.cancel();
                        Intent selectActivity = new Intent(getApplicationContext(), SelectActivity.class);
                        selectActivity.putExtra("groupID", MainActivity.this.groupId);
                        selectActivity.putExtra("playerID", MainActivity.this.playerId);
                        startActivity(selectActivity);
                    }
                });
            }
        });
    }


    public JSONObject getGame(){

        JSONObject resp = null;

        if(groupId == null | groupId == "")
            return resp;

        String url = "http://dev.mrerickruiz.com/ata/" +
                "game?groupID=" + groupId +
                "&playerID=" + playerId;
        try{
            resp =  new HttpThread().execute(url).get(10, TimeUnit.SECONDS);
        }catch(Exception e){
            e.printStackTrace();
        }

        return resp;
    }

    public void showPopUp(){

        // add items
        Map<String, String> temp = Scores.getScores(groupId);
        HashMapAdapter adapter = new HashMapAdapter(temp);

        // create a new ListView, set the adapter and item click listener
        ListView listViewItems = new ListView(this);
        listViewItems.setAdapter(adapter);
        listViewItems.setOnItemClickListener(new OnItemClickListenerListViewItem());

        // put the ListView in the pop up
        alertDialogScores = new AlertDialog.Builder(MainActivity.this)
                .setView(listViewItems)
                .setTitle("Scores")
                .show();

    }

}

