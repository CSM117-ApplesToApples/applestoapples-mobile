package com.example.julie.applestoapples;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

public class MainActivity extends AppCompatActivity {
    Game mGame = null;
    Timer t;
    private String groupId;
    private int playerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Intent main = getIntent();
        Player player = main.getExtras().getParcelable("player");
        groupId = player.mGroupID;
        playerId = player.mPlayerID;
        this.mGame = new Game(player);

        JSONObject gameStatus = getGame();
        this.mGame.updateGame(gameStatus);

        TextView scoreView = (TextView) findViewById(R.id.scoreView);
        scoreView.setText("Score: " + player.mScore);

        if(this.mGame.GameInProgress ) {
            GridView grid = (GridView) findViewById(R.id.gridView);

            if (this.mGame.mIfJudge) {
                //TODO display submitted cards



            } else {
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

       if(this.mGame.GameInProgress)
        {
            displayCurrentGame();
        }
        else{
            displayGameResults();
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

    public void displayCurrentGame(){
        TextView currentGreenCard = (TextView) findViewById(R.id.greenCard);
        currentGreenCard.setText(this.mGame.greenCard);
        ImageView apple = (ImageView) findViewById(R.id.appleMainImage);
        apple.setImageResource(getResources().getIdentifier("greenapple", "drawable", getPackageName()));
        TextView banner = (TextView) findViewById(R.id.banner);
        if(this.mGame.mIfJudge)
            banner.setText("You are the judge.");
        else {
            TextView judge = (TextView) findViewById(R.id.judgeView);
            judge.setText("The judge is " + this.mGame.judgeName);
        }
    }
    public void displayGameResults(){
        TextView banner = (TextView) findViewById(R.id.banner);
        TextView results = (TextView) findViewById(R.id.greenCard);
        ImageView apple = (ImageView) findViewById(R.id.appleMainImage);
        apple.setImageResource(getResources().getIdentifier("greenapple", "drawable", getPackageName()));
        banner.setText("The Winner of this Round is " + this.mGame.winner + ".");
        results.setText(this.mGame.greenCard + " + " + this.mGame.winningCard );
    }

    public JSONObject getGame(){
        JSONObject ret = null;

        if(groupId == null | groupId == "")
            return ret;
        String url = "http://dev.mrerickruiz.com/ata/" +
                "game?groupID=" + groupId +
                "&playerID=" + playerId;
        try{
            ret =  new HttpThread().execute(url).get(10, TimeUnit.SECONDS);
        }catch(Exception e){
            e.printStackTrace();
        }
        return ret;
    }
}

