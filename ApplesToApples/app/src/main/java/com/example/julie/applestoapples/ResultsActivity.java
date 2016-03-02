package com.example.julie.applestoapples;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

public class ResultsActivity extends AppCompatActivity {

    String groupId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupID");

        TextView displayGroupId = (TextView) findViewById(R.id.groupIdView);
        displayGroupId.setText(groupId);

        TextView displayWinner = (TextView) findViewById(R.id.banner_winner);
        displayWinner.setText(intent.getStringExtra("winner") + " won!");

        TextView displayGreen = (TextView) findViewById(R.id.greenCard);
        displayGreen.setText(intent.getStringExtra("greenCard"));

        TextView displayRed = (TextView) findViewById(R.id.redCard);
        displayRed.setText(intent.getStringExtra("redCard"));


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
