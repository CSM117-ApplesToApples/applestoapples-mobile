package com.example.julie.applestoapples;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
        getGame();

        if(this.mGame.GameInProgress) {

            TextView currentGreenCard = (TextView) findViewById(R.id.greenCard);
            currentGreenCard.setText(this.mGame.greenCard);
            TextView banner = (TextView) findViewById(R.id.banner);
            GridView grid = (GridView) findViewById(R.id.gridView);
            if (this.mGame.mIfJudge == false) {
                //PLAYER VIEW
                TextView judge = (TextView) findViewById(R.id.judgeView);
                judge.setText("The judge is " + this.mGame.judgeName);
                grid.setVisibility(View.VISIBLE);
                grid.setAdapter(new ButtonAdapter(this.player.mCards, this, player));
                /*grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(MainActivity.this, position, Toast.LENGTH_SHORT).show();
                    }
                });*/
            }
            else{
                //JUDGE VIEW
                banner.setText("You are the judge.");
                player.mCards = null;
                grid.setVisibility(View.GONE);

                timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        MainActivity.this.getGame();
                        if (MainActivity.this.mGame.canSelect == true) {
                            timer.purge();
                            displaySelectedCards();
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



    public void displaySelectedCards(){
        GridView grid = (GridView) findViewById(R.id.gridView);
        grid.setVisibility(View.VISIBLE);

        //TODO SET SELECTED CARD TO JUDGE's mCards
        //player.mCards =
        grid.setAdapter(new ButtonAdapter(this.player.mCards, this, player));
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, position, Toast.LENGTH_SHORT).show();
            }
        });


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

