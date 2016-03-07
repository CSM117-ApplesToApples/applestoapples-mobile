package com.example.julie.applestoapples;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
//import com.fasterxml.jackson.databind.ObjectMapper;

public class MainActivity extends AppCompatActivity {

    private Button scores;
    private PopupWindow scoreWindow;
    private LayoutInflater layoutInflater;
    private LinearLayout linearLayout;
    public ListView lv;

    Game mGame = null;
    Player player = null;
    private String groupId;
    private int playerId;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                TextView judge = (TextView) findViewById(R.id.judgeView);
                judge.setText("The judge is " + this.mGame.judgeName);
                grid.setVisibility(View.VISIBLE);
                grid.setAdapter(new ButtonAdapter(this.player.mCards, this, player));

            }
            else{
                //JUDGE VIEW
                banner.setText("You are the judge.");
                player.mCards = new ArrayList<>();
                grid.setAdapter(new ButtonAdapter(this.player.mCards, this, player));
                timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            JSONObject resp = MainActivity.this.getGame();
                            if (resp.getBoolean("canSelect") == true) {
                                timer.purge();
                                MainActivity.this.player.mCards = parseCards(resp.getString("CardsSubmitted"));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                timer.scheduleAtFixedRate(task, 0, 10000);
             }

            displayScores();


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

    public ArrayList<Card> parseCards(String cards){
        ArrayList<Card> mCards = new ArrayList<>();

        //ArrayList<String> cardText = new ArrayList<>(map.values());

        ArrayList<String> cardText = new ArrayList<>();
        cardText.add("Hi");
        cardText.add("Testing");
        for(int i = 0; i < cardText.size(); i++) {
            Card c = new Card();
            c.mID = i;
            c.mName = cardText.get(i);
            mCards.add(c);
        }
        return mCards;
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

    public void displayScores(){
        //START OF SCOREBOARD
        scores = (Button) findViewById(R.id.scoreboard_button);
        linearLayout = (LinearLayout) findViewById(R.id.linear);
        //ArrayList<HashMap<String, String>> mapList;


        scores.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                lv = (ListView) findViewById(R.id.scorelist);
                Map<String, Object> temp = Scores.getScores(groupId);
                HashMapAdapter adapter = new HashMapAdapter(temp);
                lv.setAdapter(adapter);

                layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.display_scores,null);

                scoreWindow = new PopupWindow(container, 800, 1000, true);
                scoreWindow.showAtLocation(linearLayout, Gravity.CENTER, 0,0);

                container.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        scoreWindow.dismiss();
                        return true;
                    }
                });
            }
        });
        //END OF SCOREBOARD
    }

}

