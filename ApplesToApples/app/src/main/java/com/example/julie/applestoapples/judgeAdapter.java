package com.example.julie.applestoapples;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.content.Context;

import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by Julie on 3/7/16.
 */
public class judgeAdapter extends BaseAdapter {
    ArrayList<String> mCards;
    String mGroupId;
    int mPlayerId;
    Context mContext;

    public judgeAdapter(ArrayList cards, Context c, String groupId, int playerId){
        mCards = cards;
        mGroupId = groupId;
        mPlayerId = playerId;
        mContext = c;
    }
    public int getCount(){
        return mCards.size();
    }
    public String getItem(int position){
        return mCards.get(position);
    }
    public long getItemId(int position){
        return 0;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        TextView card;
        if (convertView == null) {
            card = new Button(mContext);
            card.setLayoutParams(new GridView.LayoutParams(600, 100));
            card.setBackgroundColor(Color.parseColor("#FF6666"));
            card.setPadding(5, 2, 5, 2);
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setBackgroundColor(Color.parseColor("#7F0000"));
                    TextView card = (TextView) v;
                    System.out.println(card.getText());
                    selectWinningCard(card.getText().toString());
                    ((Activity) mContext).finish();
                }
            });

        }else{
            card = (Button) convertView;
        }
        card.setText(mCards.get(position));
        return card;
    }

    public boolean selectWinningCard(String cardText){
        Log.i("Judge", "Select card: " + cardText);
        boolean ret = false;
        try{
            final String request = "http://dev.mrerickruiz.com/ata/select?groupID="+mGroupId+"&playerID="+mPlayerId
                    +"&cardText="+ URLEncoder.encode(cardText, "UTF-8");

            JSONObject resp = new HttpThread().execute(request).get(10, TimeUnit.SECONDS);
            if(resp != null)
                return true;
        }
        catch(Exception e){
            e.printStackTrace();
        }


        return ret;
    }
}

