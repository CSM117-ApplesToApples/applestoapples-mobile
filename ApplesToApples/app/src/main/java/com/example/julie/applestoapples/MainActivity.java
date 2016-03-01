package com.example.julie.applestoapples;
import java.util.List;

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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity {
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

        //Update and get Game attributes to display
        getGame();

       if(this.mGame.GameInProgress)
        {
            displayCurrentGame();

        }
        else{
            Intent resultsPage = new Intent(this, ResultsActivity.class );
            resultsPage.putExtra("groupID", groupId);
            resultsPage.putExtra("winner", this.mGame.winner);
            resultsPage.putExtra("redCard", this.mGame.winningCard);
            resultsPage.putExtra("greenCard", this.mGame.greenCard);
            startActivity(resultsPage);
        }

    //TODO should only set timer after the player has submit a card
        //or if it is the judge, set timer until to check if all players have submitted a card.
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                boolean gameStatus = MainActivity.this.mGame.GameInProgress;
                MainActivity.this.getGame();
                if (gameStatus != MainActivity.this.mGame.GameInProgress) {
                    timer.purge();
                    MainActivity.this.finish();
                    MainActivity.this.startActivity(getIntent());
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 100000);
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
        getGame();
        displayCurrentGame();
    }

    public void displayCurrentGame(){

        TextView scoreView = (TextView) findViewById(R.id.scoreView);
        scoreView.setText("Score: " + player.mScore);

        TextView currentGreenCard = (TextView) findViewById(R.id.greenCard);
        currentGreenCard.setText(this.mGame.greenCard);
        TextView banner = (TextView) findViewById(R.id.banner);
        GridView grid = (GridView) findViewById(R.id.gridView);

            if (this.mGame.mIfJudge) {
                banner.setText("You are the judge.");
                grid.setVisibility(View.GONE);
                //TODO display submitted cards


            } else {
                TextView judge = (TextView) findViewById(R.id.judgeView);
                judge.setText("The judge is " + this.mGame.judgeName);

                grid.setVisibility(View.VISIBLE);
                grid.setAdapter(new ButtonAdapter(player.mCards, this));
                grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(MainActivity.this, position, Toast.LENGTH_SHORT).show();
                    }
                });

            }
    }


    public void getGame(){

        if(groupId == null | groupId == "")
            return;

        JSONObject resp = null;
        String url = "http://dev.mrerickruiz.com/ata/" +
                "game?groupID=" + groupId +
                "&playerID=" + playerId;
        try{
            resp =  new HttpThread().execute(url).get(10, TimeUnit.SECONDS);
        }catch(Exception e){
            e.printStackTrace();
        }

        this.mGame.updateGame(resp);
        return;
    }
}

