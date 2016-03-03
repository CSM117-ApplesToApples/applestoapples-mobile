package com.example.julie.applestoapples;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Julie on 2/29/16.
 */

public class ButtonAdapter extends BaseAdapter {
    List<Card> mCards;
    Player mplayer;
    Context mContext;

    public ButtonAdapter(List<Card> c, Context context, Player player){
        mplayer = player;
        mCards = c;
        mContext = context;
    }

    public int getCount(){
        return mCards.size();
    }
    public Card getItem(int position){
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
                    if(mplayer.isJudge == true) {
                        mplayer.selectCard(card.getText().toString());

                    }else{
                        mplayer.submitCard(card.getText().toString());
                    }

                    System.out.println(card.getText());



                }
            });

        }else{
            card = (Button) convertView;
        }
        card.setText(mCards.get(position).mName);
        return card;
    }


}