package com.example.julie.applestoapples;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SelectActivity extends AppCompatActivity {
    String groupId = "";
    int playerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);


        Intent main = getIntent();
        groupId = main.getStringExtra("groupID");
        playerId = main.getIntExtra("playerID", 0);

        JSONObject resp;

        if(groupId == null | groupId == "")
            System.out.println("Error no groupId.");

        String url = "http://dev.mrerickruiz.com/ata/" +
                "game?groupID=" + groupId +
                "&playerID=" + playerId;

        TextView group = (TextView) findViewById(R.id.select_groupView);
        group.setText("GroupID: " + groupId);

        TextView banner = (TextView) findViewById(R.id.select_banner);
        banner.setText("Select the card that best represents");

        TextView green = (TextView) findViewById(R.id.select_greenWord);
        ArrayList<String> cards = new ArrayList<>();
        try{
            resp =  new HttpThread().execute(url).get(10, TimeUnit.SECONDS);
            green.setText(resp.getString("GreenCard"));
            cards = parseCards(resp.getString("CardsSubmitted"));
        }catch(Exception e){
            e.printStackTrace();
        }

        GridView grid = (GridView) findViewById(R.id.select_grid);
        grid.setAdapter(new judgeAdapter(cards, this, groupId, playerId));


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



}
